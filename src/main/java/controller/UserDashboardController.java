package controller;

import DAO.BookDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import models.documents.Book;
import service.BookDetailController;
import utils.SessionManager;
import controller.SettingController;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private Label questionLabel;
    @FXML
    private Button option1, option2, option3, option4;
    @FXML
    private ToggleButton modeToggle;
    @FXML
    private TextField keywordTextField;
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
    @FXML
    private TextField searchBox;
    @FXML
    private ListView<String> searchResultsList;

    @FXML
    public void initialize() {
        searchResultsList.setVisible(false);
        searchResultsList.setManaged(false);

        searchResultsList.setPrefHeight(200);
        searchResultsList.setMaxHeight(200);
        loadScene("UserHome.fxml");
        setSelectedButton(homeButton);

        searchBox.setOnKeyReleased(event -> handleSearch());
        searchResultsList.setOnMouseClicked(this::handleBookClick);

        SessionManager.addAvatarChangeListener(this::onAvatarChanged);
        updateAvatarImageView();
    }

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
            if (selectedBook != null) {
                String[] bookDetails = selectedBook.split("\n");

                String title = bookDetails[0].split(" - ")[0].trim();
                String author = bookDetails[0].contains("-") ? bookDetails[0].split(" - ")[1].trim() : "Unknown Author";
                String publisher = bookDetails[1].replace("Publisher: ", "").trim();
                String publishedDate = bookDetails[2].replace("Published: ", "").trim();
                String description = bookDetails[3].replace("Description: ", "").trim();
                String imageUrl = bookDetails[4].replace("Image URL: ", "").trim();

                openBookDetailWindow(selectedBook, title, author, publisher, publishedDate, "", description, imageUrl);
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
        }
    }

    @FXML
    private void openChatBot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ChatBotView.fxml"));
            Parent root = loader.load();

            Stage chatBotStage = new Stage();
            chatBotStage.setTitle("ChatBot");
            chatBotStage.setScene(new Scene(root));
            chatBotStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void entertainment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Entertainment.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Gamee");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private ImageView avatarImageView;

    private String loadAvatarFromDatabase() {
        int userId = SessionManager.getCurrentUserId();
        String query = "SELECT avatar FROM person WHERE id = ?";

        try (PreparedStatement stmt = new DatabaseConnection().getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("avatar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Unable to load avatar: " + e.getMessage());
        }
        return null;
    }

    private void updateAvatarImageView() {
        String avatarPath = SessionManager.getCurrentAvatarPath();

        if (avatarPath == null || avatarPath.isEmpty()) {
            avatarPath = loadAvatarFromDatabase();
            SessionManager.setCurrentAvatarPath(avatarPath);
        }

        if (avatarPath != null && !avatarPath.isEmpty()) {
            File avatarFile = new File(avatarPath);
            if (avatarFile.exists()) {
                Image avatarImage = new Image(avatarFile.toURI().toString());
                avatarImageView.setImage(avatarImage);
                setCircularAvatar(avatarImageView);
            } else {
                useDefaultAvatar();
            }
        } else {
            useDefaultAvatar();
        }
    }

    private void useDefaultAvatar() {
        Image defaultAvatar = new Image(getClass().getResourceAsStream("/images/avatar_1732543662041.png"));
        avatarImageView.setFitWidth(40.0);
        avatarImageView.setFitHeight(40.0);
        avatarImageView.setPreserveRatio(true);
        avatarImageView.setImage(defaultAvatar);
        setCircularAvatar(avatarImageView);
    }

    private void setCircularAvatar(ImageView imageView) {
        double size = Math.min(imageView.getFitWidth(), imageView.getFitHeight());
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imageView.setClip(clip);
    }

    private void onAvatarChanged(String newAvatarPath) {
        updateAvatarImageView();
    }
}