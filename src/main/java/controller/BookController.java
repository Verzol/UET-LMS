package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    @FXML
    private Button detailButton;

    private String bookId;

    public void setBookDetails(String bookId, String title, String genre, int pageCount, String isbn,
                               String imageUrl, String author, int quantityInStock, int borrowedQuantity) {
        this.bookId = bookId;
        if (this.bookTitleLabel == null) {
            this.bookTitleLabel = new Label("Title1");
        } else if (this.bookTitleLabel.getText() == null || this.bookTitleLabel.getText().isEmpty()) {
            this.bookTitleLabel.setText("Title1");
        }

        bookTitleLabel.setText(title);

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

    @FXML
    public void handleDetailButtonAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BookDetailPopup.fxml"));
            Parent popupRoot = fxmlLoader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Book Details");
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Connection connection = new DatabaseConnection().getConnection();
            String query = """
                    SELECT d.title, b.genre, b.page_count, b.ISBN, b.image_url, 
                           d.author, d.quantity_in_stock, d.borrowed_quantity 
                    FROM books b 
                    INNER JOIN documents d ON b.id = d.id 
                    WHERE b.id = ?
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, this.bookId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                BookDetailPopupController popupController = fxmlLoader.getController();
                popupController.setBookDetails(
                        resultSet.getString("title"),
                        resultSet.getString("genre"),
                        resultSet.getInt("page_count"),
                        resultSet.getString("ISBN"),
                        resultSet.getString("image_url"),
                        resultSet.getString("author"),
                        resultSet.getInt("quantity_in_stock"),
                        resultSet.getInt("borrowed_quantity"),
                        null
                );
            }

            popupStage.setScene(new Scene(popupRoot, 450, 550));
            popupStage.showAndWait();

        } catch (IOException | RuntimeException e) {
            showErrorAlert("Error", "Unable to open the book details popup.");
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
