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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.documents.Book;
import models.users.User;
import org.json.JSONObject;
import service.BookDetailController;
import utils.SessionManager;

import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
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

    public void loadQuestion() {

        String question = "Which book is written by Author X?";
        questionLabel.setText(question);
        option1.setText("Option 1");
        option2.setText("Option 2");
        option3.setText("Option 3");
        option4.setText("Option 4");
    }

    @FXML
    private void checkAnswer(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if (clickedButton.getText().equals("Correct Answer")) {
            System.out.println("Correct!");
        } else {
            System.out.println("Wrong!");
        }
    }

    @FXML
    public void entertainment() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/QuizGame.fxml"));
            Scene scene = new Scene(loader.load());


            Stage stage = new Stage();
            stage.setTitle("Book Quiz Game");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isFindBooksByAuthorMode = true;

    @FXML
    private ToggleButton modeToggle;

    @FXML
    private TextField keywordTextField;

    @FXML
    private void switchMode() {
        isFindBooksByAuthorMode = !isFindBooksByAuthorMode;

        if (isFindBooksByAuthorMode) {

            questionLabel.setText("Enter an author name:");
            keywordTextField.setPromptText("Author name...");
        } else {

            questionLabel.setText("Enter a book title:");
            keywordTextField.setPromptText("Book title...");
        }
    }


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
    private Button closeListViewButton;

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

        if (query.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please enter a title to search.");
            alert.showAndWait();
        } else {
            service.GoogleBooksAPI.searchBookByTitle(query, searchResultsList);

            if (!searchResultsList.getItems().isEmpty()) {
                searchResultsList.setVisible(true);
                searchResultsList.setManaged(true);
                closeListViewButton.setVisible(true);
                closeListViewButton.setManaged(true);
            } else {
                searchResultsList.setVisible(false);
                searchResultsList.setManaged(false);

                Alert noResultsAlert = new Alert(Alert.AlertType.INFORMATION);
                noResultsAlert.setHeaderText(null);
                noResultsAlert.setContentText("No results found for: " + query);
                noResultsAlert.showAndWait();
            }
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
    private void handleCloseListView() {
        searchResultsList.setVisible(false);
        searchResultsList.setManaged(false);
        closeListViewButton.setVisible(false);
        closeListViewButton.setManaged(false);
        searchResultsList.getItems().clear();
    }


    @FXML
    private ImageView sadImage;


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
    private ImageView avatarImage;

    @FXML
    private void changeAvatar(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();


        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Tệp hình ảnh", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(filter);


        File selectedFile = fileChooser.showOpenDialog(avatarImage.getScene().getWindow());

        if (selectedFile != null) {

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Xác nhận");
            confirmationAlert.setHeaderText("Bạn có chắc chắn muốn đổi avatar?");
            confirmationAlert.setContentText("Ảnh avatar mới sẽ được lưu thay thế ảnh cũ.");


            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {

                    String imagesDir = "avatars";
                    File dir = new File(imagesDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }


                    String fileName = "avatar_" + System.currentTimeMillis() + ".png";
                    File destinationFile = new File(dir, fileName);


                    Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


                    Image newAvatar = new Image(destinationFile.toURI().toString());
                    avatarImage.setImage(newAvatar);
                    setCircularAvatar(newAvatar);


                    saveAvatarFileName(fileName);


                    initializeUserDashboard();

                } catch (IOException e) {
                    showAlert("Lỗi", "Không thể lưu ảnh: " + e.getMessage());
                }
            }
        }
    }



    private void saveAvatarFileName(String fileName) {
        String username = SessionManager.getCurrentUsername();
        if (username != null && !username.isEmpty()) {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            try (Connection connection = databaseConnection.getConnection()) {
                if (connection != null) {
                    String query = "UPDATE person SET avatar = ? WHERE username = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, fileName);
                        preparedStatement.setString(2, username);
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initializeUserDashboard() {
        String avatarFileName = loadAvatarFileName();
        if (avatarFileName != null) {
            File avatarFile = new File("avatars/" + avatarFileName);
            if (avatarFile.exists()) {
                Image avatar = new Image(avatarFile.toURI().toString());
                avatarImage.setImage(avatar);
                setCircularAvatar(avatar);
            }
        }
    }


    private String loadAvatarFileName() {
        String username = SessionManager.getCurrentUsername();
        if (username != null && !username.isEmpty()) {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            try (Connection connection = databaseConnection.getConnection()) {
                if (connection != null) {
                    String query = "SELECT avatar FROM person WHERE username = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, username);
                        ResultSet resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            return resultSet.getString("avatar");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private void setCircularAvatar(Image image) {

        double size = Math.min(avatarImage.getFitWidth(), avatarImage.getFitHeight());


        Circle clip = new Circle(size / 2, size / 2, size / 2);


        avatarImage.setClip(clip);


        avatarImage.setImage(image);
    }

}

