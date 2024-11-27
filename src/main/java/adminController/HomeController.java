package adminController;

import DAO.DatabaseConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    @FXML
    private Label totalDocuments;

    @FXML
    private Label totalAvailableDocuments;

    @FXML
    private Label totalBorrowedDocuments;

    @FXML
    private Label totalTimesBorrowed;

    @FXML
    private ListView<TopBook> topBooksList;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private Connection connection;

    public HomeController() {
        connection = DatabaseConnection.getInstance().getConnection();
    }

    @FXML
    public void initialize() {
        updateDashboard();
    }

    private void updateDashboard() {
        loadStatistics();
        loadTopBooks();
    }

    private void loadStatistics() {
        Task<Void> statisticsTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int totalDocs = getTotalDocumentsCount();
                int totalAvailableDocs = getTotalAvailableDocuments();
                int totalBorrowedDocs = getTotalBorrowedDocuments();
                int totalTimes = getTotalTimesBorrowed();

                Platform.runLater(() -> {
                    totalDocuments.setText(String.valueOf(totalDocs));
                    totalAvailableDocuments.setText(String.valueOf(totalAvailableDocs));
                    totalBorrowedDocuments.setText(String.valueOf(totalBorrowedDocs));
                    totalTimesBorrowed.setText(String.valueOf(totalTimes));
                });

                return null;
            }
        };
        new Thread(statisticsTask).start();
    }

    private void loadTopBooks() {
        Task<List<TopBook>> topBooksTask = new Task<>() {
            @Override
            protected List<TopBook> call() throws Exception {
                return getTopBooks(10);
            }
        };

        topBooksTask.setOnSucceeded(event -> {
            List<TopBook> topBooks = topBooksTask.getValue();
            if (topBooks != null && !topBooks.isEmpty()) {
                Platform.runLater(() -> {
                    updateTopBooksList(topBooks);
                    updateBarChart(topBooks);
                });
            }
        });

        new Thread(topBooksTask).start();
    }

    private void updateTopBooksList(List<TopBook> topBooks) {
        topBooksList.setItems(FXCollections.observableArrayList(topBooks));
        topBooksList.setCellFactory(listView -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label titleLabel = new Label();
            private final Label timesBorrowedLabel = new Label();

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
                titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                timesBorrowedLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
            }

            @Override
            protected void updateItem(TopBook book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setGraphic(null);
                } else {
                    if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                        imageView.setImage(new Image(book.getImageUrl(), true));
                    } else {
                        imageView.setImage(null);
                    }
                    titleLabel.setText(book.getTitle());
                    timesBorrowedLabel.setText(book.getTimesBorrowed() + " times borrowed");
                    setGraphic(new HBox(10, imageView, new VBox(titleLabel, timesBorrowedLabel)));
                }
            }
        });
    }

    private void updateBarChart(List<TopBook> topBooks) {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (TopBook book : topBooks) {
            series.getData().add(new XYChart.Data<>(book.getTitle(), book.getTimesBorrowed()));
        }

        barChart.getData().add(series);

        yAxis.setUpperBound(10);
    }


    private int getTotalDocumentsCount() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM documents";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        }
        return 0;
    }

    private int getTotalAvailableDocuments() throws SQLException {
        String query = "SELECT SUM(quantity_in_stock) AS totalAvailable FROM documents";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("totalAvailable");
            }
        }
        return 0;
    }

    private int getTotalBorrowedDocuments() throws SQLException {
        String query = "SELECT SUM(borrowed_quantity) AS totalBorrowed FROM documents";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("totalBorrowed");
            }
        }
        return 0;
    }

    private int getTotalTimesBorrowed() throws SQLException {
        String query = "SELECT SUM(times_borrowed) AS totalTimes FROM documents";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("totalTimes");
            }
        }
        return 0;
    }

    private List<TopBook> getTopBooks(int limit) throws SQLException {
        List<TopBook> topBooks = new ArrayList<>();
        String query = "SELECT d.title, d.times_borrowed, b.image_url " +
                "FROM documents d " +
                "LEFT JOIN books b ON d.id = b.id " +
                "ORDER BY d.times_borrowed DESC LIMIT ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                int timesBorrowed = resultSet.getInt("times_borrowed");
                String imageUrl = resultSet.getString("image_url");

                topBooks.add(new TopBook(title, timesBorrowed, imageUrl));
            }
        }

        return topBooks;
    }

    public static class TopBook {
        private final String title;
        private final int timesBorrowed;
        private final String imageUrl;

        public TopBook(String title, int timesBorrowed, String imageUrl) {
            this.title = title;
            this.timesBorrowed = timesBorrowed;
            this.imageUrl = imageUrl;
        }

        public String getTitle() {
            return title;
        }

        public int getTimesBorrowed() {
            return timesBorrowed;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}