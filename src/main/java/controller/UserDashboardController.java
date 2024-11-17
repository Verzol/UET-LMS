package controller;

import DAO.BookDAO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.documents.Book;
import java.util.List;

public class UserDashboardController {

    @FXML
    private ImageView bookCoverImageView;

    public void displayBookCover(String isbn) {
        BookDAO bookDAO = new BookDAO();
        List<Book> books = bookDAO.getAllBooks();

        for (Book book : books) {
            if (book.getISBN().equals(isbn)) {
                String imageUrl = book.getImageUrl();
                Image bookCoverImage = new Image(imageUrl, true);
                bookCoverImageView.setImage(bookCoverImage);
                break;
            }
        }
    }
}
