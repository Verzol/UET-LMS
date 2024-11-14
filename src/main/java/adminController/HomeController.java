package adminController;

import DAO.BookDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {
    @FXML
    private Label totalUsers;

    @FXML
    private Label totalDocuments;

    @FXML
    private Label totalIssuedDocuments;

    @FXML
    private Label totalBorrowedDocuments;

    private BookDAO bookDAO = new BookDAO();

    @FXML
    public void initialize() {
        updateDashboard();
    }

    private void updateDashboard() {

    }
}
