package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    @FXML
    private AnchorPane mainContent;

    @FXML
    private Button HomeButton;

    @FXML
    private Button manageBooksButton;

    @FXML
    private Button manageUsersButton;

    @FXML
    private Button issueBooksButton;

    @FXML
    private Button insightsButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button logoutButton;

    private Button selectedButton;

    @FXML
    public void initialize() {
        loadScene("Home.fxml");
        setSelectedButton(HomeButton);
    }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent scene = loader.load();
            mainContent.getChildren().setAll(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSelectedButton(Button button) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected-button");
        }
        button.getStyleClass().add("selected-button");
        selectedButton = button;
    }

    @FXML
    private void Home() {
        loadScene("Home.fxml");
        setSelectedButton(HomeButton);
    }

    @FXML
    private void manageBooks() {
        loadScene("ManageBooks.fxml");
        setSelectedButton(manageBooksButton);
    }

    @FXML
    private void manageUsers() {
        loadScene("ManageUsers.fxml");
        setSelectedButton(manageUsersButton);
    }

    @FXML
    private void issueBooks() {
        loadScene("IssueBooks.fxml");
        setSelectedButton(issueBooksButton);
    }

    @FXML
    private void insights() {
        loadScene("Insights.fxml");
        setSelectedButton(insightsButton);
    }

    @FXML
    private void settings() {
        loadScene("Settings.fxml");
        setSelectedButton(settingsButton);
    }

    @FXML
    private void logout() {
        loadScene("LogOut.fxml");
        setSelectedButton(logoutButton);
    }

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
            Parent view = loader.load();
            mainContent.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
