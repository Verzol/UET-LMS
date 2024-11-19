package service;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BookDetailController {

    @FXML
    private Label bookTitle;

    @FXML
    private Label authorLabel;

    @FXML
    private Label publisherLabel;

    @FXML
    private Label publishedDateLabel;

    @FXML
    private Label ratingLabel;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ImageView bookImageView;

    public void setBookDetails(String title, String author, String publisher, String publishedDate,
                               String rating, String description, String imageUrl) {
        bookTitle.setText(title);
        authorLabel.setText("Author: " + author);
        publisherLabel.setText("Publisher: " + publisher);
        publishedDateLabel.setText("Published Date: " + publishedDate);
        ratingLabel.setText("Rating: " + rating);
        descriptionArea.setText(description);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image bookImage = new Image(imageUrl, true); // Tải không đồng bộ
                bookImageView.setImage(bookImage);
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi khi tải hình ảnh: " + e.getMessage());
                bookImageView.setImage(loadDefaultImage());
            }
        } else {
            bookImageView.setImage(loadDefaultImage());
        }
    }

    private Image loadDefaultImage() {
        return new Image(getClass().getResource("/image/book.png").toExternalForm());
    }
}
