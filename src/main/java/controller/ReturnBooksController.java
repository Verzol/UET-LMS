package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import models.documents.Book;
import models.documents.BookError;
import utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReturnBooksController {

    @FXML
    public Button buttonViewErrorDetails;

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
    private TableColumn<Book, Integer> columnReportedByUserId;

    @FXML
    private TableColumn<Book, String> columnResolutionNotes;

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

        columnReportedByUserId.setCellValueFactory(data -> {
            BookError bookError = data.getValue().getBookError();
            if (bookError != null) {
                return new SimpleObjectProperty<>(bookError.getReportedByUserId());
            }
            return new SimpleObjectProperty<>(null);
        });

        columnResolutionNotes.setCellValueFactory(data -> {
            BookError bookError = data.getValue().getBookError();
            if (bookError != null) {
                return new SimpleStringProperty(bookError.getResolutionNotes());
            }
            return new SimpleStringProperty("");
        });

        loadBorrowedBooks();
    }

    private void loadBorrowedBooks() {
        String query = "SELECT d.id AS book_id, d.title, d.author, b.status, " +
                "e.reported_by_user_id, e.resolution_notes " +
                "FROM borrow_history b " +
                "JOIN documents d ON b.document_id = d.id " +
                "LEFT JOIN book_errors e ON d.id = e.document_id " +
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

                int reportedByUserId = resultSet.getInt("reported_by_user_id");
                String resolutionNotes = resultSet.getString("resolution_notes");

                if (reportedByUserId > 0 || resolutionNotes != null) {
                    BookError bookError = new BookError();
                    bookError.setReportedByUserId(reportedByUserId);
                    bookError.setResolutionNotes(resolutionNotes);
                    book.setBookError(bookError);
                }

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
            try {
                String checkErrorQuery = "SELECT COUNT(*) AS errorCount FROM book_errors WHERE document_id = ? AND fixed_status = false";
                try (PreparedStatement checkErrorStmt = connection.prepareStatement(checkErrorQuery)) {
                    checkErrorStmt.setString(1, selectedBook.getId());
                    ResultSet resultSet = checkErrorStmt.executeQuery();

                    if (resultSet.next() && resultSet.getInt("errorCount") > 0) {
                        showError("Unresolved Issue", "This book has unresolved issues. Please contact the administrator.");
                        return;
                    }
                }

                String updateBorrowHistoryQuery = "UPDATE borrow_history SET status = 1 WHERE document_id = ? AND user_id = ? AND status = 0";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateBorrowHistoryQuery)) {
                    preparedStatement.setString(1, selectedBook.getId());
                    preparedStatement.setInt(2, SessionManager.getCurrentUserId());
                    int rowsUpdated = preparedStatement.executeUpdate();

                    if (rowsUpdated > 0) {
                        String updateDocumentQuery = "UPDATE documents SET quantity_in_stock = quantity_in_stock + 1 WHERE id = ?";
                        try (PreparedStatement updateDocumentStmt = connection.prepareStatement(updateDocumentQuery)) {
                            updateDocumentStmt.setString(1, selectedBook.getId());
                            updateDocumentStmt.executeUpdate();
                        }

                        showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully!");
                        loadBorrowedBooks();
                    } else {
                        showError("Error", "Failed to return the book. Please try again.");
                    }
                }
            } catch (SQLException e) {
                showError("Database Error", "An error occurred while returning the book: " + e.getMessage());
            }
        } else {
            showError("No Selection", "Please select a book to return.");
        }
    }

    @FXML
    public void handleReportError(ActionEvent actionEvent) {
        Book selectedBook = tableBorrowedBooks.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to report an error.");
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Report Error");
        dialog.setHeaderText("Report an issue with the book: " + selectedBook.getTitle());

        Label label = new Label("Error Description:");
        TextField errorDescriptionField = new TextField();
        errorDescriptionField.setPromptText("Enter the error description...");

        VBox dialogContent = new VBox(10, label, errorDescriptionField);
        dialogContent.setStyle("-fx-padding: 20;");
        dialog.getDialogPane().setContent(dialogContent);

        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return errorDescriptionField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(errorDescription -> {
            if (errorDescription == null || errorDescription.trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Error description cannot be empty.");
                return;
            }

            // Cập nhật ID người dùng báo lỗi
            String insertErrorQuery = "INSERT INTO book_errors (document_id, error_description, error_date, fixed_status, fixed_date, reported_by_user_id) " +
                    "VALUES (?, ?, CURRENT_DATE, false, NULL, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertErrorQuery)) {
                preparedStatement.setString(1, selectedBook.getId());
                preparedStatement.setString(2, errorDescription);
                preparedStatement.setInt(3, SessionManager.getCurrentUserId()); // Lấy ID người dùng từ SessionManager
                int rowsInserted = preparedStatement.executeUpdate();

                if (rowsInserted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Error reported successfully!");
                    loadBorrowedBooks(); // Cập nhật lại bảng
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to report the error. Please try again.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error while reporting: " + e.getMessage());
            }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleViewErrorDetails() {
        Book selectedBook = tableBorrowedBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to view error details.");
            return;
        }

        String query = "SELECT * FROM book_errors WHERE document_id = ? AND fixed_status = false";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, selectedBook.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String errorDescription = resultSet.getString("error_description");
                int reportedByUserId = resultSet.getInt("reported_by_user_id");
                String resolutionNotes = resultSet.getString("resolution_notes");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error Details");
                alert.setHeaderText("Error details for document: " + selectedBook.getTitle());
                alert.setContentText("Description: " + errorDescription +
                        "\nReported By: " + reportedByUserId +
                        "\nResolution Notes: " + (resolutionNotes != null ? resolutionNotes : "N/A"));
                alert.showAndWait();
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No Error", "No unresolved errors found for this book.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while fetching error details: " + e.getMessage());
        }
    }
}
