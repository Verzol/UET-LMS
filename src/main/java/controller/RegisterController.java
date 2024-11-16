package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.security.SecureRandom;

public class RegisterController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signUpButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label registrationMessageLabel;

    @FXML
    public void signUpButtonAction(ActionEvent event) {
        if (isInputValid()) {
            if (passwordField.getText().equals(confirmPasswordField.getText())) {
                registerUser();
            } else {
                registrationMessageLabel.setText("Passwords do not match!");
            }
        } else {
            registrationMessageLabel.setText("Please complete all fields!");
        }
    }

    private boolean isInputValid() {
        return !firstNameField.getText().isEmpty() &&
                !lastNameField.getText().isEmpty() &&
                !usernameField.getText().isEmpty() &&
                !passwordField.getText().isEmpty() &&
                !confirmPasswordField.getText().isEmpty();
    }

    private void registerUser() {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        if (connection == null) {
            registrationMessageLabel.setText("Database connection failed!");
            return;
        }

        String randomId = generateRandomId(10);

        String insertUser = "INSERT INTO users (id, firstName, lastName, username, password, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertUser)) {
            preparedStatement.setString(1, randomId); // Chèn ID ngẫu nhiên
            preparedStatement.setString(2, firstNameField.getText());
            preparedStatement.setString(3, lastNameField.getText());
            preparedStatement.setString(4, usernameField.getText());
            preparedStatement.setString(5, passwordField.getText());
            preparedStatement.setString(6, "user"); // Gán vai trò mặc định là "user"

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                registrationMessageLabel.setText("Registration Successful!");
                clearFields();
            } else {
                registrationMessageLabel.setText("Registration Failed!");
            }
        } catch (Exception e) {
            registrationMessageLabel.setText("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private String generateRandomId(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder id = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            id.append(characters.charAt(index));
        }

        return id.toString();
    }

    @FXML
    public void cancelButtonAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent loginRoot = fxmlLoader.load();

            Scene currentScene = cancelButton.getScene();
            currentScene.setRoot(loginRoot);

        } catch (IOException e) {
            showErrorAlert("Error", "Unable to return to the login screen.");
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

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
