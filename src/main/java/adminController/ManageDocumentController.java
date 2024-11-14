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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

                itemTableView.getColumns().addAll(idColumn, titleColumn, authorColumn,
                        editionColumn, quantityInStockColumn, genreColumn, pageCountColumn);
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

                itemTableView.getColumns().addAll(idColumnMagazine, titleColumnMagazine,
                        authorColumnMagazine, publishNumberColumn, monthColumn);
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

                itemTableView.getColumns().addAll(idColumnJournal, titleColumnJournal,
                        authorColumnJournal, volumeColumn, publishNumberColumnJournal);
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

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void addDocument() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminfxml/AddDocument.fxml"));
            Parent addDocumentRoot = loader.load();

            addDocumentRoot.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            addDocumentRoot.setOnMouseDragged(event -> {
                Stage stage = (Stage) addDocumentRoot.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            Stage stage = new Stage();
            stage.setTitle("Add New Document");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(addDocumentRoot));
            stage.setOnHidden(event -> loadDataForType(itemSelectorComboBox.getValue()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteDocument() {
        String selectedType = itemSelectorComboBox.getValue();
        Object selectedItem = itemTableView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            switch (selectedType) {
                case "Book":
                    Book selectedBook = (Book) selectedItem;
                    bookDAO.deleteBook(selectedBook.getId());
                    loadDataForType("Book");
                    break;

                case "Magazine":
                    Magazine selectedMagazine = (Magazine) selectedItem;
                    magazineDAO.deleteMagazine(selectedMagazine.getId());
                    loadDataForType("Magazine");
                    break;

                case "Thesis":
                    Thesis selectedThesis = (Thesis) selectedItem;
                    thesisDAO.deleteThesis(selectedThesis.getId());
                    loadDataForType("Thesis");
                    break;

                case "Journal":
                    Journal selectedJournal = (Journal) selectedItem;
                    journalDAO.deleteJournal(selectedJournal.getId());
                    loadDataForType("Journal");
                    break;
            }
        } else {
            System.out.println("Please select an item to delete.");
        }
    }

    @FXML
    private void updateDocument() {

    }


}
