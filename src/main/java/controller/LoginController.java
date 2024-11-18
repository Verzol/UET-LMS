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

            String verifyLogin = "SELECT role FROM users WHERE username = ? AND password = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(verifyLogin)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet querySet = preparedStatement.executeQuery();

                if (querySet.next()) {
                    String role = querySet.getString("role");

                    if ("admin".equalsIgnoreCase(role)) {
                        loginMessageLabel.setText("Login Successful! Welcome Admin.");
                        openAdminDashboardScreen();
                    } else if ("user".equalsIgnoreCase(role)) {
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

            // Lấy controller của UserDashboard và gọi phương thức khởi tạo Home
            UserDashboardController userDashboardController = fxmlLoader.getController();
            userDashboardController.selectHome(); // Chọn Home khi chuyển màn hình

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
