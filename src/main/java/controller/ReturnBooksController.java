package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.documents.Book;
import utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReturnBooksController {

    @FXML
    private TableView<Book> tableBorrowedBooks;

    @FXML
    private TableColumn<Book, String> columnBookId;
    @FXML
    private TableColumn<Book, String> columnTitle;
    @FXML
    private TableColumn<Book, String> columnAuthor;
    @FXML
    private TableColumn<Book, String> columnBorrowStatus;

    @FXML
    private Button buttonReturnBook;

    private Connection connection;

    public ReturnBooksController() {
        this.connection = new DatabaseConnection().getConnection();
    }

    @FXML
    private void initialize() {
        columnBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        columnBorrowStatus.setCellValueFactory(new PropertyValueFactory<>("borrowStatus"));

        loadBorrowedBooks();
    }

    private void loadBorrowedBooks() {
        String query = "SELECT b.document_id AS book_id, d.title, d.author, b.borrow_date, b.return_date, b.status " +
                "FROM borrow_history b " +
                "JOIN documents d ON b.document_id = d.id " +
                "WHERE b.status = 0 AND b.user_id = ?";

        ObservableList<Book> data = FXCollections.observableArrayList();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, SessionManager.getCurrentUserId());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getString("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setBorrowStatus("Borrowed");
                data.add(book);
            }

            tableBorrowedBooks.setItems(data);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load borrowed books: " + e.getMessage());
        }
    }

    @FXML
    private void handleReturnBook() {
        Book selectedBook = tableBorrowedBooks.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            String query = "UPDATE borrow_history SET status = 1 WHERE document_id = ? AND user_id = ? AND status = 0";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, selectedBook.getId());
                preparedStatement.setInt(2, SessionManager.getCurrentUserId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully!");
                    loadBorrowedBooks();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Error", "Failed to return the book. Please try again.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error while returning the book: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to return.");
        }
    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}