package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField searchBox;

    @FXML
    private void handleSearch() {
        String query = searchBox.getText();
        if (query.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("You want to search something?");
            alert.showAndWait();
        } else {
            List<String> data = Arrays.asList("Harry Potter", "OOP", "DSA", "Boruto");

            List<String> results = data.stream()
                    .filter(item -> item.toLowerCase().contains(query.toLowerCase()))
                    .toList();

            if (results.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Results");
                alert.setHeaderText(null);
                alert.setContentText("No results found for: " + query);
                alert.showAndWait();
            } else {
                System.out.println("Search results: " + results);
            }
        }
    }
    @FXML
    private Button HomeButton;

    @FXML
    private void Home(javafx.event.ActionEvent event) {
        loadView("Home.fxml");
    }

    @FXML
    private Button manageBooksButton;

    @FXML
    private void manageBooks(javafx.event.ActionEvent event) {
        loadView("ManageBooks.fxml");
    }

    @FXML
    private Button manageUsersButton;

    @FXML
    private void manageUsers(javafx.event.ActionEvent event) {
        loadView("ManageUsers.fxml");
    }

    @FXML
    private Button exitButton;

    @FXML
    private void exitApplication() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit confirmation");
        alert.setHeaderText("Are you sure you want to exit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    private Button minimizeButton;

    @FXML
    private void minimizeApplication(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Region view = loader.load();
            borderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
