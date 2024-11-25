package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import service.BookDataService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserHomeController {

    @FXML
    private GridPane bookContainer;

    @FXML
    private GridPane topBooksContainer;

    private Connection connection;

    @FXML
    private TextField searchTextField;  // Để lấy giá trị tìm kiếm

    @FXML
    private Button searchButton;  // Nút tìm kiếm

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

        preLoadTopBooks();
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
                       d.author, d.quantity_in_stock, d.borrowed_quantity, d.bookdescription 
                FROM books b 
                INNER JOIN documents d ON b.id = d.id 
                LIMIT 6
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Book.fxml"));
                Node bookNode = loader.load();

                BookController controller = loader.getController();
                controller.setBookDetails(
                        resultSet.getString("id"),
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

                bookNodes.add(bookNode);
            }
        } catch (IOException | SQLException e) {
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

    private void preLoadTopBooks() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                List<Node> topBookNodes = loadTopBorrowedBooksFromDatabase();
                Platform.runLater(() -> displayTopBooks(topBookNodes));
            } finally {
                executor.shutdown();
            }
        });
    }

    private List<Node> loadTopBorrowedBooksFromDatabase() {
        List<Node> topBookNodes = new ArrayList<>();
        String query = """
            SELECT b.id, d.title, b.genre, b.page_count, b.ISBN, b.image_url, 
                   d.author, d.quantity_in_stock, d.borrowed_quantity, d.bookdescription 
            FROM books b 
            INNER JOIN documents d ON b.id = d.id 
            ORDER BY d.borrowed_quantity DESC 
            LIMIT 3
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Book.fxml"));
                Node bookNode = loader.load();

                BookController controller = loader.getController();
                controller.setBookDetails(
                        resultSet.getString("id"),
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

                topBookNodes.add(bookNode);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return topBookNodes;
    }

    private void displayTopBooks(List<Node> topBookNodes) {
        topBooksContainer.getChildren().clear();
        int row = 0;

        for (Node topBookNode : topBookNodes) {
            topBooksContainer.add(topBookNode, 0, row); // Thêm vào cột 0, tăng dần hàng
            row++;
        }
    }

    @FXML
    public void handleSearch() {
        String searchQuery = searchTextField.getText().trim();

        if (!searchQuery.isEmpty()) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    List<Node> searchedBooks = searchBooks(searchQuery);
                    Platform.runLater(() -> {
                        displayBooks(searchedBooks);
                    });
                } finally {
                    executor.shutdown();
                }
            });
        }
    }

    private List<Node> searchBooks(String searchQuery) {
        List<Node> searchedBookNodes = new ArrayList<>();
        String query = """
                SELECT b.id, d.title, b.genre, b.page_count, b.ISBN, b.image_url, 
                       d.author, d.quantity_in_stock, d.borrowed_quantity, d.bookdescription 
                FROM books b 
                INNER JOIN documents d ON b.id = d.id 
                WHERE d.title LIKE ? OR d.author LIKE ?
                LIMIT 6
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchQuery + "%");
            preparedStatement.setString(2, "%" + searchQuery + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Book.fxml"));
                Node bookNode = loader.load();

                BookController controller = loader.getController();
                controller.setBookDetails(
                        resultSet.getString("id"),
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

                searchedBookNodes.add(bookNode);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return searchedBookNodes;
    }
}