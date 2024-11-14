package adminController;

import DAO.DatabaseConnection;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

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
        Connection connection = databaseConnection.getConnection();

        if (connection == null) {
            loginMessageLabel.setText("Database connection failed!");
            return;
        }

        String verifyLogin = "SELECT count(1) FROM user_account WHERE username = ? AND password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(verifyLogin)) {
            preparedStatement.setString(1, accountField.getText());
            preparedStatement.setString(2, passwordField.getText());
            ResultSet querySet = preparedStatement.executeQuery();

            if (querySet.next() && querySet.getInt(1) == 1) {
                loginMessageLabel.setText("Login Successful!");
            } else {
                loginMessageLabel.setText("Login Failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
