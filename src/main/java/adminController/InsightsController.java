package adminController;

import DAO.DatabaseConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsightsController {

    private final Connection connection;

    @FXML
    private Label alertLabel;

    @FXML
    private PieChart genrePieChart;

    @FXML
    private ListView<String> overdueListView;

    @FXML
    private ListView<String> topBorrowersListView;

    @FXML
    private BarChart<String, Number> booksBorrowedBarChart;

    public InsightsController() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.executor = Executors.newSingleThreadExecutor();
    }

    @FXML
    private void initialize() {
        loadDashboardData();
    }

    private final ExecutorService executor;

    private void loadDashboardData() {
        Task<Void> loadDataTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ObservableList<PieChart.Data> pieChartData = getGenrePieChartData();
                ObservableList<String> overdueData = getOverdueBorrowingRecords();
                ObservableList<String> topBorrowersData = getTopBorrowers();
                XYChart.Series<String, Number> barChartData = getBooksBorrowedChartData();

                Platform.runLater(() -> {
                    genrePieChart.setData(pieChartData);
                    overdueListView.setItems(overdueData);
                    topBorrowersListView.setItems(topBorrowersData);
                    booksBorrowedBarChart.getData().clear();
                    booksBorrowedBarChart.getData().add(barChartData);
                    booksBorrowedBarChart.getXAxis().setTickLabelRotation(45);
                });

                return null;
            }
        };

        loadDataTask.setOnFailed(event -> {
            Throwable exception = loadDataTask.getException();
            exception.printStackTrace();
            Platform.runLater(() -> alertLabel.setText("Error loading data!"));
        });

        executor.submit(loadDataTask);
    }

    private ObservableList<PieChart.Data> getGenrePieChartData() throws SQLException {
        String query = "SELECT genre, COUNT(*) AS count FROM books GROUP BY genre";
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String genre = resultSet.getString("genre");
                int count = resultSet.getInt("count");
                pieChartData.add(new PieChart.Data(genre, count));
            }
        }
        return pieChartData;
    }

    private ObservableList<String> getOverdueBorrowingRecords() throws SQLException {
        ObservableList<String> overdueData = FXCollections.observableArrayList();
        String query = """
            SELECT p.username, COUNT(bh.id) AS overdue_count
            FROM borrow_history bh
            JOIN user u ON bh.user_id = u.id
            JOIN person p ON u.id = p.id
            WHERE bh.return_date < CURRENT_DATE AND bh.status = 0
            GROUP BY p.username
        """;
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int overdueCount = resultSet.getInt("overdue_count");
                overdueData.add(username + " - " + overdueCount + " Book(s) Overdue");
            }
        }
        return overdueData;
    }

    private ObservableList<String> getTopBorrowers() throws SQLException {
        ObservableList<String> topBorrowersData = FXCollections.observableArrayList();
        String query = """
            SELECT p.username, COUNT(bh.id) AS books_borrowed
            FROM borrow_history bh
            JOIN user u ON bh.user_id = u.id
            JOIN person p ON u.id = p.id
            GROUP BY p.username
            ORDER BY books_borrowed DESC
            LIMIT 10
        """;
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int booksBorrowed = resultSet.getInt("books_borrowed");
                topBorrowersData.add(username + " - " + booksBorrowed + " Books Borrowed");
            }
        }
        return topBorrowersData;
    }

    private XYChart.Series<String, Number> getBooksBorrowedChartData() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Books Borrowed by Month");
        String query = """
            SELECT month_table.month_num, COALESCE(borrowed_data.total_borrowed, 0) AS total_borrowed
            FROM (
                SELECT 1 AS month_num UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL
                SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL
                SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL
                SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
            ) AS month_table
            LEFT JOIN (
                SELECT MONTH(borrow_date) AS month_num, COUNT(*) AS total_borrowed
                FROM borrow_history
                GROUP BY MONTH(borrow_date)
            ) AS borrowed_data
            ON month_table.month_num = borrowed_data.month_num
            ORDER BY month_table.month_num ASC
        """;
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int month = resultSet.getInt("month_num");
                int totalBorrowed = resultSet.getInt("total_borrowed");
                String monthName = getMonthName(month);
                series.getData().add(new XYChart.Data<>(monthName, totalBorrowed));
            }
        }
        return series;
    }

    private String getMonthName(int month) {
        return switch (month) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Unknown";
        };
    }
}
