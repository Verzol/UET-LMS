package controller;

import DAO.BookDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.documents.Book;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserDashboardController {

    @FXML
    private AnchorPane mainContent;

    @FXML
    private Button homeButton;

    @FXML
    private Button borrowDocumentButton;

    @FXML
    private Button returnDocumentButton;

    @FXML
    private Button entertainmentButton;

    @FXML
    private Button settingButton;

    @FXML
    private Button logoutButton;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private ListView<String> topBooksList;

    @FXML
    private ImageView bookCoverImageView;

    @FXML
    private Button exitButton;

    @FXML
    private Button minimizeButton;

    private Button selectedButton;

    /**
     * Loads a specific FXML scene into the main content area.
     */
    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent scene = loader.load();
            mainContent.getChildren().setAll(scene);
        } catch (IOException e) {
            showAlert("Load Scene Error", "Cannot load the scene: " + e.getMessage());
        }
    }

    @FXML
    public void borrowDocument() {
        loadScene("BorrowDocument.fxml");
        setSelectedButton(borrowDocumentButton);
    }

    @FXML
    public void home() {
        loadScene("UserHome.fxml");
        setSelectedButton(homeButton);
    }

    @FXML
    public void returnDocument() {
        loadScene("ReturnDocument.fxml");
        setSelectedButton(returnDocumentButton);
    }

    @FXML
    public void entertainment() {
        loadScene("Entertainment.fxml");
        setSelectedButton(entertainmentButton);
    }

    @FXML
    public void setting() {
        loadScene("Setting.fxml");
        setSelectedButton(settingButton);
    }

    @FXML
    public void logout() {
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

    private void setSelectedButton(Button button) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected-button");
        }
        button.getStyleClass().add("selected-button");
        selectedButton = button;
    }

    public void displayBookCover(String isbn) {
        BookDAO bookDAO = new BookDAO();
        List<Book> books = bookDAO.getAllBooks();

        for (Book book : books) {
            if (book.getISBN().equals(isbn)) {
                String imageUrl = book.getImageUrl();
                Image bookCoverImage = new Image(imageUrl, true);
                bookCoverImageView.setImage(bookCoverImage);
                break;
            }
        }
    }

    @FXML
    private void exitApplication() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    private void minimizeApplication(javafx.event.ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void selectHome() {
        home();
    }
}
