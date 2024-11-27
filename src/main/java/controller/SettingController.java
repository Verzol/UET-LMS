package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class SettingController {

    @FXML
    private ImageView avatarImage;

    @FXML
    private Label userName;

    @FXML
    private Label userEmail;

    @FXML
    private Label userPhone;

    @FXML
    private Label totalBookBorrowed;

    @FXML
    private PieChart categoryPieChart;

    private final Connection connection;

    public SettingController() {
        this.connection = new DatabaseConnection().getConnection();
    }

    @FXML
    public void initialize() {
        loadUserInfo();
        initializeUserDashboard();
        setupGenrePieChart();
    }

    private String loadAvatarFileName() {
        String username = SessionManager.getCurrentUsername();
        if (username != null && !username.isEmpty()) {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            try (Connection connection = databaseConnection.getConnection()) {
                if (connection != null) {
                    String query = "SELECT avatar FROM person WHERE username = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, username);
                        ResultSet resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            return resultSet.getString("avatar");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void initializeUserDashboard() {
        String avatarFileName = loadAvatarFileName();
        if (avatarFileName != null) {
            File avatarFile = new File("avatars/" + avatarFileName);
            if (avatarFile.exists()) {
                Image avatar = new Image(avatarFile.toURI().toString());
                avatarImage.setImage(avatar);
                setCircularAvatar(avatar);
            }
        }
    }

    private void loadUserInfo() {
        int userId = SessionManager.getCurrentUserId();
        String query = """
                SELECT 
                    p.first_name, p.last_name, p.email, p.phone, p.avatar, 
                    COUNT(bh.id) AS borrowed_count
                FROM 
                    person p
                LEFT JOIN 
                    user u ON p.id = u.id
                LEFT JOIN 
                    borrow_history bh ON u.id = bh.user_id
                WHERE 
                    p.id = ?
                GROUP BY 
                    p.id, p.first_name, p.last_name, p.email, p.phone, p.avatar;
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    populateUserInfo(rs);
                } else {
                    showAlert("User Not Found", "No user data found for the provided user ID.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while loading user information.");
        }
    }

    private void populateUserInfo(ResultSet rs) throws Exception {
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String avatarPath = rs.getString("avatar");
        int borrowedCount = rs.getInt("borrowed_count");

        userName.setText((firstName != null && lastName != null) ? firstName + " " + lastName : "Unknown User");
        userEmail.setText(email != null ? "Email: " + email : "N/A");
        userPhone.setText(phone != null ? "Phone: " + phone : "N/A");
        totalBookBorrowed.setText(String.valueOf("Borrowed Quantity: " + borrowedCount));

        loadAvatar(avatarPath);
    }

    private void loadAvatar(String avatarPath) {
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                File avatarFile = new File(avatarPath);
                if (!avatarFile.exists()) {
                    useDefaultAvatar();
                    return;
                }
                Image avatarImageFromDB = new Image(avatarFile.toURI().toString());
                avatarImage.setImage(avatarImageFromDB);
                setCircularAvatar(avatarImageFromDB);
            } catch (Exception e) {
                useDefaultAvatar();
                showAlert("Image Load Error", "Could not load the avatar image from the provided path.");
            }
        } else {
            useDefaultAvatar();
        }
    }

    private void setupGenrePieChart() {
        int userId = SessionManager.getCurrentUserId();
        String query = """
        SELECT b.genre, COUNT(*) AS count
        FROM borrow_history bh
        JOIN books b ON bh.document_id = b.id
        WHERE bh.user_id = ? AND (bh.status = 1 OR bh.status = 0)
        GROUP BY b.genre;
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    String genre = resultSet.getString("genre");
                    int count = resultSet.getInt("count");

                    pieChartData.add(new PieChart.Data(genre, count));
                }

                categoryPieChart.setData(pieChartData);
            }
        } catch (Exception e) {
            showAlert("PieChart Error", "Failed to set up the genre PieChart: " + e.getMessage());
        }
    }

    private void useDefaultAvatar() {
        Image defaultAvatar = new Image(getClass()
                .getResourceAsStream("/images/avatar_1732543662041.png"));
        avatarImage.setImage(defaultAvatar);
        setCircularAvatar(defaultAvatar);
    }


    private void setCircularAvatar(Image image) {
        double size = Math.min(avatarImage.getFitWidth(), avatarImage.getFitHeight());
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        avatarImage.setClip(clip);
        avatarImage.setImage(image);
    }

    @FXML
    private void handleChangePassword() {
        loadUI("change_password.fxml");
    }

    @FXML
    private void handleUpdateProfile() {
        loadUI("update_profile.fxml");
    }

    @FXML
    private Button changeAvatarButton;

    @FXML
    private void changeAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(avatarImage.getScene().getWindow());

        if (selectedFile != null) {
            if (selectedFile.length() > 5 * 1024 * 1024) {
                showAlert("File Too Large", "Please select an image smaller than 5MB.");
                return;
            }
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setHeaderText("Change Avatar");
            confirmationAlert.setContentText("Are you sure you want to change your avatar?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                saveNewAvatar(selectedFile);
            }
        }
    }

    private UserDashboardController dashboardController;

    private void saveNewAvatar(File selectedFile) {
        String imagesDir = "avatars";
        File dir = new File(imagesDir);

        if (!dir.exists() && !dir.mkdirs()) {
            showAlert("Error", "Unable to create directory for avatars.");
            return;
        }

        String fileName = "avatar_" + System.currentTimeMillis() + ".png";
        File destinationFile = new File(dir, fileName);

        try {
            Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            SessionManager.setCurrentAvatarPath("avatars/" + fileName);
            initializeUserAvatar();
            showAlert("Success", "Avatar changed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to save avatar: " + e.getMessage());
        }
    }

    private void updateAvatarInDatabase(String fileName) {
        int userId = SessionManager.getCurrentUserId();
        String query = "UPDATE person SET avatar = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "avatars/" + fileName);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while saving the avatar to the database.");
        }
    }

    private void initializeUserAvatar() {
        String avatarPath = SessionManager.getCurrentAvatarPath();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            File avatarFile = new File(avatarPath);
            if (avatarFile.exists()) {
                Image avatar = new Image(avatarFile.toURI().toString());
                avatarImage.setImage(avatar);
            } else {
                useDefaultAvatar();
            }
        } else {
            useDefaultAvatar();
        }
    }

    private void loadUI(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Load Error", "Unable to load the requested page: " + fxmlFile);
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
