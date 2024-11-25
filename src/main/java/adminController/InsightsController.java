package adminController;

import controller.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label totalBooksLabel;

    @FXML
    private Label totalBorrowedBooksLabel;

    @FXML
    private Label totalIssuedBooksLabel;

    @FXML
    private void initialize() {
        try {
            System.out.println("Initializing InsightsController...");
            setupGenrePieChart();
            setupOverdueListView();
            setupTopBorrowersListView();
            setupBarChart();
            setupTotalStats();
        } catch (Exception e) {
            showError("Initialization Error", "An error occurred while initializing the dashboard: " + e.getMessage());
        }
    }

    public InsightsController() {
        this.connection = new DatabaseConnection().getConnection();
    }

    private void setupTotalStats() {
        String totalUsersQuery = "SELECT COUNT(*) AS total_users FROM person";
        try (PreparedStatement statement = connection.prepareStatement(totalUsersQuery);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int totalUsers = resultSet.getInt("total_users");
                totalUsersLabel.setText(String.format("%,d", totalUsers));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load total users: " + e.getMessage());
        }

        String totalBooksQuery = "SELECT SUM(quantity_in_stock) AS total_books FROM documents";
        try (PreparedStatement statement = connection.prepareStatement(totalBooksQuery);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int totalBooks = resultSet.getInt("total_books");
                totalBooksLabel.setText(String.format("%,d", totalBooks));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load total books: " + e.getMessage());
        }

        String totalBorrowedBooksQuery = "SELECT SUM(borrowed_quantity) AS total_borrowed_books FROM documents";
        try (PreparedStatement statement = connection.prepareStatement(totalBorrowedBooksQuery);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int totalBorrowedBooks = resultSet.getInt("total_borrowed_books");
                totalBorrowedBooksLabel.setText(String.format("%,d", totalBorrowedBooks));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load total borrowed books: " + e.getMessage());
        }

        String totalIssuedBooksQuery = """
            SELECT COUNT(*) AS total_issued_books
            FROM borrow_history bh
            WHERE bh.return_date IS NULL
        """;
        try (PreparedStatement statement = connection.prepareStatement(totalIssuedBooksQuery);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int totalIssuedBooks = resultSet.getInt("total_issued_books");
                totalIssuedBooksLabel.setText(String.format("%,d", totalIssuedBooks));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load total issued books: " + e.getMessage());
        }
    }

    private void setupGenrePieChart() {
        String query = "SELECT genre, COUNT(*) AS count FROM books GROUP BY genre";

        try (var statement = connection.createStatement();
             var resultSet = statement.executeQuery(query)) {

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String genre = resultSet.getString("genre");
                int count = resultSet.getInt("count");

                pieChartData.add(new PieChart.Data(genre, count));
            }

            genrePieChart.setData(pieChartData);
        } catch (Exception e) {
            showError("PieChart Error", "Failed to set up the genre PieChart: " + e.getMessage());
        }
    }


    private void setupOverdueListView() {
        ObservableList<String> overdueData = FXCollections.observableArrayList();

        String query = """
    SELECT p.username, COUNT(bh.id) AS overdue_count
    FROM borrow_history bh
    JOIN user u ON bh.user_id = u.id
    JOIN person p ON u.id = p.id
    WHERE bh.return_date < CURRENT_DATE AND bh.status = 0
    GROUP BY p.username
    """;

        try (PreparedStatement statement = this.connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int overdueCount = resultSet.getInt("overdue_count");
                overdueData.add(username + " - " + overdueCount + " Book(s) Overdue");
            }

            overdueListView.setItems(overdueData);

        } catch (SQLException e) {
            showError("Database Error", "Failed to load overdue data: " + e.getMessage());
        }
    }




    private void setupTopBorrowersListView() {
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

        try (PreparedStatement statement = this.connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int booksBorrowed = resultSet.getInt("books_borrowed");
                topBorrowersData.add(username + " - " + booksBorrowed + " Books Borrowed");
            }

            topBorrowersListView.setItems(topBorrowersData);

        } catch (SQLException e) {
            showError("Database Error", "Failed to load top borrowers data: " + e.getMessage());
        }
    }

    private void setupBarChart() {
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

            booksBorrowedBarChart.getData().clear();
            booksBorrowedBarChart.getData().add(series);
            booksBorrowedBarChart.getXAxis().setTickLabelRotation(45);

        } catch (SQLException e) {
            showError("BarChart Error", "Failed to load BarChart data: " + e.getMessage());
        }
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


    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
