package service;

import DAO.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.nio.Buffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateProfileController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button closeScene;

    @FXML
    private void handleUpdateProfile() {

        String username = UserSession.getUsername();

        if (!UserSession.isUserLoggedIn()) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user is logged in.");
            return;
        }


        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();


        if (firstName.isEmpty() && lastName.isEmpty() && email.isEmpty() && phone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "At least one field must be updated!");
            return;
        }


        if (!email.isEmpty() && !email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid email address.");
            return;
        }


        if (updateProfile(username, firstName, lastName, email, phone)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile.");
        }
    }

    private boolean updateProfile(String username, String firstName, String lastName, String email, String phone) {

        StringBuilder queryBuilder = new StringBuilder("UPDATE person SET ");
        boolean firstCondition = true;

        if (!firstName.isEmpty()) {
            queryBuilder.append("first_name = ?");
            firstCondition = false;
        }
        if (!lastName.isEmpty()) {
            if (!firstCondition) queryBuilder.append(", ");
            queryBuilder.append("last_name = ?");
            firstCondition = false;
        }
        if (!email.isEmpty()) {
            if (!firstCondition) queryBuilder.append(", ");
            queryBuilder.append("email = ?");
            firstCondition = false;
        }
        if (!phone.isEmpty()) {
            if (!firstCondition) queryBuilder.append(", ");
            queryBuilder.append("phone = ?");
        }


        queryBuilder.append(" WHERE username = ?");

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString())) {


            int parameterIndex = 1;
            if (!firstName.isEmpty()) preparedStatement.setString(parameterIndex++, firstName);
            if (!lastName.isEmpty()) preparedStatement.setString(parameterIndex++, lastName);
            if (!email.isEmpty()) preparedStatement.setString(parameterIndex++, email);
            if (!phone.isEmpty()) preparedStatement.setString(parameterIndex++, phone);


            preparedStatement.setString(parameterIndex, username);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeScene.getScene().getWindow();
        stage.close();
    }

}
