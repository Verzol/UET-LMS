package adminController;

import DAO.BookDAO;
import DAO.JournalDAO;
import DAO.MagazineDAO;
import DAO.ThesisDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.documents.*;

public class UpdateDocumentController {

    @FXML
    private TextField titleTextField;
    @FXML
    private TextField authorTextField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField editionField;
    @FXML
    private TextField additionalField1;
    @FXML
    private TextField additionalField2;
    @FXML
    private TextField additionalField3;
    @FXML
    private TextField additionalField4;

    @FXML
    private Label additionalField1Label;
    @FXML
    private Label additionalField2Label;
    @FXML
    private Label additionalField3Label;
    @FXML
    private Label additionalField4Label;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Object document;
    private String type;

    private boolean updated = false;

    private final BookDAO bookDAO = new BookDAO();
    private final MagazineDAO magazineDAO = new MagazineDAO();
    private final ThesisDAO thesisDAO = new ThesisDAO();
    private final JournalDAO journalDAO = new JournalDAO();

    public boolean isUpdated() {
        return updated;
    }

    public Object getUpdatedDocument() {
        return document;
    }

    public void setDocument(Object document, String type) {
        clearAndHideAdditionalFields();
        this.document = document;
        this.type = type;

        if (document instanceof Document baseDocument) {
            titleTextField.setText(baseDocument.getTitle());
            authorTextField.setText(baseDocument.getAuthor());
            quantityField.setText(String.valueOf(baseDocument.getQuantityInStock()));
            editionField.setText(String.valueOf(baseDocument.getEdition()));
        }

        switch (type) {
            case "Book" -> setupBookFields((Book) document);
            case "Magazine" -> setupMagazineFields((Magazine) document);
            case "Thesis" -> setupThesisFields((Thesis) document);
            case "Journal" -> setupJournalFields((Journal) document);
            default -> showErrorAlert("Unknown document type: " + type);
        }
    }

    private void setupBookFields(Book book) {
        additionalField1Label.setText("Genre:");
        additionalField1.setText(book.getGenre());
        additionalField1Label.setVisible(true);
        additionalField1.setVisible(true);

        additionalField2Label.setText("Page Count:");
        additionalField2.setText(String.valueOf(book.getPageCount()));
        additionalField2Label.setVisible(true);
        additionalField2.setVisible(true);

        additionalField3Label.setText("ISBN:");
        additionalField3.setText(book.getISBN());
        additionalField3Label.setVisible(true);
        additionalField3.setVisible(true);

        additionalField4Label.setText("Image URL:");
        additionalField4.setText(book.getImageUrl());
        additionalField4Label.setVisible(true);
        additionalField4.setVisible(true);
    }

    private void setupMagazineFields(Magazine magazine) {
        additionalField1Label.setText("Publish Number:");
        additionalField1.setText(magazine.getPublishNumber());
        additionalField1Label.setVisible(true);
        additionalField1.setVisible(true);

        additionalField2Label.setText("Month:");
        additionalField2.setText(magazine.getMonth());
        additionalField2Label.setVisible(true);
        additionalField2.setVisible(true);

        additionalField3Label.setText("Image URL:");
        additionalField3.setText(magazine.getImageUrl());
        additionalField3Label.setVisible(true);
        additionalField3.setVisible(true);
    }

    private void setupThesisFields(Thesis thesis) {
        additionalField1Label.setText("University:");
        additionalField1.setText(thesis.getUniversity());
        additionalField1Label.setVisible(true);
        additionalField1.setVisible(true);

        additionalField2Label.setText("Supervisor:");
        additionalField2.setText(thesis.getSupervisor());
        additionalField2Label.setVisible(true);
        additionalField2.setVisible(true);

        additionalField3Label.setText("Field:");
        additionalField3.setText(thesis.getField());
        additionalField3Label.setVisible(true);
        additionalField3.setVisible(true);
    }

