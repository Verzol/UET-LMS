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
import java.sql.ResultSet;

public class RegisterController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

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
                !emailField.getText().isEmpty() &&
                !phoneField.getText().isEmpty() &&
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

        try {
            // Kiểm tra username hoặc email đã tồn tại chưa
            String checkUserQuery = "SELECT * FROM person WHERE username = ? OR email = ?";
            try (PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {
                checkUserStatement.setString(1, usernameField.getText());
                checkUserStatement.setString(2, emailField.getText());
                ResultSet resultSet = checkUserStatement.executeQuery();

                if (resultSet.next()) {
                    registrationMessageLabel.setText("Username or email already exists!");
                    return;
                }
            }

            // Thêm người dùng vào bảng `person`
            String insertPersonQuery = "INSERT INTO person (username, password, first_name, last_name, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertPersonStatement = connection.prepareStatement(insertPersonQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertPersonStatement.setString(1, usernameField.getText());
                insertPersonStatement.setString(2, passwordField.getText());
                insertPersonStatement.setString(3, firstNameField.getText());
                insertPersonStatement.setString(4, lastNameField.getText());
                insertPersonStatement.setString(5, emailField.getText());
                insertPersonStatement.setString(6, phoneField.getText());
                int affectedRows = insertPersonStatement.executeUpdate();

                if (affectedRows > 0) {
                    // Lấy ID tự tăng từ bảng `person`
                    ResultSet generatedKeys = insertPersonStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int personId = generatedKeys.getInt(1);

                        // Thêm thông tin vào bảng `user`
                        String insertUserQuery = "INSERT INTO user (id, max_documents_allowed) VALUES (?, ?)";
                        try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {
                            insertUserStatement.setInt(1, personId);
                            insertUserStatement.setInt(2, 5);
                            insertUserStatement.executeUpdate();
                        }
                    }

                    registrationMessageLabel.setText("Registration Successful!");
                    clearFields();
                } else {
                    registrationMessageLabel.setText("Registration Failed!");
                }
            }
        } catch (Exception e) {
            registrationMessageLabel.setText("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
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
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
