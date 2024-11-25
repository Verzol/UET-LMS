package adminController;

import controller.DatabaseConnection;
import DAO.BookError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IssueBookController {

    @FXML
    private Button refreshButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<BookError> errorTable;

    @FXML
    private TableColumn<BookError, Integer> colId;

    @FXML
    private TableColumn<BookError, String> colDocumentId;

    @FXML
    private TableColumn<BookError, String> colErrorDescription;

    @FXML
    private TableColumn<BookError, java.sql.Date> colErrorDate;

    @FXML
    private TableColumn<BookError, Boolean> colFixedStatus;

    @FXML
    private TableColumn<BookError, java.sql.Date> colFixedDate;

    private ObservableList<BookError> errorList;
    private final Connection connection;

    public IssueBookController() {
        this.connection = new DatabaseConnection().getConnection(); // Khởi tạo kết nối
    }

    @FXML
    public void initialize() {
        setupColumns();
        loadErrorData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDocumentId.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        colErrorDescription.setCellValueFactory(new PropertyValueFactory<>("errorDescription"));
        colErrorDate.setCellValueFactory(new PropertyValueFactory<>("errorDate"));
        colFixedStatus.setCellValueFactory(new PropertyValueFactory<>("fixedStatus"));
        colFixedDate.setCellValueFactory(new PropertyValueFactory<>("fixedDate"));
    }

    @FXML
    private void loadErrorData() {
        errorList = FXCollections.observableArrayList();
        String query = "SELECT * FROM book_errors";  // Câu lệnh SQL lấy tất cả lỗi

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                BookError error = new BookError(
                        resultSet.getInt("id"),
                        resultSet.getString("document_id"),
                        resultSet.getString("error_description"),
                        resultSet.getDate("error_date"),
                        resultSet.getBoolean("fixed_status"),
                        resultSet.getDate("fixed_date")
                );
                errorList.add(error);
            }

            errorTable.setItems(errorList);

        } catch (SQLException e) {
            showErrorAlert("Failed to load error data: " + e.getMessage());
        }
    }

    @FXML
    private void refreshTable() {
        loadErrorData();
    }

    @FXML
    private void deleteError() {
        BookError selectedError = errorTable.getSelectionModel().getSelectedItem();

        if (selectedError == null) {
            showErrorAlert("No item selected to delete!");
            return;
        }

        String query = "DELETE FROM book_errors WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, selectedError.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                loadErrorData();
                showSuccessAlert("Error deleted successfully!");
            } else {
                showErrorAlert("Failed to delete the selected error.");
            }

        } catch (SQLException e) {
            showErrorAlert("Error while deleting: " + e.getMessage());
        }
    }

    @FXML
    private void addError(String documentId, String errorDescription) {
        // Kiểm tra xem sách có tồn tại và có lỗi hay không
        String checkQuery = "SELECT hasIssue FROM documents WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, documentId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                boolean hasIssue = rs.getBoolean("hasIssue");

                if (!hasIssue) {
                    showErrorAlert("The document does not have an issue to report.");
                    return;
                }

                // Thêm lỗi vào bảng book_errors nếu sách có lỗi
                String insertQuery = "INSERT INTO book_errors (document_id, error_description, error_date, fixed_status, fixed_date) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, documentId);
                    insertStmt.setString(2, errorDescription);
                    insertStmt.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now()));
                    insertStmt.setBoolean(4, false);
                    insertStmt.setDate(5, null);      // Ngày sửa chưa có

                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showSuccessAlert("Error added successfully!");
                        loadErrorData();
                    } else {
                        showErrorAlert("Failed to add error.");
                    }
                }

            } else {
                showErrorAlert("Document ID not found.");
            }

        } catch (SQLException e) {
            showErrorAlert("Error while adding error: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
