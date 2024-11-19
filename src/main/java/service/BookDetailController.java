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

                try {
                    // Nếu có URL hợp lệ, tải hình ảnh từ URL
                    Image bookImage = new Image(imageUrl, true); // 'true' để tải hình ảnh không đồng bộ
                    bookImageView.setImage(bookImage);
                } catch (IllegalArgumentException e) {
                    // Nếu URL không hợp lệ, hiển thị hình ảnh mặc định
                    System.out.println("Lỗi khi tải hình ảnh: " + e.getMessage());
                    bookImageView.setImage(loadDefaultImage());
                }
            } else {
                bookImageView.setImage(null);
                // Nếu không có imageUrl, sử dụng hình ảnh mặc định
                System.out.println("Không có imageUrl, sử dụng hình ảnh mặc định.");
                bookImageView.setImage(loadDefaultImage());
            }
        }
        // Phương thức giúp tải hình ảnh mặc định từ resources
        private Image loadDefaultImage() {
            return new Image(getClass().getResource("/image/book.png").toString());
        }
    }