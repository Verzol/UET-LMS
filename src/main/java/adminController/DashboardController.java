package adminController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.BookDetailController;

import java.io.IOException;
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

    @FXML
    private Button returnBooksButton;

    private Button selectedButton;

    @FXML
    private TextField searchBox;

    @FXML
    private Button ChatBotButton;
    @FXML
    private ListView<String> searchResultsList;

    @FXML
    public void initialize() {
        searchResultsList.setVisible(false);
        searchResultsList.setManaged(false);

        searchResultsList.setPrefHeight(200);
        searchResultsList.setMaxHeight(200);
        loadScene("Home.fxml");
        setSelectedButton(HomeButton);

        searchBox.setOnKeyReleased(event -> handleSearch());
        searchResultsList.setOnMouseClicked(this::handleBookClick);
    }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfxml/" + fxmlFile));
            Parent scene = loader.load();

            AnchorPane.setTopAnchor(scene, 0.0);
            AnchorPane.setBottomAnchor(scene, 0.0);
            AnchorPane.setLeftAnchor(scene, 0.0);
            AnchorPane.setRightAnchor(scene, 0.0);

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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) mainContent.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Logout Error", "Failed to log out: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void returnBooks() {
        loadScene("ReturnBooks.fxml");
        setSelectedButton(returnBooksButton);
    }

    @FXML
    private void handleSearch() {
        String query = searchBox.getText().trim();

        if (!query.isEmpty()) {
            service.GoogleBooksAPI.searchBookByTitle(query, searchResultsList);

            if (!searchResultsList.getItems().isEmpty()) {
                searchResultsList.setVisible(true);
                searchResultsList.setManaged(true);
            } else {
                searchResultsList.setVisible(false);
                searchResultsList.setManaged(false);
            }
        } else {
            searchResultsList.setVisible(false);
            searchResultsList.setManaged(false);
            searchResultsList.getItems().clear();
        }
    }

    @FXML
    private void handleBookClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String selectedBook = searchResultsList.getSelectionModel().getSelectedItem();
            if (selectedBook != null && !selectedBook.isEmpty()) {
                try {
                    String[] bookDetails = selectedBook.split("\n");

                    String title = bookDetails[0].split(" - ")[0].trim();
                    String author = bookDetails[0].contains("-") ? bookDetails[0].split(" - ")[1].trim() : "Unknown Author";
                    String publisher = bookDetails.length > 1 ? bookDetails[1].replace("Publisher: ", "").trim() : "Unknown Publisher";
                    String publishedDate = bookDetails.length > 2 ? bookDetails[2].replace("Published: ", "").trim() : "Unknown Date";
                    String description = bookDetails.length > 3 ? bookDetails[3].replace("Description: ", "").trim() : "No Description";
                    String imageUrl = bookDetails.length > 4 ? bookDetails[4].replace("Image URL: ", "").trim() : "";

                    openBookDetailWindow(selectedBook, title, author, publisher, publishedDate, "", description, imageUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorAlert("Invalid book format", "The selected book format is invalid.");
                }
            }
        }
    }

    private void openBookDetailWindow(String selectedBook, String title, String author, String publisher,
                                      String publishedDate, String rating, String description,
                                      String imageUrl) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfxml/BookDetail.fxml"));
            Parent root = loader.load();

            BookDetailController detailController = loader.getController();
            detailController.setBookDetails(title, author, publisher, publishedDate, rating, description, imageUrl);

            Stage detailStage = new Stage();
            detailStage.setTitle("Book Details");
            detailStage.setScene(new Scene(root));
            detailStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error loading details", "Could not load the book details window.");
        }
    }

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
    private void minimizeApplication(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
