package service;

import DAO.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordController {

    @FXML
    private TextField userField;
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    public void initialize() {
        System.out.println("userField: " + userField);
        System.out.println("oldPasswordField: " + oldPasswordField);
        System.out.println("newPasswordField: " + newPasswordField);
        System.out.println("confirmPasswordField: " + confirmPasswordField);
    }

    @FXML
    private void handleChangePassword() {
        String username = userField.getText();
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();


        if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }


        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New password and confirm password do not match.");
            return;
        }


        if (!checkOldPassword(username, oldPassword)) {
            System.out.println("Old password is incorrect.");
            return;
        }

        if (updatePassword(username, oldPassword, newPassword)) {
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Failed to update password. Please check your inputs.");
        }
    }

    private boolean updatePassword(String username, String oldPassword, String newPassword) {
        String query = "UPDATE user SET password = ? WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, oldPassword);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkOldPassword(String username, String oldPassword) {
        String query = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, oldPassword);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
