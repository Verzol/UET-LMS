package adminController;

import DAO.BookDAO;
import DAO.JournalDAO;
import DAO.MagazineDAO;
import DAO.ThesisDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.documents.Book;
import models.documents.Journal;
import models.documents.Magazine;
import models.documents.Thesis;

public class AddDocumentController {

    @FXML
    private ComboBox<String> itemTypeComboBox;

    @FXML
    private TextField idTextField;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField authorTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    @FXML
    private TextField editionTextField;

    @FXML
    private TextField quantityInStockTextField;

    @FXML
    private TextField genreTextField;

    @FXML
    private TextField pageCountTextField;

    @FXML
    private Label editionLabel;

    @FXML
    private Label quantityInStockLabel;

    @FXML
    private Label genreLabel;

    @FXML
    private Label pageCountLabel;

    @FXML
    private VBox bookFields;

    @FXML
    private TextField publishNumberTextFieldMagazine;

    @FXML
    private TextField monthTextField;

    @FXML
    private Label publishNumberLabelMagazine;

    @FXML
    private Label monthLabel;

    @FXML
    private VBox magazineFields;

    @FXML
    private TextField universityTextField;

    @FXML
    private TextField supervisorTextField;

    @FXML
    private TextField fieldTextField;

    @FXML
    private Label universityLabel;

    @FXML
    private Label supervisorLabel;

    @FXML
    private Label fieldLabel;

    @FXML
    private VBox thesisFields;

    @FXML
    private TextField volumeTextField;

    @FXML
    private TextField publishNumberTextFieldJournal;

    @FXML
    private Label volumeLabel;

    @FXML
    private Label publishNumberLabelJournal;

    @FXML
    private VBox journalFields;

    private final BookDAO bookDAO = new BookDAO();
    private final MagazineDAO magazineDAO = new MagazineDAO();
    private final ThesisDAO thesisDAO = new ThesisDAO();
    private final JournalDAO journalDAO = new JournalDAO();

    @FXML
    private void initialize() {
        itemTypeComboBox.setItems(FXCollections.observableArrayList("Book", "Magazine", "Thesis", "Journal"));
        itemTypeComboBox.getSelectionModel().selectFirst();
        itemTypeComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateFieldsForType());
        updateFieldsForType();
    }

    @FXML
    public void updateFieldsForType() {
        hideAllFields();

        String selectedType = itemTypeComboBox.getValue();
        if (selectedType == null) return;

        switch (selectedType) {
            case "Book" -> showBookFields();
            case "Magazine" -> showMagazineFields();
            case "Thesis" -> showThesisFields();
            case "Journal" -> showJournalFields();
        }
    }

    private void hideAllFields() {
        bookFields.setVisible(false);
        bookFields.setManaged(false);
        magazineFields.setVisible(false);
        magazineFields.setManaged(false);
        thesisFields.setVisible(false);
        thesisFields.setManaged(false);
        journalFields.setVisible(false);
        journalFields.setManaged(false);

        idTextField.clear();
        titleTextField.clear();
        authorTextField.clear();
    }

    private void showBookFields() {
        bookFields.setVisible(true);
        bookFields.setManaged(true);
    }

    private void showMagazineFields() {
        magazineFields.setVisible(true);
        magazineFields.setManaged(true);
    }

    private void showThesisFields() {
        thesisFields.setVisible(true);
        thesisFields.setManaged(true);
    }

    private void showJournalFields() {
        journalFields.setVisible(true);
        journalFields.setManaged(true);
    }

    @FXML
    private void saveItem() {
        String selectedType = itemTypeComboBox.getValue();
        if (selectedType == null) return;

        try {
            boolean isDuplicate = checkDuplicate(idTextField.getText());

            if (isDuplicate) {
                showErrorAlert("Document ID already exists. Please use another ID");
            } else {
                switch (selectedType) {
                    case "Book" -> saveBook();
                    case "Magazine" -> saveMagazine();
                    case "Thesis" -> saveThesis();
                    case "Journal" -> saveJournal();
                }
                showSuccessAlert(selectedType + " has been added successfully!");
                clearFields();
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Please enter valid values for each fields");
        }
    }

    private boolean checkDuplicate(String id) {
        return bookDAO.exists(id) || magazineDAO.exists(id) || thesisDAO.exists(id) || journalDAO.exists(id);
    }

    private void clearFields() {
        idTextField.clear();
        titleTextField.clear();
        authorTextField.clear();

        editionTextField.clear();
        quantityInStockTextField.clear();
        genreTextField.clear();
        pageCountTextField.clear();
        publishNumberTextFieldMagazine.clear();
        monthTextField.clear();
        universityTextField.clear();
        supervisorTextField.clear();
        fieldTextField.clear();
        volumeTextField.clear();
        publishNumberTextFieldJournal.clear();
    }


    private void saveBook() {
        Book newBook = new Book();
        newBook.setId(idTextField.getText());
        newBook.setTitle(titleTextField.getText());
        newBook.setAuthor(authorTextField.getText());
        newBook.setEdition(Integer.parseInt(editionTextField.getText()));
        newBook.setQuantityInStock(Integer.parseInt(quantityInStockTextField.getText()));
        newBook.setGenre(genreTextField.getText());
        newBook.setPageCount(Integer.parseInt(pageCountTextField.getText()));
        bookDAO.addBook(newBook);
    }

    private void saveMagazine() {
        Magazine newMagazine = new Magazine();
        newMagazine.setId(idTextField.getText());
        newMagazine.setTitle(titleTextField.getText());
        newMagazine.setAuthor(authorTextField.getText());
        newMagazine.setPublishNumber(publishNumberTextFieldMagazine.getText());
        newMagazine.setMonth(monthTextField.getText());
        magazineDAO.addMagazine(newMagazine);
    }

    private void saveThesis() {
        Thesis newThesis = new Thesis();
        newThesis.setId(idTextField.getText());
        newThesis.setTitle(titleTextField.getText());
        newThesis.setAuthor(authorTextField.getText());
        newThesis.setUniversity(universityTextField.getText());
        newThesis.setSupervisor(supervisorTextField.getText());
        newThesis.setField(fieldTextField.getText());
        thesisDAO.addThesis(newThesis);
    }

    private void saveJournal() {
        Journal newJournal = new Journal();
        newJournal.setId(idTextField.getText());
        newJournal.setTitle(titleTextField.getText());
        newJournal.setAuthor(authorTextField.getText());
        newJournal.setVolume(Integer.parseInt(volumeTextField.getText()));
        newJournal.setPublishNumber(Integer.parseInt(publishNumberTextFieldJournal.getText()));
        journalDAO.addJournal(newJournal);
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) titleTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelAddDocument() {
        closeWindow();
    }
}
