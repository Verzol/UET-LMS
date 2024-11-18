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

import javax.swing.*;
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

    private void openDashboardScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/adminfxml/Dashboard.fxml"));
            Parent dashboardRoot = fxmlLoader.load();

            Scene currentScene = loginButton.getScene();
            currentScene.setRoot(dashboardRoot);

            Stage stage = (Stage) currentScene.getWindow();

            final double[] dragAnchorX = new double[1];
            final double[] dragAnchorY = new double[1];

            dashboardRoot.setOnMousePressed(event -> {
                dragAnchorX[0] = event.getSceneX();
                dragAnchorY[0] = event.getSceneY();
            });

            dashboardRoot.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - dragAnchorX[0]);
                stage.setY(event.getScreenY() - dragAnchorY[0]);
            });

        } catch (IOException e) {
            showErrorAlert("Error", "Unable to open the dashboard screen.");
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
        if (!accountField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Please enter valid account and password!");
        }
    }

    public void validateLogin() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        try (Connection connection = databaseConnection.getConnection()) {
            if (connection == null) {
                loginMessageLabel.setText("Database connection failed!");
                return;
            }

            String verifyLogin = "SELECT count(1) FROM users WHERE username = ? AND password = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(verifyLogin)) {
                preparedStatement.setString(1, accountField.getText());
                preparedStatement.setString(2, passwordField.getText());
                ResultSet querySet = preparedStatement.executeQuery();

                if (querySet.next() && querySet.getInt(1) == 1) {
                    loginMessageLabel.setText("Login Successful!");
                    openDashboardScreen();
                } else {
                    loginMessageLabel.setText("Login Failed!");
                }
            }
        } catch (Exception e) {
            showErrorAlert("Database Error", "An error occurred while validating login.");
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