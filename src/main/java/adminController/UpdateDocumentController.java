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
import models.documents.Book;
import models.documents.Journal;
import models.documents.Magazine;
import models.documents.Thesis;

public class UpdateDocumentController {

    @FXML
    private TextField titleTextField;
    @FXML
    private TextField authorTextField;
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

    private Object document; // Tài liệu được chọn
    private String type;     // Loại tài liệu

    private final BookDAO bookDAO = new BookDAO();
    private final MagazineDAO magazineDAO = new MagazineDAO();
    private final ThesisDAO thesisDAO = new ThesisDAO();
    private final JournalDAO journalDAO = new JournalDAO();

    public void setDocument(Object document, String type) {
        this.document = document;
        this.type = type;

        // Hiển thị thông tin tài liệu và tùy chỉnh các trường hiển thị
        clearAndHideAdditionalFields(); // Ẩn tất cả các trường không cần thiết

        if (document instanceof Book book) {
            titleTextField.setText(book.getTitle());
            authorTextField.setText(book.getAuthor());
            additionalField1.setText(book.getISBN());
            additionalField1Label.setText("ISBN:");
            additionalField1.setVisible(true);
            additionalField1Label.setVisible(true);

            additionalField2.setText(String.valueOf(book.getEdition()));
            additionalField2Label.setText("Edition:");
            additionalField2.setVisible(true);
            additionalField2Label.setVisible(true);

            additionalField3.setText(String.valueOf(book.getQuantityInStock()));
            additionalField3Label.setText("Quantity In Stock:");
            additionalField3.setVisible(true);
            additionalField3Label.setVisible(true);

            additionalField4.setText(book.getGenre());
            additionalField4Label.setText("Genre:");
            additionalField4.setVisible(true);
            additionalField4Label.setVisible(true);
        } else if (document instanceof Magazine magazine) {
            titleTextField.setText(magazine.getTitle());
            authorTextField.setText(magazine.getAuthor());
            additionalField1.setText(magazine.getPublishNumber());
            additionalField1Label.setText("Publish Number:");
            additionalField1.setVisible(true);
            additionalField1Label.setVisible(true);

            additionalField2.setText(magazine.getMonth());
            additionalField2Label.setText("Month:");
            additionalField2.setVisible(true);
            additionalField2Label.setVisible(true);
        } else if (document instanceof Thesis thesis) {
            titleTextField.setText(thesis.getTitle());
            authorTextField.setText(thesis.getAuthor());
            additionalField1.setText(thesis.getUniversity());
            additionalField1Label.setText("University:");
            additionalField1.setVisible(true);
            additionalField1Label.setVisible(true);

            additionalField2.setText(thesis.getSupervisor());
            additionalField2Label.setText("Supervisor:");
            additionalField2.setVisible(true);
            additionalField2Label.setVisible(true);

            additionalField3.setText(thesis.getField());
            additionalField3Label.setText("Field:");
            additionalField3.setVisible(true);
            additionalField3Label.setVisible(true);
        } else if (document instanceof Journal journal) {
            titleTextField.setText(journal.getTitle());
            authorTextField.setText(journal.getAuthor());
            additionalField1.setText(String.valueOf(journal.getVolume()));
            additionalField1Label.setText("Volume:");
            additionalField1.setVisible(true);
            additionalField1Label.setVisible(true);

            additionalField2.setText(String.valueOf(journal.getPublishNumber()));
            additionalField2Label.setText("Publish Number:");
            additionalField2.setVisible(true);
            additionalField2Label.setVisible(true);
        }
    }

    /**
     * Ẩn tất cả các trường bổ sung và xóa dữ liệu.
     */
    private void clearAndHideAdditionalFields() {
        additionalField1.setVisible(false);
        additionalField2.setVisible(false);
        additionalField3.setVisible(false);
        additionalField4.setVisible(false);

        additionalField1Label.setVisible(false);
        additionalField2Label.setVisible(false);
        additionalField3Label.setVisible(false);
        additionalField4Label.setVisible(false);

        additionalField1.clear();
        additionalField2.clear();
        additionalField3.clear();
        additionalField4.clear();
    }

    /**
     * Lưu các thay đổi vào tài liệu được chọn.
     */
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
                    book.setISBN(additionalField1.getText());
                    book.setEdition(Integer.parseInt(additionalField2.getText()));
                    book.setQuantityInStock(Integer.parseInt(additionalField3.getText()));
                    book.setGenre(additionalField4.getText());
                    bookDAO.updateBook(book);
                }
                case "Magazine" -> {
                    Magazine magazine = (Magazine) document;
                    magazine.setTitle(titleTextField.getText());
                    magazine.setAuthor(authorTextField.getText());
                    magazine.setPublishNumber(additionalField1.getText());
                    magazine.setMonth(additionalField2.getText());
                    magazineDAO.updateMagazine(magazine);
                }
                case "Thesis" -> {
                    Thesis thesis = (Thesis) document;
                    thesis.setTitle(titleTextField.getText());
                    thesis.setAuthor(authorTextField.getText());
                    thesis.setUniversity(additionalField1.getText());
                    thesis.setSupervisor(additionalField2.getText());
                    thesis.setField(additionalField3.getText());
                    thesisDAO.updateThesis(thesis);
                }
                case "Journal" -> {
                    Journal journal = (Journal) document;
                    journal.setTitle(titleTextField.getText());
                    journal.setAuthor(authorTextField.getText());
                    journal.setVolume(Integer.parseInt(additionalField1.getText()));
                    journal.setPublishNumber(Integer.parseInt(additionalField2.getText()));
                    journalDAO.updateJournal(journal);
                }
            }
            closeWindow();
        } catch (NumberFormatException e) {
            showErrorAlert("Invalid number format for fields like Edition, Volume, or Publish Number!");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error while saving changes!");
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void cancelUpdate() {
        closeWindow();
    }
}
