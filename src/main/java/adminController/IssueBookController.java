package adminController;

import javafx.scene.control.*;
import models.documents.BookError;
import controller.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.SessionManager;

import java.sql.*;

public class IssueBookController {

    @FXML
    private Button refreshButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button markAsResolvedButton;

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

    @FXML
    private TableColumn<BookError, Integer> colReportedByUserId;

    @FXML
    private TableColumn<BookError, String> colResolutionNotes;

    @FXML
    private Button addResolutionNotesButton;

    private ObservableList<BookError> errorList;
    private final Connection connection;

    public IssueBookController() {
        this.connection = new DatabaseConnection().getConnection();
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
        colReportedByUserId.setCellValueFactory(new PropertyValueFactory<>("reportedByUserId")); // New column
        colResolutionNotes.setCellValueFactory(new PropertyValueFactory<>("resolutionNotes")); // New column
    }

    private void loadErrorData() {
        errorList = FXCollections.observableArrayList();
        String query = "SELECT * FROM book_errors";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                BookError error = new BookError(
                        resultSet.getInt("id"),
                        resultSet.getString("document_id"),
                        resultSet.getString("error_description"),
                        resultSet.getDate("error_date"),
                        resultSet.getBoolean("fixed_status"),
                        resultSet.getDate("fixed_date"),
                        resultSet.getInt("reported_by_user_id"),
                        resultSet.getString("resolution_notes")
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
    private void markAsResolved() {
        BookError selectedError = errorTable.getSelectionModel().getSelectedItem();

        if (selectedError == null) {
            showErrorAlert("No item selected to mark as resolved!");
            return;
        }

        if (selectedError.isFixedStatus()) {
            showErrorAlert("This error is already marked as resolved.");
            return;
        }

        String updateQuery = "UPDATE book_errors SET fixed_status = true, fixed_date = CURRENT_DATE WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, selectedError.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                String updateDocumentQuery = "UPDATE documents SET hasIssue = false WHERE id = ?";
                try (PreparedStatement documentStatement = connection.prepareStatement(updateDocumentQuery)) {
                    documentStatement.setString(1, selectedError.getDocumentId());
                    documentStatement.executeUpdate();
                }

                loadErrorData();
                showSuccessAlert("Error marked as resolved successfully!");
            } else {
                showErrorAlert("Failed to mark the error as resolved.");
            }

        } catch (SQLException e) {
            showErrorAlert("Error while marking as resolved: " + e.getMessage());
        }
    }

    public void processReturnedBook(String documentId, boolean hasIssues, String errorDescription) {
        String updateQuery = "UPDATE documents SET hasIssue = ? WHERE id = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setBoolean(1, hasIssues);
            updateStmt.setString(2, documentId);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0 && hasIssues) {
                recordReturnError(documentId, errorDescription);
            } else if (rowsAffected > 0) {
                showSuccessAlert("Book returned successfully with no issues.");
            } else {
                showErrorAlert("Failed to update book return status.");
            }

        } catch (SQLException e) {
            showErrorAlert("Error during book return processing: " + e.getMessage());
        }
    }

    public void recordReturnError(String documentId, String errorDescription) {
        String insertErrorQuery = "INSERT INTO book_errors (document_id, error_description, error_date, fixed_status, fixed_date, reported_by_user_id) " +
                "VALUES (?, ?, CURRENT_DATE, false, NULL, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertErrorQuery)) {
            preparedStatement.setString(1, documentId);
            preparedStatement.setString(2, errorDescription);
            preparedStatement.setInt(3, SessionManager.getCurrentUserId()); // Lưu ID người dùng report
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                showSuccessAlert("Error reported successfully!");
                loadErrorData();
            } else {
                showErrorAlert("Failed to report the error.");
            }
        } catch (SQLException e) {
            showErrorAlert("Error during error recording: " + e.getMessage());
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

    @FXML
    private void handleAddResolutionNotes() {
        BookError selectedError = errorTable.getSelectionModel().getSelectedItem();

        if (selectedError == null) {
            showErrorAlert("No error selected!");
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Resolution Notes");
        dialog.setHeaderText("Add resolution notes for error: " + selectedError.getErrorDescription());
        dialog.setContentText("Resolution Notes:");

        dialog.showAndWait().ifPresent(notes -> {
            String updateQuery = "UPDATE book_errors SET resolution_notes = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, notes);
                preparedStatement.setInt(2, selectedError.getId());
                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    showSuccessAlert("Resolution notes updated successfully!");
                    loadErrorData();
                } else {
                    showErrorAlert("Failed to update resolution notes.");
                }
            } catch (SQLException e) {
                showErrorAlert("Error while updating resolution notes: " + e.getMessage());
            }
        });
    }
}
