package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookDetailPopupController {

    @FXML
    private Label genreLabel, pageCountLabel, isbnLabel, bookTitleLabel, authorLabel, quantityInStockLabel, borrowedQuantityLabel, ratingLabel;

    @FXML
    private ImageView bookCoverImageView;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button closeButton, rate1StarButton, rate2StarButton, rate3StarButton, rate4StarButton, rate5StarButton;

    @FXML
    private Label getRatingLabel, totalRate;

    private int currentRating = 0;
    private String documentId;

    private Connection connection;

    public BookDetailPopupController() {
        this.connection = new DatabaseConnection().getConnection();
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
        updateRatingStatistics(documentId);
    }

    @FXML
    private void closePopup() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRating(int stars) {
        currentRating = stars;
        ratingLabel.setText("You rated: " + stars + " star(s).");
        int userId = SessionManager.getCurrentUserId();

        if (saveRatingToDatabase(documentId, userId, stars)) {
            System.out.println("Rating saved successfully for document: " + documentId);
            updateRatingStatistics(documentId);
        } else {
            System.out.println("Failed to save rating. Please try again.");
        }
    }

    private boolean saveRatingToDatabase(String documentId, int userId, int ratingValue) {
        String insertQuery = "INSERT INTO ratings (document_id, user_id, rating_value) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, documentId);
            statement.setInt(2, userId);
            statement.setInt(3, ratingValue);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Failed to save rating: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void initialize() {
        rate1StarButton.setOnAction(e -> handleRating(1));
        rate2StarButton.setOnAction(e -> handleRating(2));
        rate3StarButton.setOnAction(e -> handleRating(3));
        rate4StarButton.setOnAction(e -> handleRating(4));
        rate5StarButton.setOnAction(e -> handleRating(5));
    }

    public void setBookDetails(String title, String genre, int pageCount, String isbn, String imageUrl, String author, int quantityInStock, int borrowedQuantity, String description) {
        bookTitleLabel.setText(title != null ? title : "N/A");
        genreLabel.setText("Genre: " + (genre != null ? genre : "N/A"));
        pageCountLabel.setText("Page Count: " + (pageCount > 0 ? pageCount : "N/A"));
        isbnLabel.setText("ISBN: " + (isbn != null ? isbn : "N/A"));
        authorLabel.setText("Author: " + (author != null ? author : "N/A"));
        quantityInStockLabel.setText("In Stock: " + quantityInStock);
        borrowedQuantityLabel.setText("Borrowed: " + borrowedQuantity);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                bookCoverImageView.setImage(new Image(imageUrl));
            } catch (Exception e) {
                bookCoverImageView.setImage(new Image("/images/default_book_cover.png"));
            }
        } else {
            bookCoverImageView.setImage(new Image("/images/default_book_cover.png"));
        }

        descriptionTextArea.setText(description != null && !description.isEmpty() ? description : "No description available.");
    }

    private void updateRatingStatistics(String documentId) {
        String query = "SELECT AVG(rating_value) AS average_rating, COUNT(*) AS rating_count " +
                "FROM ratings WHERE document_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, documentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    double averageRating = resultSet.getDouble("average_rating");
                    int ratingCount = resultSet.getInt("rating_count");

                    if (ratingCount > 0) {
                        getRatingLabel.setText(String.format("Average Rating: %.2f", averageRating));
                        totalRate.setText(String.format("Total Ratings: %d", ratingCount));
                    } else {
                        getRatingLabel.setText("No ratings yet.");
                        totalRate.setText("No ratings yet.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
