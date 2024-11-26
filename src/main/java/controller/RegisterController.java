package controller;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

import java.io.IOException;
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
                showFadeMessage("Passwords do not match!", true);
            }
        } else {
            showFadeMessage("Please complete all fields!", true);
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
            showFadeMessage("Database connection failed!", true);
            return;
        }

        try {
            String checkUserQuery = "SELECT * FROM person WHERE username = ? OR email = ?";
            try (PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery)) {
                checkUserStatement.setString(1, usernameField.getText());
                checkUserStatement.setString(2, emailField.getText());
                ResultSet resultSet = checkUserStatement.executeQuery();

                if (resultSet.next()) {
                    showFadeMessage("Username or email already exists!", true);
                    return;
                }
            }

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
                    ResultSet generatedKeys = insertPersonStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int personId = generatedKeys.getInt(1);

                        String insertUserQuery = "INSERT INTO user (id, max_documents_allowed) VALUES (?, ?)";
                        try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {
                            insertUserStatement.setInt(1, personId);
                            insertUserStatement.setInt(2, 5);
                            insertUserStatement.executeUpdate();
                        }
                    }

                    showFadeMessage("Registration Successful!", false);
                    clearFields();
                } else {
                    showFadeMessage("Registration Failed!", true);
                }
            }
        } catch (Exception e) {
            showFadeMessage("An error occurred: " + e.getMessage(), true);
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

    private void showFadeMessage(String message, boolean isError) {
        registrationMessageLabel.setText(message);
        registrationMessageLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-background-color: " + (isError ? "#ff0000;" : "#00a000;") +
                        "-fx-padding: 10px; -fx-border-radius: 5px; -fx-background-radius: 5px;"
        );

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), registrationMessageLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), registrationMessageLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);
        fadeOut.setDelay(Duration.seconds(2));

        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeIn.play();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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

    @FXML
    public void initialize() {
        firstNameField.setOnKeyPressed(this::handleArrowAndEnterKeys);
        lastNameField.setOnKeyPressed(this::handleArrowAndEnterKeys);
        usernameField.setOnKeyPressed(this::handleArrowAndEnterKeys);
        emailField.setOnKeyPressed(this::handleArrowAndEnterKeys);
        phoneField.setOnKeyPressed(this::handleArrowAndEnterKeys);
        passwordField.setOnKeyPressed(this::handleArrowAndEnterKeys);
        confirmPasswordField.setOnKeyPressed(this::handleArrowAndEnterKeys);
        signUpButton.setOnKeyPressed(this::handleArrowAndEnterKeys);
        cancelButton.setOnKeyPressed(this::handleArrowAndEnterKeys);
    }

    private void handleArrowAndEnterKeys(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            if (event.getSource() == confirmPasswordField) {
                passwordField.requestFocus();
            } else if (event.getSource() == passwordField) {
                phoneField.requestFocus();
            } else if (event.getSource() == phoneField) {
                emailField.requestFocus();
            } else if (event.getSource() == emailField) {
                usernameField.requestFocus();
            } else if (event.getSource() == usernameField) {
                lastNameField.requestFocus();
            } else if (event.getSource() == lastNameField) {
                firstNameField.requestFocus();
            } else if (event.getSource() == signUpButton) {
                confirmPasswordField.requestFocus();
            } else if (event.getSource() == cancelButton) {
                signUpButton.requestFocus();
            }
        } else if (event.getCode() == KeyCode.DOWN) {
            if (event.getSource() == firstNameField) {
                lastNameField.requestFocus();
            } else if (event.getSource() == lastNameField) {
                usernameField.requestFocus();
            } else if (event.getSource() == usernameField) {
                emailField.requestFocus();
            } else if (event.getSource() == emailField) {
                phoneField.requestFocus();
            } else if (event.getSource() == phoneField) {
                passwordField.requestFocus();
            } else if (event.getSource() == passwordField) {
                confirmPasswordField.requestFocus();
            } else if (event.getSource() == confirmPasswordField) {
                signUpButton.requestFocus();
            } else if (event.getSource() == signUpButton) {
                cancelButton.requestFocus();
            } else if (event.getSource() == cancelButton) {
                signUpButton.requestFocus();
            }
        } else if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() == confirmPasswordField) {
                signUpButton.fire();
            } else if (event.getSource() == cancelButton) {
                cancelButton.fire();
            }
        }
    }

}
