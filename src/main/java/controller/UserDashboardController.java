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
import javafx.stage.Stage;
import models.documents.Book;
import service.BookDetailController;

import java.awt.event.ActionEvent;
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

    private boolean isFindBooksByAuthorMode = true; // true: Tìm sách từ tác giả, false: Tìm tác giả từ sách

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
            e.printStackTrace();
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
    public void selectHome() {
        home();
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

    }

    private Button selectedButton;

    private void setSelectedButton(Button button) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected-button");
        }
        button.getStyleClass().add("selected-button");
        selectedButton = button;
    }

    @FXML
    private ImageView bookCoverImageView;

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
}
