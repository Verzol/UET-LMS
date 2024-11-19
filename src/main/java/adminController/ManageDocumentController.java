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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.documents.Book;
import models.documents.Journal;
import models.documents.Magazine;
import models.documents.Thesis;

import java.io.IOException;

public class ManageDocumentController {

    @FXML
    private ComboBox<String> itemSelectorComboBox;

    @FXML
    private TableView<Object> itemTableView;

    @FXML
    private Label editionLabel;

    @FXML
    private Label quantityInStockLabel;

    @FXML
    private Label genreLabel;

    @FXML
    private Label pageCountLabel;

    @FXML
    private Label publishNumberLabel;

    @FXML
    private Label monthLabel;

    @FXML
    private Label universityLabel;

    @FXML
    private Label supervisorLabel;

    @FXML
    private Label fieldLabel;

    @FXML
    private Label volumeLabel;

    @FXML
    private ImageView documentImageView;

    @FXML
    private Button viewCoverButton;


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
    }

    private void configureTableViewForType(String type) {
        itemTableView.getColumns().clear();

        switch (type) {
            case "Book":
                TableColumn<Object, String> idColumn = new TableColumn<>("ID");
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Object, String> isbnColumn = new TableColumn<>("ISBN");
                isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));

                TableColumn<Object, String> titleColumn = new TableColumn<>("Title");
                titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

                TableColumn<Object, String> authorColumn = new TableColumn<>("Author");
                authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

                TableColumn<Object, Integer> editionColumn = new TableColumn<>("Edition");
                editionColumn.setCellValueFactory(new PropertyValueFactory<>("edition"));

                TableColumn<Object, Integer> quantityInStockColumn = new TableColumn<>("Quantity In Stock");
                quantityInStockColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

                TableColumn<Object, String> genreColumn = new TableColumn<>("Genre");
                genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));

                TableColumn<Object, Integer> pageCountColumn = new TableColumn<>("Page Count");
                pageCountColumn.setCellValueFactory(new PropertyValueFactory<>("pageCount"));

                TableColumn<Object, String> imageUrlColumnBook = new TableColumn<>("Image URL");
                imageUrlColumnBook.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

                itemTableView.getColumns().addAll(idColumn, isbnColumn, titleColumn, authorColumn,
                        editionColumn, quantityInStockColumn, genreColumn, pageCountColumn, imageUrlColumnBook);
                break;

            case "Magazine":
                TableColumn<Object, String> idColumnMagazine = new TableColumn<>("ID");
                idColumnMagazine.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Object, String> titleColumnMagazine = new TableColumn<>("Title");
                titleColumnMagazine.setCellValueFactory(new PropertyValueFactory<>("title"));

                TableColumn<Object, String> authorColumnMagazine = new TableColumn<>("Author");
                authorColumnMagazine.setCellValueFactory(new PropertyValueFactory<>("author"));

                TableColumn<Object, String> publishNumberColumn = new TableColumn<>("Publish Number");
                publishNumberColumn.setCellValueFactory(new PropertyValueFactory<>("publishNumber"));

                TableColumn<Object, String> monthColumn = new TableColumn<>("Month");
                monthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));

                TableColumn<Object, String> imageUrlColumnMagazine = new TableColumn<>("Image URL");
                imageUrlColumnMagazine.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

                itemTableView.getColumns().addAll(idColumnMagazine, titleColumnMagazine,
                        authorColumnMagazine, publishNumberColumn, monthColumn, imageUrlColumnMagazine);
                break;

            case "Thesis":
                TableColumn<Object, String> idColumnThesis = new TableColumn<>("ID");
                idColumnThesis.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Object, String> titleColumnThesis = new TableColumn<>("Title");
                titleColumnThesis.setCellValueFactory(new PropertyValueFactory<>("title"));

                TableColumn<Object, String> authorColumnThesis = new TableColumn<>("Author");
                authorColumnThesis.setCellValueFactory(new PropertyValueFactory<>("author"));

                TableColumn<Object, String> universityColumn = new TableColumn<>("University");
                universityColumn.setCellValueFactory(new PropertyValueFactory<>("university"));

                TableColumn<Object, String> supervisorColumn = new TableColumn<>("Supervisor");
                supervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisor"));

                TableColumn<Object, String> fieldColumn = new TableColumn<>("Field");
                fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));

                itemTableView.getColumns().addAll(idColumnThesis, titleColumnThesis,
                        authorColumnThesis, universityColumn, supervisorColumn, fieldColumn);
                break;

            case "Journal":
                TableColumn<Object, String> idColumnJournal = new TableColumn<>("ID");
                idColumnJournal.setCellValueFactory(new PropertyValueFactory<>("id"));

                TableColumn<Object, String> titleColumnJournal = new TableColumn<>("Title");
                titleColumnJournal.setCellValueFactory(new PropertyValueFactory<>("title"));

                TableColumn<Object, String> authorColumnJournal = new TableColumn<>("Author");
                authorColumnJournal.setCellValueFactory(new PropertyValueFactory<>("author"));

                TableColumn<Object, Integer> volumeColumn = new TableColumn<>("Volume");
                volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));

                TableColumn<Object, Integer> publishNumberColumnJournal = new TableColumn<>("Publish Number");
                publishNumberColumnJournal.setCellValueFactory(new PropertyValueFactory<>("publishNumber"));

                TableColumn<Object, String> imageUrlColumnJournal = new TableColumn<>("Image URL");
                imageUrlColumnJournal.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

                itemTableView.getColumns().addAll(idColumnJournal, titleColumnJournal,
                        authorColumnJournal, volumeColumn, publishNumberColumnJournal, imageUrlColumnJournal);
                break;
        }
    }

    private void loadDataForType(String type) {
        switch (type) {
            case "Book":
                itemTableView.setItems(FXCollections.observableArrayList(bookDAO.getAllBooks()));
                break;
            case "Magazine":
                itemTableView.setItems(FXCollections.observableArrayList(magazineDAO.getAllMagazines()));
                break;
            case "Thesis":
                itemTableView.setItems(FXCollections.observableArrayList(thesisDAO.getAllThesis()));
                break;
            case "Journal":
                itemTableView.setItems(FXCollections.observableArrayList(journalDAO.getAllJournals()));
                break;
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
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setOnHidden(e -> loadDataForType(itemSelectorComboBox.getValue()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                switch (selectedType) {
                    case "Book" -> bookDAO.deleteBook(((Book) selectedItem).getId());
                    case "Magazine" -> magazineDAO.deleteMagazine(((Magazine) selectedItem).getId());
                    case "Journal" -> journalDAO.deleteJournal(((Journal) selectedItem).getId());
                    case "Thesis" -> thesisDAO.deleteThesis(((Thesis) selectedItem).getId());
                }
                loadDataForType(selectedType);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Document deleted successfully!");
                successAlert.showAndWait();
            }
        });
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
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setOnHidden(e -> loadDataForType(selectedType));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
        if (selectedItem != null) {
            if (selectedItem instanceof Book) {
                Book book = (Book) selectedItem;
                if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                    documentImageView.setImage(new Image(book.getImageUrl()));
                } else {
                    documentImageView.setImage(null);
                }
            } else if (selectedItem instanceof Magazine) {
                Magazine magazine = (Magazine) selectedItem;
                if (magazine.getImageUrl() != null && !magazine.getImageUrl().isEmpty()) {
                    documentImageView.setImage(new Image(magazine.getImageUrl()));
                } else {
                    documentImageView.setImage(null);
                }
            } else if (selectedItem instanceof Journal) {
                Journal journal = (Journal) selectedItem;
                if (journal.getImageUrl() != null && !journal.getImageUrl().isEmpty()) {
                    documentImageView.setImage(new Image(journal.getImageUrl()));
                } else {
                    documentImageView.setImage(null);
                }
            } else {
                documentImageView.setImage(null);
            }
        }
    }

    @FXML
    private void viewDocumentCover() {
        Object selectedItem = itemTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showErrorAlert("No item selected!");
            return;
        }
        String imageUrl = null;

        if (selectedItem instanceof Book book) {
            imageUrl = book.getImageUrl();
        } else if (selectedItem instanceof Magazine magazine) {
            imageUrl = magazine.getImageUrl();
        } else if (selectedItem instanceof Journal journal) {
            imageUrl = journal.getImageUrl();
        } else if (selectedItem instanceof Thesis thesis) {
            showErrorAlert("This document type does not support cover images!");
            return;
        }
        if (imageUrl == null || imageUrl.isEmpty()) {
            showErrorAlert("This document does not have a cover image URL!");
            return;
        }
        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Document Cover");

            AnchorPane root = new AnchorPane();

            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true);
            imageView.setImage(new Image(imageUrl));
            imageView.setFitWidth(400);
            imageView.setFitHeight(400);

            AnchorPane.setTopAnchor(imageView, 0.0);
            AnchorPane.setLeftAnchor(imageView, 0.0);
            AnchorPane.setRightAnchor(imageView, 0.0);
            AnchorPane.setBottomAnchor(imageView, 0.0);

            root.getChildren().addAll(imageView);
            dialog.getDialogPane().setContent(root);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);

            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Failed to load the image. Please check the URL!");
        }
    }




}
