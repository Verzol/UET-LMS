package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
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
    private FlowPane bookListFlowPane;

    @FXML
    private FlowPane topBooksFlowPane;

    @FXML
    private TextField searchTextField;

    private Connection connection;
    private ExecutorService executor;

    public UserHomeController() {
        connection = new DatabaseConnection().getConnection();
        executor = Executors.newFixedThreadPool(5);
    }

    @FXML
    public void initialize() {
        executor.submit(this::loadBooksInBackground);
        executor.submit(this::loadTopBooksInBackground);

        if (BookDataService.getLoadedBooks().isEmpty()) {
            loadBooksInBackground();
        } else {
            displayBooksInFlowPane(BookDataService.getLoadedBooks(), bookListFlowPane);
        }

        searchTextField.setOnKeyReleased(this::handleSearchInput);
    }

    private void loadBooksInBackground() {
        executor.submit(() -> {
            List<Node> bookNodes = loadBooksFromDatabase(12);
            Platform.runLater(() -> {
                BookDataService.setLoadedBooks(bookNodes);
                displayBooksInFlowPane(bookNodes, bookListFlowPane);
            });
        });
    }

    private void loadTopBooksInBackground() {
        executor.submit(() -> {
            List<Node> topBookNodes = loadTopBorrowedBooksFromDatabase();
            Platform.runLater(() -> displayBooksInFlowPane(topBookNodes, topBooksFlowPane));
        });
    }

    private List<Node> loadBooksFromDatabase(int limit) {
        List<Node> bookNodes = new ArrayList<>();
        String query = """
                SELECT b.id, d.title, b.genre, b.page_count, b.ISBN, b.image_url, 
                       d.author, d.quantity_in_stock, d.borrowed_quantity, d.bookdescription 
                FROM books b 
                INNER JOIN documents d ON b.id = d.id 
                LIMIT ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, limit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Node bookNode = createBookNode(resultSet);
                bookNodes.add(bookNode);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return bookNodes;
    }

    private List<Node> loadTopBorrowedBooksFromDatabase() {
        List<Node> topBookNodes = new ArrayList<>();
        String query = """
            SELECT b.id, d.title, b.genre, b.page_count, b.ISBN, b.image_url, 
                   d.author, d.quantity_in_stock, d.borrowed_quantity, d.bookdescription 
            FROM books b 
            INNER JOIN documents d ON b.id = d.id 
            ORDER BY d.borrowed_quantity DESC 
            LIMIT 6
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Node bookNode = createBookNode(resultSet);
                topBookNodes.add(bookNode);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return topBookNodes;
    }

    private Node createBookNode(ResultSet resultSet) throws IOException, SQLException {
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

        bookNode.setUserData(controller);
        return bookNode;
    }

    private void displayBooksInFlowPane(List<Node> bookNodes, FlowPane flowPane) {
        Platform.runLater(() -> {
            flowPane.getChildren().clear();
            flowPane.getChildren().addAll(bookNodes);
        });
    }

    @FXML
    public void handleSearchInput(KeyEvent event) {
        String searchQuery = searchTextField.getText().trim();

        if (!searchQuery.isEmpty()) {
            executor.submit(() -> {
                List<Node> searchedBooks = searchBooks(searchQuery);
                Platform.runLater(() -> displayBooksInFlowPane(searchedBooks, bookListFlowPane));
            });
        } else {
            executor.submit(this::loadBooksInBackground);
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
                LIMIT 12
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + searchQuery + "%");
            preparedStatement.setString(2, "%" + searchQuery + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Node bookNode = createBookNode(resultSet);
                searchedBookNodes.add(bookNode);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return searchedBookNodes;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
