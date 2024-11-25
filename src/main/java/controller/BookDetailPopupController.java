package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class BookDetailPopupController {

    @FXML
    private Label genreLabel;

    @FXML
    private Label pageCountLabel;

    @FXML
    private Label isbnLabel;

    @FXML
    private ImageView bookCoverImageView;

    @FXML
    private Label bookTitleLabel;

    @FXML
    private Label authorLabel;

    @FXML
    private Label quantityInStockLabel;

    @FXML
    private Label borrowedQuantityLabel;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button closeButton;

    @FXML
    private void closePopup() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // Method to set book details, including description
    public void setBookDetails(String title, String genre, int pageCount, String isbn, String imageUrl,
                               String author, int quantityInStock, int borrowedQuantity, String description) {
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
}
