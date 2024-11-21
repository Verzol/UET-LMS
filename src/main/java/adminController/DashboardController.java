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
        private ListView<String> searchResultsList;

        @FXML
        private Button closeListViewButton;

        @FXML
        public void initialize() {
            searchResultsList.setPrefHeight(200);
            searchResultsList.setMaxHeight(200);
            loadScene("Home.fxml");
            setSelectedButton(HomeButton);
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
            loadScene("LogOut.fxml");
            setSelectedButton(logoutButton);
        }

        @FXML
        private void returnBooks() {
            loadScene("ReturnBooks.fxml");
            setSelectedButton(returnBooksButton);
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
                    // Xử lý lấy thông tin chi tiết từ Google Books API
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
    }
