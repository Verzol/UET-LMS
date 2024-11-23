package adminController;

import controller.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import models.documents.Document;
import models.users.User;

import java.sql.*;

public class ManageUsersController {
    @FXML
    private TableView<User> tableUsers;

    @FXML
    private TableColumn<User, Integer> columnUserId;
    @FXML
    private TableColumn<User, String> columnUsername;
    @FXML
    private TableColumn<User, String> columnFirstName;
    @FXML
    private TableColumn<User, String> columnLastName;
    @FXML
    private TableColumn<User, String> columnEmail;
    @FXML
    private TableColumn<User, String> columnPhone;

    private ObservableList<User> users = FXCollections.observableArrayList();
    private Connection connection;

    public void initialize() {
        setupTableColumns();
        DatabaseConnection dbConnection = new DatabaseConnection();
        this.connection = dbConnection.getConnection();
        loadUsersFromDatabase();
    }

    private void setupTableColumns() {
        columnUserId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        columnUsername.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        columnFirstName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        columnLastName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        columnEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        columnPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        tableUsers.setItems(users);
    }

    private void loadUsersFromDatabase() {
        users.clear();
        String query = "SELECT * FROM person WHERE id IN (SELECT id FROM user)";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        5
                ));
            }
        } catch (SQLException e) {
            showAlert("Load Users Error", e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Enter the new user details:");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(400);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        dialogPane.setContent(new VBox(10, usernameField, passwordField, firstNameField, lastNameField, emailField, phoneField));

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (usernameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                    showAlert("Validation Error", "Please fill in all required fields.");
                    return;
                }
                try {
                    String insertQuery = "INSERT INTO person (username, password, first_name, last_name, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, usernameField.getText());
                    preparedStatement.setString(2, passwordField.getText());
                    preparedStatement.setString(3, firstNameField.getText());
                    preparedStatement.setString(4, lastNameField.getText());
                    preparedStatement.setString(5, emailField.getText());
                    preparedStatement.setString(6, phoneField.getText());
                    preparedStatement.executeUpdate();

                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int newUserId = generatedKeys.getInt(1);
                        String insertUserQuery = "INSERT INTO user (id, max_documents_allowed) VALUES (?, ?)";
                        PreparedStatement userStatement = connection.prepareStatement(insertUserQuery);
                        userStatement.setInt(1, newUserId);
                        userStatement.setInt(2, 5);
                        userStatement.executeUpdate();
                    }
                    loadUsersFromDatabase();
                } catch (SQLException e) {
                    showAlert("Add User Error", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleEditUser() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit User");
            dialog.setHeaderText("Edit the details of the selected user:");

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setPrefWidth(400);
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            TextField usernameField = new TextField(selectedUser.getUsername());
            TextField firstNameField = new TextField(selectedUser.getFirstName());
            TextField lastNameField = new TextField(selectedUser.getLastName());
            TextField emailField = new TextField(selectedUser.getEmail());
            TextField phoneField = new TextField(selectedUser.getPhone());

            dialogPane.setContent(new VBox(10, usernameField, firstNameField, lastNameField, emailField, phoneField));

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (usernameField.getText().isEmpty() || emailField.getText().isEmpty()) {
                        showAlert("Validation Error", "Please fill in all required fields.");
                        return;
                    }
                    try {
                        String updateQuery = "UPDATE person SET username = ?, first_name = ?, last_name = ?, email = ?, phone = ? WHERE id = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                        preparedStatement.setString(1, usernameField.getText());
                        preparedStatement.setString(2, firstNameField.getText());
                        preparedStatement.setString(3, lastNameField.getText());
                        preparedStatement.setString(4, emailField.getText());
                        preparedStatement.setString(5, phoneField.getText());
                        preparedStatement.setInt(6, selectedUser.getId());
                        preparedStatement.executeUpdate();
                        loadUsersFromDatabase();
                    } catch (SQLException e) {
                        showAlert("Edit User Error", e.getMessage());
                    }
                }
            });
        } else {
            showAlert("Selection Error", "No user selected for editing!");
        }    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Delete Confirmation");
            confirmationAlert.setHeaderText("Are you sure you want to delete this user?");
            confirmationAlert.setContentText("User: " + selectedUser.getUsername());

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        String deleteQuery = "DELETE FROM person WHERE id = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                        preparedStatement.setInt(1, selectedUser.getId());
                        preparedStatement.executeUpdate();
                        loadUsersFromDatabase();
                    } catch (SQLException e) {
                        showAlert("Delete User Error", e.getMessage());
                    }
                }
            });
        } else {
            showAlert("Selection Error", "No user selected for deletion!");
        }    }

    @FXML
    private void handleViewHistory() {
        User selectedUser = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                String query = "SELECT d.title, d.author, b.borrow_date, b.return_date " +
                        "FROM borrow_history b " +
                        "JOIN documents d ON b.document_id = d.id " +
                        "WHERE b.user_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, selectedUser.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Borrow History");
                dialog.setHeaderText("Borrow history for: " + selectedUser.getUsername());
                dialog.getDialogPane().setPrefSize(800, 600);

                TableView<ObservableList<String>> historyTable = new TableView<>();
                historyTable.getStyleClass().add("colored-table");
                TableColumn<ObservableList<String>, String> colTitle = new TableColumn<>("Title");
                TableColumn<ObservableList<String>, String> colAuthor = new TableColumn<>("Author");
                TableColumn<ObservableList<String>, String> colBorrowDate = new TableColumn<>("Borrow Date");
                TableColumn<ObservableList<String>, String> colReturnDate = new TableColumn<>("Return Date");

                colTitle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
                colAuthor.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
                colBorrowDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
                colReturnDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(3)));

                historyTable.getColumns().addAll(colTitle, colAuthor, colBorrowDate, colReturnDate);

                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(resultSet.getString("title"));
                    row.add(resultSet.getString("author"));
                    row.add(resultSet.getString("borrow_date"));
                    row.add(resultSet.getString("return_date") != null ? resultSet.getString("return_date") : "Not Returned");
                    data.add(row);
                }

                historyTable.setItems(data);
                historyTable.setPrefHeight(500);

                // Thêm nút "Close" vào Dialog
                dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

                VBox layout = new VBox(10, historyTable);
                layout.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-border-color: #CDCDCD; -fx-border-radius: 10;");

                dialog.getDialogPane().setContent(layout);

                // Đảm bảo nút Close hoạt động
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == ButtonType.CLOSE) {
                        dialog.close();
                    }
                    return null;
                });

                dialog.showAndWait();

            } catch (SQLException e) {
                showAlert("View History Error", e.getMessage());
            }
        } else {
            showAlert("Selection Error", "No user selected to view history!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
