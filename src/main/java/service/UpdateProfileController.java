package service;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class UpdateProfileController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private void handleUpdateProfile() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();

        if (fullName.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required!");
        } else {
            // TODO: Implement profile update logic (e.g., update database)
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
