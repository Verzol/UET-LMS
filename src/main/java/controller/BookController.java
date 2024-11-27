package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookController {

    @FXML
    private Label bookTitleLabel;

    @FXML
    private ImageView bookCoverImageView;

    private String bookId;

    @FXML
    public void initialize() {
        // Add double-click event to bookCoverImageView
        bookCoverImageView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                handleDetailClick();
            }
        });
    }

    public void setBookDetails(String bookId, String title, String genre, int pageCount, String isbn,
                               String imageUrl, String author, int quantityInStock, int borrowedQuantity, String bookdescription) {
        this.bookId = bookId;
        bookTitleLabel.setText(title != null && !title.isEmpty() ? title : "Unknown Title");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                bookCoverImageView.setImage(new Image(imageUrl));
            } catch (Exception e) {
                bookCoverImageView.setImage(new Image("/images/default_book_cover.png"));
            }
        } else {
            bookCoverImageView.setImage(new Image("/images/default_book_cover.png"));
        }
    }

    public void handleDetailClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BookDetailPopup.fxml"));
            Parent popupRoot = fxmlLoader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Book Details");
            popupStage.initModality(Modality.APPLICATION_MODAL);

            BookDetailPopupController popupController = fxmlLoader.getController();
            popupController.setDocumentId(this.bookId);

            try (Connection connection = new DatabaseConnection().getConnection()) {
                String query = """
                        SELECT d.title, b.genre, b.page_count, b.ISBN, b.image_url, 
                               d.author, d.quantity_in_stock, d.borrowed_quantity, d.bookdescription
                        FROM books b 
                        INNER JOIN documents d ON b.id = d.id 
                        WHERE b.id = ?
                        """;
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, this.bookId);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            popupController.setBookDetails(
                                    resultSet.getString("title"),
                                    resultSet.getString("genre"),
                                    resultSet.getInt("page_count"),
                                    resultSet.getString("ISBN"),
                                    resultSet.getString("image_url"),
                                    resultSet.getString("author"),
                                    resultSet.getInt("quantity_in_stock"),
                                    resultSet.getInt("borrowed_quantity"),
                                    resultSet.getString("bookdescription")
                            );
                        }
                    }
                }
            }

            popupStage.setScene(new Scene(popupRoot, 810, 600));
            popupStage.showAndWait();

        } catch (IOException | SQLException e) {
            showErrorAlert("Error", "Unable to open the book details popup.");
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
