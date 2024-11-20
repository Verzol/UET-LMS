package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BookController {

    @FXML
    private Label bookTitle;

    @FXML
    private ImageView bookCoverImageView;

    public void setBookDetails(String imageUrl, String title) {
        bookTitle.setText(title);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image bookCoverImage = new Image(imageUrl);
            bookCoverImageView.setImage(bookCoverImage);
        } else {
            bookCoverImageView.setImage(new Image("default_book_cover.png"));
        }
    }
}
