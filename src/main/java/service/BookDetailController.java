package service;

import javafx.fxml.FXML;
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

    /**
     * Cập nhật thông tin chi tiết của sách.
     *
     * @param title         Tiêu đề sách.
     * @param author        Tác giả.
     * @param publisher     Nhà xuất bản.
     * @param publishedDate Năm xuất bản.
     * @param rating        Đánh giá.
     * @param description   Mô tả sách.
     * @param imageUrl      URL hình ảnh sách.
     */
    public void setBookDetails(String title, String author, String publisher, String publishedDate,
                                String rating, String description,  String imageUrl) {
        bookTitle.setText(title);
        authorLabel.setText("Author: " + author);
        publisherLabel.setText("Publisher: " + publisher);
        publishedDateLabel.setText("Published Date: " + publishedDate);
        ratingLabel.setText("Rating: " + rating);
        descriptionArea.setText(description);


        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image bookImage = new Image(imageUrl, true);
            bookImageView.setImage(bookImage);
        } else {
            bookImageView.setImage(null);
        }
    }
}