    private void setupJournalFields(Journal journal) {
        additionalField1Label.setText("Volume:");
        additionalField1.setText(String.valueOf(journal.getVolume()));
        additionalField1Label.setVisible(true);
        additionalField1.setVisible(true);

        additionalField2Label.setText("Publish Number:");
        additionalField2.setText(String.valueOf(journal.getPublishNumber()));
        additionalField2Label.setVisible(true);
        additionalField2.setVisible(true);

        additionalField3Label.setText("Image URL:");
        additionalField3.setText(journal.getImageUrl());
        additionalField3Label.setVisible(true);
        additionalField3.setVisible(true);
    }

    private void clearAndHideAdditionalFields() {
        additionalField1.clear();
        additionalField2.clear();
        additionalField3.clear();
        additionalField4.clear();
        quantityField.clear();
        editionField.clear();

        additionalField1.setVisible(false);
        additionalField2.setVisible(false);
        additionalField3.setVisible(false);
        additionalField4.setVisible(false);

        additionalField1Label.setVisible(false);
        additionalField2Label.setVisible(false);
        additionalField3Label.setVisible(false);
        additionalField4Label.setVisible(false);
    }

    @FXML
    private void saveChanges() {
        if (document == null || type == null) {
            showErrorAlert("No document selected for update!");
            return;
        }

        try {
            switch (type) {
                case "Book" -> {
                    Book book = (Book) document;
                    book.setTitle(titleTextField.getText());
                    book.setAuthor(authorTextField.getText());
                    book.setGenre(additionalField1.getText());
                    book.setPageCount(Integer.parseInt(additionalField2.getText()));
                    book.setISBN(additionalField3.getText());
                    book.setImageUrl(additionalField4.getText());
                    book.setQuantityInStock(Integer.parseInt(quantityField.getText()));
                    book.setEdition(Integer.parseInt(editionField.getText())); // Update edition
                    bookDAO.updateBook(book);
                }
                case "Magazine" -> {
                    Magazine magazine = (Magazine) document;
                    magazine.setTitle(titleTextField.getText());
                    magazine.setAuthor(authorTextField.getText());
                    magazine.setPublishNumber(additionalField1.getText());
                    magazine.setMonth(additionalField2.getText());
                    magazine.setImageUrl(additionalField3.getText());
                    magazine.setQuantityInStock(Integer.parseInt(quantityField.getText()));
                    magazine.setEdition(Integer.parseInt(editionField.getText())); // Update edition
                    magazineDAO.updateMagazine(magazine);
                }
                case "Thesis" -> {
                    Thesis thesis = (Thesis) document;
                    thesis.setTitle(titleTextField.getText());
                    thesis.setAuthor(authorTextField.getText());
                    thesis.setUniversity(additionalField1.getText());
                    thesis.setSupervisor(additionalField2.getText());
                    thesis.setField(additionalField3.getText());
                    thesis.setQuantityInStock(Integer.parseInt(quantityField.getText()));
                    thesis.setEdition(Integer.parseInt(editionField.getText())); // Update edition
                    thesisDAO.updateThesis(thesis);
                }
                case "Journal" -> {
                    Journal journal = (Journal) document;
                    journal.setTitle(titleTextField.getText());
                    journal.setAuthor(authorTextField.getText());
                    journal.setVolume(Integer.parseInt(additionalField1.getText()));
                    journal.setPublishNumber(Integer.parseInt(additionalField2.getText()));
                    journal.setImageUrl(additionalField3.getText());
                    journal.setQuantityInStock(Integer.parseInt(quantityField.getText()));
                    journal.setEdition(Integer.parseInt(editionField.getText())); // Update edition
                    journalDAO.updateJournal(journal);
                }
                default -> showErrorAlert("Unknown document type: " + type);
            }
            showSuccessAlert("Update successful!");
            updated = true;
            closeWindow();
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid number format for fields!");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error while saving changes!");
        }
    }

    private void showSuccessAlert(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancelUpdate() {
        updated = false;
        closeWindow();
    }
}