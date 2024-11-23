package controller;

import DAO.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import models.documents.*;
import utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BorrowDocumentController {

    @FXML
    private TableView<Document> tableDocuments;
    @FXML
    private TableColumn<Document, String> columnDocumentId;
    @FXML
    private TableColumn<Document, String> columnTitle;
    @FXML
    private TableColumn<Document, String> columnAuthor;
    @FXML
    private TableColumn<Document, String> columnEdition;
    @FXML
    private TableColumn<Document, Integer> columnAvailable;
    @FXML
    private TableColumn<Document, String> columnBorrowStatus;

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private ListView<String> searchSuggestions;
    @FXML
    private ScrollPane searchSuggestionsPane;

    private ObservableList<Document> documents = FXCollections.observableArrayList();
    private Connection connection;
    private final int MAX_BORROW_LIMIT = 5;

    public void initialize() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        setupTableColumns();
        loadDocumentsFromDatabase();
    }

    private void setupTableColumns() {
        columnDocumentId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        columnTitle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        columnAuthor.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        columnEdition.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getEdition())));
        columnAvailable.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getAvailableQuantity()).asObject());
        columnBorrowStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBorrowStatus()));

        tableDocuments.setItems(documents);
    }

    private void loadDocumentsFromDatabase() {
        documents.clear();

        String query = "SELECT id, title, author, edition, quantity_in_stock, borrowed_quantity, times_borrowed FROM documents";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int edition = resultSet.getInt("edition");
                int quantityInStock = resultSet.getInt("quantity_in_stock");
                int borrowedQuantity = resultSet.getInt("borrowed_quantity");
                int timesBorrowed = resultSet.getInt("times_borrowed");

                Document document = createDocumentByType(id, title, author, edition, quantityInStock, borrowedQuantity, timesBorrowed);
                if (document != null) {
                    documents.add(document);
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load documents: " + e.getMessage());
        }
    }

    private Document createDocumentByType(String id, String title, String author, int edition, int quantityInStock, int borrowedQuantity, int timesBorrowed) {
        Document document = null;
        if (id.startsWith("BO")) {
            document = new Book(id, title, author, edition, quantityInStock, timesBorrowed, "Genre Example", 300, "ISBN123", "ImageURL");
        } else if (id.startsWith("MAG")) {
            document = new Magazine(id, title, author, edition, quantityInStock, timesBorrowed, "PublishNo", "May", "ImageURL");
        } else if (id.startsWith("THE")) {
            document = new Thesis(id, title, author, edition, quantityInStock, timesBorrowed, "University", "Supervisor", "Field");
        } else if (id.startsWith("JOU")) {
            document = new Journal(id, title, author, edition, quantityInStock, timesBorrowed, 5, 10, "ImageURL");
        }

        if (document != null) {
            document.setBorrowedQuantity(borrowedQuantity);
        }
        return document;
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            showAlert("Search Error", "Please enter a keyword to search.");
            searchSuggestionsPane.setVisible(false);
            loadDocumentsFromDatabase();
            return;
        }

        documents.clear();
        searchSuggestions.getItems().clear();
        searchSuggestionsPane.setVisible(true);

        String query = "SELECT id, title, author, edition, quantity_in_stock, borrowed_quantity, times_borrowed " +
                "FROM documents WHERE title LIKE ? OR author LIKE ? OR id LIKE ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            String likeSearchText = "%" + searchText + "%";
            preparedStatement.setString(1, likeSearchText);
            preparedStatement.setString(2, likeSearchText);
            preparedStatement.setString(3, likeSearchText);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    int edition = resultSet.getInt("edition");
                    int quantityInStock = resultSet.getInt("quantity_in_stock");
                    int borrowedQuantity = resultSet.getInt("borrowed_quantity");
                    int timesBorrowed = resultSet.getInt("times_borrowed");

                    Document document = createDocumentByType(id, title, author, edition, quantityInStock, borrowedQuantity, timesBorrowed);
                    if (document != null) {
                        documents.add(document);
                    }
                    searchSuggestions.getItems().add(id + " - " + title);
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to search documents: " + e.getMessage());
        }

        tableDocuments.setItems(documents);
    }

    @FXML
    private void handleBorrowDocument() {
        Document selectedDocument = tableDocuments.getSelectionModel().getSelectedItem();
        if (selectedDocument != null) {
            if (selectedDocument.getAvailableQuantity() <= 0) {
                showAlert("Error", "This document is currently unavailable for borrowing.");
                return;
            }

            try {
                int currentUserId = getCurrentUserId();
                String documentId = selectedDocument.getId();

                String checkExistingBorrowQuery = "SELECT COUNT(*) AS borrowed_count FROM borrow_history WHERE user_id = ? AND document_id = ? AND return_date IS NULL";
                PreparedStatement checkExistingStatement = connection.prepareStatement(checkExistingBorrowQuery);
                checkExistingStatement.setInt(1, currentUserId);
                checkExistingStatement.setString(2, documentId);
                ResultSet checkExistingResult = checkExistingStatement.executeQuery();

                if (checkExistingResult.next() && checkExistingResult.getInt("borrowed_count") > 0) {
                    showAlert("Error", "You have already borrowed this document.");
                    return;
                }

                String countQuery = "SELECT COUNT(*) AS borrowed_count FROM borrow_history WHERE user_id = ? AND return_date IS NULL";
                PreparedStatement countStatement = connection.prepareStatement(countQuery);
                countStatement.setInt(1, currentUserId);
                ResultSet countResult = countStatement.executeQuery();

                if (countResult.next() && countResult.getInt("borrowed_count") >= MAX_BORROW_LIMIT) {
                    showAlert("Error", "You cannot borrow more than " + MAX_BORROW_LIMIT + " documents at once.");
                    return;
                }

                String updateDocumentQuery = "UPDATE documents SET borrowed_quantity = borrowed_quantity + 1, times_borrowed = times_borrowed + 1 WHERE id = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateDocumentQuery);
                updateStatement.setString(1, documentId);
                updateStatement.executeUpdate();

                String insertHistoryQuery = "INSERT INTO borrow_history (user_id, document_id, borrow_date) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertHistoryQuery);
                insertStatement.setInt(1, currentUserId);
                insertStatement.setString(2, documentId);
                insertStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                insertStatement.executeUpdate();

                selectedDocument.incrementTimesBorrowed();
                loadDocumentsFromDatabase();

                showAlert("Success", "Document borrowed successfully!");
            } catch (SQLException e) {
                showAlert("Error", "Failed to borrow document: " + e.getMessage());
            }
        } else {
            showAlert("Error", "No document selected.");
        }
    }

    private int getCurrentUserId() {
        return SessionManager.getCurrentUserId();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleSuggestionClick() {
        String selectedSuggestion = searchSuggestions.getSelectionModel().getSelectedItem();
        if (selectedSuggestion != null) {
            searchSuggestionsPane.setVisible(false);

            String id = selectedSuggestion.split(" - ")[0].trim();

            documents.clear();

            String query = "SELECT id, title, author, edition, quantity_in_stock, borrowed_quantity, times_borrowed FROM documents WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery())
                {
                    if (resultSet.next()) {
                        String title = resultSet.getString("title");
                        String author = resultSet.getString("author");

                        int edition = resultSet.getInt("edition");
                        int quantityInStock = resultSet.getInt("quantity_in_stock");
                        int borrowedQuantity = resultSet.getInt("borrowed_quantity");
                        int timesBorrowed = resultSet.getInt("times_borrowed");

                        Document document = createDocumentByType(id, title, author, edition, quantityInStock, borrowedQuantity, timesBorrowed);
                        if (document != null) {
                            documents.add(document);
                        }
                    }
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to load document: " + e.getMessage());
            }

            tableDocuments.setItems(documents);
        }
    }

    @FXML
    private void handleSearchKeyPress(KeyEvent event) {
        String searchText = searchField.getText();
        if (searchText.isEmpty()) {
            searchSuggestionsPane.setVisible(false);
        } else {
            handleSearch();
        }
    }
}
