package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import service.BookDataService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserHomeController {

    @FXML
    private GridPane bookContainer;

    private Connection connection;

    public UserHomeController() {
        connection = new DatabaseConnection().getConnection();
    }

    @FXML
    public void initialize() {
        if (BookDataService.getLoadedBooks().isEmpty()) {
            preLoadBooks();
        } else {
            displayBooks(BookDataService.getLoadedBooks());
        }
    }

    private void preLoadBooks() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            try {
                List<Node> bookNodes = loadBooksFromDatabase();
                Platform.runLater(() -> {
                    BookDataService.setLoadedBooks(bookNodes);
                    displayBooks(bookNodes);
                });
            } finally {
                executor.shutdown();
            }
        });
    }

    private List<Node> loadBooksFromDatabase() {
        List<Node> bookNodes = new ArrayList<>();
        String query = """
                SELECT b.id, d.title, b.genre, b.page_count, b.ISBN, b.image_url, 
                       d.author, d.quantity_in_stock, d.borrowed_quantity 
                FROM books b 
                INNER JOIN documents d ON b.id = d.id 
                LIMIT 6
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                int pageCount = resultSet.getInt("page_count");
                String isbn = resultSet.getString("ISBN");
                String imageUrl = resultSet.getString("image_url");
                String author = resultSet.getString("author");
                int quantityInStock = resultSet.getInt("quantity_in_stock");
                int borrowedQuantity = resultSet.getInt("borrowed_quantity");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Book.fxml"));
                Node bookNode = loader.load();

                BookController controller = loader.getController();
                controller.setBookDetails(id, title, genre, pageCount, isbn, imageUrl, author, quantityInStock, borrowedQuantity);

                bookNodes.add(bookNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookNodes;
    }

    private void displayBooks(List<Node> bookNodes) {
        bookContainer.getChildren().clear();
        int row = 0, col = 0;

        for (Node bookNode : bookNodes) {
            bookContainer.add(bookNode, col, row);
            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }
}
