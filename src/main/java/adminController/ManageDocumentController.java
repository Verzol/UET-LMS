    package adminController;

    import DAO.BookDAO;
    import DAO.JournalDAO;
    import DAO.MagazineDAO;
    import DAO.ThesisDAO;
    import javafx.collections.FXCollections;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.VBox;
    import javafx.stage.Modality;
    import javafx.stage.Stage;
    import models.documents.*;

    import java.io.IOException;

    public class ManageDocumentController {

        @FXML
        private ComboBox<String> itemSelectorComboBox;

        @FXML
        private TableView<Object> itemTableView;

        @FXML
        private Button showDocumentDetails;

        @FXML
        private Button searchButton;

        @FXML
        private TextField searchBar;

        private BookDAO bookDAO;
        private MagazineDAO magazineDAO;
        private ThesisDAO thesisDAO;
        private JournalDAO journalDAO;

        @FXML
        private void initialize() {
            bookDAO = new BookDAO();
            magazineDAO = new MagazineDAO();
            thesisDAO = new ThesisDAO();
            journalDAO = new JournalDAO();

            itemSelectorComboBox.setItems(FXCollections.observableArrayList("Book", "Magazine", "Thesis", "Journal"));
            itemSelectorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    configureTableViewForType(newValue);
                    loadDataForType(newValue);
                }
            });
            itemSelectorComboBox.setValue("Book");

            searchBar.setOnKeyReleased(event -> searchAndDisplay());

            itemTableView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    showDocumentDetails();
                }
            });
        }

        @FXML
        private void configureTableViewForType(String type) {
            itemTableView.getColumns().clear();

            itemTableView.getStyleClass().add("colored-table");

            TableColumn<Object, String> idColumn = createStyledColumn("ID", "id", 80);
            TableColumn<Object, String> titleColumn = createStyledColumn("Title", "title", 200);
            TableColumn<Object, String> authorColumn = createStyledColumn("Author", "author", 150);
            TableColumn<Object, Integer> editionColumn = createStyledColumn("Edition", "edition", 100);
            TableColumn<Object, Integer> quantityColumn = createStyledColumn("Quantity", "quantityInStock", 100);
            TableColumn<Object, Integer> borrowedQuantityColumn = createStyledColumn("Borrowed", "borrowedQuantity", 100);
            TableColumn<Object, Integer> timesBorrowedColumn = createStyledColumn("Times Borrowed", "timesBorrowed", 120);

            itemTableView.getColumns().addAll(idColumn, titleColumn, authorColumn, editionColumn, quantityColumn, borrowedQuantityColumn, timesBorrowedColumn);

            switch (type) {
                case "Book" -> setupBookColumns();
                case "Magazine" -> setupMagazineColumns();
                case "Thesis" -> setupThesisColumns();
                case "Journal" -> setupJournalColumns();
            }
        }

        private void setupBookColumns() {
            TableColumn<Object, String> genreColumn = createStyledColumn("Genre", "genre", 120);
            TableColumn<Object, Integer> pageCountColumn = createStyledColumn("Page Count", "pageCount", 120);
            TableColumn<Object, String> ISBNColumn = createStyledColumn("ISBN", "ISBN", 150);
            TableColumn<Object, String> imageUrlColumn = createStyledColumn("Image URL", "imageUrl", 200);

            itemTableView.getColumns().addAll(genreColumn, pageCountColumn, ISBNColumn, imageUrlColumn);
        }

        private void setupMagazineColumns() {
            TableColumn<Object, String> publishNumberColumn = createStyledColumn("Publish Number", "publishNumber", 150);
            TableColumn<Object, String> monthColumn = createStyledColumn("Month", "month", 100);
            TableColumn<Object, String> imageUrlColumn = createStyledColumn("Image URL", "imageUrl", 200);

            itemTableView.getColumns().addAll(publishNumberColumn, monthColumn, imageUrlColumn);
        }

        private void setupThesisColumns() {
            TableColumn<Object, String> universityColumn = createStyledColumn("University", "university", 150);
            TableColumn<Object, String> supervisorColumn = createStyledColumn("Supervisor", "supervisor", 150);
            TableColumn<Object, String> fieldColumn = createStyledColumn("Field", "field", 150);

            itemTableView.getColumns().addAll(universityColumn, supervisorColumn, fieldColumn);
        }

        private void setupJournalColumns() {
            TableColumn<Object, Integer> volumeColumn = createStyledColumn("Volume", "volume", 100);
            TableColumn<Object, String> publishNumberColumn = createStyledColumn("Publish Number", "publishNumber", 150);
            TableColumn<Object, String> imageUrlColumn = createStyledColumn("Image URL", "imageUrl", 200);

            itemTableView.getColumns().addAll(volumeColumn, publishNumberColumn, imageUrlColumn);
        }

        private <T> TableColumn<Object, T> createStyledColumn(String title, String property, int width) {
            TableColumn<Object, T> column = new TableColumn<>(title);
            column.setCellValueFactory(new PropertyValueFactory<>(property));
            column.setPrefWidth(width);

            column.getStyleClass().add("table-cell");
            column.setStyle("-fx-alignment: CENTER;");
            return column;
        }

        @FXML
        private void loadDataForType(String type) {
            switch (type) {
                case "Book" -> itemTableView.setItems(FXCollections.observableArrayList(bookDAO.getAllBooks()));
                case "Magazine" -> itemTableView.setItems(FXCollections.observableArrayList(magazineDAO.getAllMagazines()));
                case "Thesis" -> itemTableView.setItems(FXCollections.observableArrayList(thesisDAO.getAllThesis()));
                case "Journal" -> itemTableView.setItems(FXCollections.observableArrayList(journalDAO.getAllJournals()));
            }
        }

        @FXML
        private void updateDocument() {
            Object selectedItem = itemTableView.getSelectionModel().getSelectedItem();
            String selectedType = itemSelectorComboBox.getValue();

            if (selectedItem == null) {
                showErrorAlert("No item selected to update!");
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfxml/UpdateDocument.fxml"));
                Parent root = loader.load();

                UpdateDocumentController controller = loader.getController();
                controller.setDocument(selectedItem, selectedType);

                Stage stage = new Stage();
                stage.setTitle("Update Document");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                if (controller.isUpdated()) {
                    boolean updated = false;

                    switch (selectedType) {
                        case "Book" -> updated = bookDAO.updateBook((Book) controller.getUpdatedDocument());
                        case "Magazine" -> updated = magazineDAO.updateMagazine((Magazine) controller.getUpdatedDocument());
                        case "Thesis" -> updated = thesisDAO.updateThesis((Thesis) controller.getUpdatedDocument());
                        case "Journal" -> updated = journalDAO.updateJournal((Journal) controller.getUpdatedDocument());
                        default -> showErrorAlert("Invalid document type selected!");
                    }

                    if (updated) {
                        loadDataForType(selectedType);
                    } else {
                        showErrorAlert("Failed to update document. Please try again.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Failed to open Update Document window.");
            }
        }

        private void showErrorAlert(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        @FXML
        private void showDocumentDetails() {
            Object selectedItem = itemTableView.getSelectionModel().getSelectedItem();

            if (selectedItem instanceof Document document) {
                try {
                    Stage stage = new Stage();
                    stage.setTitle("Document Details");

                    VBox dialogContent = new VBox(20);
                    dialogContent.setStyle(
                            "-fx-padding: 20; " +
                                    "-fx-background-color: white; " +
                                    "-fx-border-color: #cccccc; " +
                                    "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 10, 0, 0, 4);"
                    );

                    Label headerLabel = new Label("Document Details");
                    headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

                    TextArea detailTextArea = new TextArea();
                    detailTextArea.setEditable(false);
                    detailTextArea.setWrapText(true);
                    detailTextArea.setText(document.showDetail());
                    detailTextArea.setPrefWidth(300);
                    detailTextArea.setPrefHeight(250);
                    detailTextArea.setStyle("-fx-font-size: 15px; -fx-text-fill: #000;");

                    ImageView imageView = new ImageView();
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(200);
                    imageView.setFitHeight(300);

                    String imageUrl = null;
                    if (document instanceof Book book) {
                        imageUrl = book.getImageUrl();
                    } else if (document instanceof Magazine magazine) {
                        imageUrl = magazine.getImageUrl();
                    } else if (document instanceof Journal journal) {
                        imageUrl = journal.getImageUrl();
                    }

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        try {
                            imageView.setImage(new Image(imageUrl));
                        } catch (Exception e) {
                            imageView.setImage(null);
                            detailTextArea.appendText("\n\n[Image could not be loaded]");
                        }
                    } else {
                        detailTextArea.appendText("\n\n[No Image Available]");
                    }

                    HBox contentBox = new HBox(20, detailTextArea, imageView);
                    contentBox.setStyle("-fx-alignment: center;");

                    dialogContent.getChildren().addAll(headerLabel, contentBox);

                    Scene scene = new Scene(dialogContent);
                    stage.setScene(scene);
                    stage.showAndWait();

                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorAlert("Failed to display document details.");
                }
            } else {
                showErrorAlert("No document selected or invalid document type!");
            }
        }


        @FXML
        private void addDocument() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfxml/AddDocument.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Add Document");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                loadDataForType(itemSelectorComboBox.getValue());
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Failed to open Add Document window.");
            }
        }

        @FXML
        private void deleteDocument() {
            Object selectedItem = itemTableView.getSelectionModel().getSelectedItem();
            String selectedType = itemSelectorComboBox.getValue();

            if (selectedItem == null) {
                showErrorAlert("No item selected to delete!");
                return;
            }

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Delete");
            confirmationAlert.setHeaderText("Are you sure you want to delete this document?");
            confirmationAlert.setContentText("This action cannot be undone.");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    switch (selectedType) {
                        case "Book" -> bookDAO.deleteBook(((Book) selectedItem).getId());
                        case "Magazine" -> magazineDAO.deleteMagazine(((Magazine) selectedItem).getId());
                        case "Journal" -> journalDAO.deleteJournal(((Journal) selectedItem).getId());
                        case "Thesis" -> thesisDAO.deleteThesis(((Thesis) selectedItem).getId());
                        default -> showErrorAlert("Invalid document type selected.");
                    }
                    loadDataForType(selectedType);

                    showSuccessAlert("Document deleted successfully!");
                }
            });
        }

        private void showSuccessAlert(String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

        @FXML
        private void searchAndDisplay() {
            String searchKeyword = searchBar.getText().trim();
            String selectedType = itemSelectorComboBox.getValue();

            if (searchKeyword.isEmpty()) {
                loadDataForType(selectedType);
                return;
            }

            switch (selectedType) {
                case "Book" -> {
                    var books = bookDAO.searchBooks(searchKeyword);
                    itemTableView.setItems(FXCollections.observableArrayList(books));
                }
                case "Magazine" -> {
                    var magazines = magazineDAO.searchMagazines(searchKeyword);
                    itemTableView.setItems(FXCollections.observableArrayList(magazines));
                }
                case "Thesis" -> {
                    var theses = thesisDAO.searchThesis(searchKeyword);
                    itemTableView.setItems(FXCollections.observableArrayList(theses));
                }
                case "Journal" -> {
                    var journals = journalDAO.searchJournal(searchKeyword);
                    itemTableView.setItems(FXCollections.observableArrayList(journals));
                }
                default -> showErrorAlert("Invalid document type selected for search.");
            }
        }

        @FXML
        private Button refreshButton;

        @FXML
        private void refreshTable() {
            String selectedType = itemSelectorComboBox.getValue();
            loadDataForType(selectedType);
        }
    }
