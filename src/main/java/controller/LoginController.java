package controller;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
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
            showFadeMessage("Please enter a valid username and password!", true);
        }
    }

    public void validateLogin(String username, String password) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection connection = databaseConnection.getConnection()) {
            if (connection == null) {
                showFadeMessage("Database connection failed!", true);
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

                    SessionManager.setCurrentUserId(personId);
                    SessionManager.setCurrentUsername(username);

                    if (isAdmin) {
                        showFadeMessage("Login Successful! Welcome Admin.", false);
                        openAdminDashboardScreen();
                    } else if (isUser) {
                        showFadeMessage("Login Successful! Welcome User.", false);
                        openUserDashboardScreen();
                    } else {
                        showFadeMessage("Invalid role assigned.", true);
                    }
                } else {
                    showFadeMessage("Login Failed! Incorrect username or password.", true);
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showFadeMessage(String message, boolean isError) {
        loginMessageLabel.setText(message);
        loginMessageLabel.setStyle(isError ? "-fx-text-fill: #ff0000;" : "-fx-text-fill: #00a000;");

        Scene currentScene = loginButton.getScene();

        // Set background color of loginMessageLabel only
        loginMessageLabel.setStyle("-fx-background-color: " + (isError ? "#ff0000" : "#00ff00") + ";");

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), loginMessageLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), loginMessageLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);
        fadeOut.setDelay(Duration.seconds(2));

        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeIn.play();
    }
}
