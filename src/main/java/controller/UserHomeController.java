package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.concurrent.Task;
import service.BookDataService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
            loadBooksFromDatabase();
        } else {
            displayBooks();
        }
    }

    private void loadBooksFromDatabase() {
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String query = "SELECT id, genre, image_url FROM books";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    ResultSet resultSet = preparedStatement.executeQuery();

                    List<Node> booksToAdd = new ArrayList<>();
                    int row = 0;
                    int col = 0;
                    while (resultSet.next()) {
                        String genre = resultSet.getString("genre");
                        String imageUrl = resultSet.getString("image_url");

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Book.fxml"));
                        Node bookNode = loader.load();

                        BookController controller = loader.getController();
                        controller.setBookDetails(imageUrl, genre);

                        booksToAdd.add(bookNode);

                        col++;
                        if (col == 4) {
                            col = 0;
                            row++;
                        }
                    }

                    Platform.runLater(() -> {
                        for (int i = 0; i < booksToAdd.size(); i++) {
                            Node bookNode = booksToAdd.get(i);
                            int finalRow = i / 4;
                            int finalCol = i % 4;
                            bookContainer.add(bookNode, finalCol, finalRow);
                        }

                        BookDataService.setLoadedBooks(booksToAdd);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(loadTask).start();
    }

    private void displayBooks() {
        int row = 0;
        int col = 0;
        for (Node bookNode : BookDataService.getLoadedBooks()) {
            final int finalRow = row;
            final int finalCol = col;

            Platform.runLater(() -> bookContainer.add(bookNode, finalCol, finalRow));

            col++;
            if (col == 4) {
                col = 0;
                row++;
            }
        }
    }
}
