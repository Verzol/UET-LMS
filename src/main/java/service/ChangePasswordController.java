package service;

import DAO.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordController {

    @FXML
    private Label usernameLabel;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void initialize() {

        String currentUser = UserSession.getUsername();
        if (currentUser != null && !currentUser.isEmpty()) {
            usernameLabel.setText(currentUser);
        } else {
            showAlert("Error", "No user is currently logged in.", Alert.AlertType.ERROR);
            closeWindow();
        }
    }

    @FXML
    private void handleChangePassword() {
        String currentUser = UserSession.getUsername();

        if (!UserSession.isUserLoggedIn()) {
            showAlert("Error", "No user is currently logged in.", Alert.AlertType.ERROR);
            return;
        }

        String oldPassword = oldPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Warning", "Please fill all fields.", Alert.AlertType.WARNING);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "New password and confirm password do not match.", Alert.AlertType.ERROR);
            return;
        }

        String oldPasswordHash = oldPassword;
        String newPasswordHash = newPassword;

        if (!checkOldPassword(currentUser, oldPasswordHash)) {
            showAlert("Error", "Old password is incorrect.", Alert.AlertType.ERROR);
            return;
        }

        if (updatePassword(currentUser, newPasswordHash)) {
            showAlert("Success", "Password updated successfully!", Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            showAlert("Error", "Failed to update password. Please try again.", Alert.AlertType.ERROR);
        }
    }


    private void closeWindow() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close();
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private boolean updatePassword(String username, String newPasswordHash) {
        String query = "UPDATE person SET password = ? WHERE username = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newPasswordHash);
            preparedStatement.setString(2, username);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean checkOldPassword(String username, String oldPasswordHash) {
        String query = "SELECT 1 FROM person WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, oldPasswordHash);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}
