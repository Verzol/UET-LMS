package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Hyperlink;
import utils.SessionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private Button loginButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField accountField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink registerLink;

    @FXML
    public void openRegisterScreen(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Register.fxml"));
            Parent registerRoot = fxmlLoader.load();

            Scene currentScene = loginButton.getScene();
            currentScene.setRoot(registerRoot);

        } catch (IOException e) {
            showErrorAlert("Error", "Unable to open the register screen.");
            e.printStackTrace();
        }
    }

    @FXML
    public void cancelButtonAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void loginButtonAction(ActionEvent event) {
        String username = accountField.getText().trim();
        String password = passwordField.getText().trim();

        if (!username.isEmpty() && !password.isEmpty()) {
            validateLogin(username, password);
        } else {
            loginMessageLabel.setText("Please enter a valid username and password!");
        }
    }

    public void validateLogin(String username, String password) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection connection = databaseConnection.getConnection()) {
            if (connection == null) {
                loginMessageLabel.setText("Database connection failed!");
                return;
            }

            String verifyLogin = """
                SELECT p.id, p.username, u.id AS user_id, a.id AS admin_id
                FROM person p
                LEFT JOIN user u ON p.id = u.id
                LEFT JOIN admin a ON p.id = a.id
                WHERE p.username = ? AND p.password = ?
            """;

            try (PreparedStatement preparedStatement = connection.prepareStatement(verifyLogin)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int personId = resultSet.getInt("id");
                    boolean isAdmin = resultSet.getObject("admin_id") != null;
                    boolean isUser = resultSet.getObject("user_id") != null;

                    // Lưu thông tin người dùng vào SessionManager
                    SessionManager.setCurrentUserId(personId);
                    SessionManager.setCurrentUsername(username);

                    if (isAdmin) {
                        loginMessageLabel.setText("Login Successful! Welcome Admin.");
                        openAdminDashboardScreen();
                    } else if (isUser) {
                        loginMessageLabel.setText("Login Successful! Welcome User.");
                        openUserDashboardScreen();
                    } else {
                        loginMessageLabel.setText("Invalid role assigned.");
                    }
                } else {
                    loginMessageLabel.setText("Login Failed! Incorrect username or password.");
                }
            }
        } catch (Exception e) {
            showErrorAlert("Database Error", "An error occurred while validating login.");
            e.printStackTrace();
        }
    }

    private void openAdminDashboardScreen() {
        switchToScene("/adminfxml/Dashboard.fxml", "Unable to open the admin dashboard screen.");
    }

    private void openUserDashboardScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/UserDashboard.fxml"));
            Parent dashboardRoot = fxmlLoader.load();

            // Gửi thông tin người dùng vào UserDashboardController
            UserDashboardController userDashboardController = fxmlLoader.getController();
            userDashboardController.selectHome();

            Scene currentScene = loginButton.getScene();
            currentScene.setRoot(dashboardRoot);

        } catch (IOException e) {
            showErrorAlert("Error", "Unable to open the user dashboard screen.");
            e.printStackTrace();
        }
    }

    private void switchToScene(String fxmlPath, String errorMessage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = fxmlLoader.load();

            Scene currentScene = loginButton.getScene();
            currentScene.setRoot(newRoot);

        } catch (IOException e) {
            showErrorAlert("Error", errorMessage);
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
