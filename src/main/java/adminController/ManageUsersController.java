package adminController;

import controller.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
        columnUserId.setCellValueFactory(data
                -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        columnUsername.setCellValueFactory(data
                -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        columnFirstName.setCellValueFactory(data
                -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirstName()));
        columnLastName.setCellValueFactory(data
                -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLastName()));
        columnEmail.setCellValueFactory(data
                -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        columnPhone.setCellValueFactory(data
                -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
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
        Dialog<ButtonType> dialog = createStyledDialog("Add User", "Enter the new user details:");

        VBox formLayout = new VBox(10);
        formLayout.getStyleClass().add("vbox-padding");

        TextField usernameField = createStyledTextField("Username");
        TextField passwordField = createStyledTextField("Password");
        TextField firstNameField = createStyledTextField("First Name");
        TextField lastNameField = createStyledTextField("Last Name");
        TextField emailField = createStyledTextField("Email");
        TextField phoneField = createStyledTextField("Phone");

        formLayout.getChildren().addAll(
                createLabeledField("Username", usernameField),
                createLabeledField("Password", passwordField),
                createLabeledField("First Name", firstNameField),
                createLabeledField("Last Name", lastNameField),
                createLabeledField("Email", emailField),
                createLabeledField("Phone", phoneField)
        );

        dialog.getDialogPane().setContent(formLayout);

        dialog.showAndWait().ifPresent(response -> {
            if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
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
            Dialog<ButtonType> dialog = createStyledDialog("Edit User", "Edit the details of the selected user:");

            TextField usernameField = createStyledTextField(selectedUser.getUsername());
            TextField firstNameField = createStyledTextField(selectedUser.getFirstName());
            TextField lastNameField = createStyledTextField(selectedUser.getLastName());
            TextField emailField = createStyledTextField(selectedUser.getEmail());
            TextField phoneField = createStyledTextField(selectedUser.getPhone());

            VBox formLayout = new VBox(10,
                    createLabeledField("Username", usernameField),
                    createLabeledField("First Name", firstNameField),
                    createLabeledField("Last Name", lastNameField),
                    createLabeledField("Email", emailField),
                    createLabeledField("Phone", phoneField)
            );
            formLayout.getStyleClass().add("vbox-padding");

            dialog.getDialogPane().setContent(formLayout);

            dialog.showAndWait().ifPresent(response -> {
                if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
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
        }
    }

    private Dialog<ButtonType> createStyledDialog(String title, String headerText) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        okButton.getStyleClass().add("save-button");
        cancelButton.getStyleClass().add("cancel-button");

        return dialog;
    }

    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle("-fx-font-size: 14px; -fx-border-radius: 4px; -fx-background-radius: 4px;");
        return textField;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
        }
    }

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

                Stage stage = new Stage();
                stage.setTitle("Borrow History: " + selectedUser.getUsername());
                stage.setWidth(600);
                stage.setHeight(400);

                TableView<ObservableList<String>> historyTable = new TableView<>();
                historyTable.getStyleClass().add("colored-table");

                TableColumn<ObservableList<String>, String> colTitle = new TableColumn<>("Title");
                TableColumn<ObservableList<String>, String> colAuthor = new TableColumn<>("Author");
                TableColumn<ObservableList<String>, String> colBorrowDate = new TableColumn<>("Borrow Date");
                TableColumn<ObservableList<String>, String> colReturnDate = new TableColumn<>("Return Date");

                setupColumnWithWrappingAndTooltip(colTitle, 0);
                setupColumnWithWrappingAndTooltip(colAuthor, 1);
                setupColumnWithWrappingAndTooltip(colBorrowDate, 2);
                setupColumnWithWrappingAndTooltip(colReturnDate, 3);

                colTitle.setStyle("-fx-background-color: transparent;");
                colAuthor.setStyle("-fx-background-color: transparent;");
                colBorrowDate.setStyle("-fx-background-color: transparent;");
                colReturnDate.setStyle("-fx-background-color: transparent;");

                historyTable.getColumns().addAll(colTitle, colAuthor, colBorrowDate, colReturnDate);

                historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

                ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    ObservableList<String> row = FXCollections.observableArrayList();
                    row.add(resultSet.getString("title"));
                    row.add(resultSet.getString("author"));
                    row.add(resultSet.getString("borrow_date"));
                    row.add(resultSet.getString("return_date") != null
                            ? resultSet.getString("return_date")
                            : "Not Returned");
                    data.add(row);
                }

                historyTable.setItems(data);
                historyTable.setPrefHeight(400);
                historyTable.setPrefWidth(600);

                VBox layout = new VBox(10, historyTable);
                layout.setStyle("-fx-padding: 10; -fx-background-color: white; " +
                        "-fx-border-color: #CCCCCC; -fx-border-radius: 10;");
                layout.setPrefSize(600, 400);

                Scene scene = new Scene(layout, 600, 400);
                scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
                stage.setScene(scene);
                stage.show();

            } catch (SQLException e) {
                showAlert("View History Error", e.getMessage());
            }
        } else {
            showAlert("Selection Error", "No user selected to view history!");
        }
    }

    private void setupColumnWithWrappingAndTooltip(TableColumn<ObservableList<String>, String> column, int index) {
        column.setCellFactory(tc -> {
            TableCell<ObservableList<String>, String> cell = new TableCell<>() {
                private final Text text = new Text();

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setTooltip(null);
                    } else {
                        text.setText(item);
                        text.wrappingWidthProperty().bind(column.widthProperty().subtract(10)); // Bọc văn bản
                        setGraphic(text);

                        Tooltip tooltip = new Tooltip(item);
                        setTooltip(tooltip);
                    }
                }
            };
            return cell;
        });

        column.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().get(index))
        );
    }

    private HBox createLabeledField(String labelText, TextField textField) {
        Label label = new Label(labelText + ":");
        label.getStyleClass().add("header-label");
        label.setPrefWidth(120);
        textField.setPrefWidth(250);

        HBox hBox = new HBox(10, label, textField);
        hBox.setStyle("-fx-alignment: center-left;");
        return hBox;
    }
}
