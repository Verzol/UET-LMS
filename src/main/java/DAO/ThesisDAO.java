package DAO;

import models.documents.Magazine;
import models.documents.Thesis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThesisDAO {
    private Connection connection;
    private DocumentDAO documentDAO;

    public ThesisDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.documentDAO = new DocumentDAO();
    }

    public boolean addThesis(Thesis thesis) {
        if (!documentDAO.addDocument(thesis)) {
            return false;
        }

        String query = "INSERT INTO thesis (id, university, supervisor, field) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, thesis.getId());
            statement.setString(2, thesis.getUniversity());
            statement.setString(3, thesis.getSupervisor());
            statement.setString(4, thesis.getField());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Thesis> getAllThesis() {
        List<Thesis> theses = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, d.times_borrowed, " +
                "t.university, t.supervisor, t.field " +
                "FROM documents d JOIN thesis t ON d.id = t.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int edition = resultSet.getInt("edition");
                int quantityInStock = resultSet.getInt("quantity_in_stock");
                int borrowedQuantity = resultSet.getInt("borrowed_quantity");
                int timesBorrowed = resultSet.getInt("times_borrowed");
                String university = resultSet.getString("university");
                String supervisor = resultSet.getString("supervisor");
                String field = resultSet.getString("field");

                Thesis thesis = new Thesis(id, title, author, edition, quantityInStock, timesBorrowed, university, supervisor, field);
                thesis.setBorrowedQuantity(borrowedQuantity);
                theses.add(thesis);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return theses;
    }

    public boolean updateThesis(Thesis thesis) {
        if (!documentDAO.updateDocument(thesis)) {
            return false;
        }

        String query = "UPDATE thesis SET university = ?, supervisor = ?, field = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, thesis.getUniversity());
            statement.setString(2, thesis.getSupervisor());
            statement.setString(3, thesis.getField());
            statement.setString(4, thesis.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteThesis(String id) {
        String query = "DELETE FROM thesis WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            statement.executeUpdate();
            return documentDAO.deleteDocument(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean exists(String id) {
        String query = "SELECT t.id FROM thesis t JOIN documents d ON t.id = d.id WHERE t.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private Thesis mapToThesis(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        int edition = resultSet.getInt("edition");
        int quantityInStock = resultSet.getInt("quantity_in_stock");
        int borrowedQuantity = resultSet.getInt("borrowed_quantity");
        int timesBorrowed = resultSet.getInt("times_borrowed");
        String university = resultSet.getString("university");
        String supervisor = resultSet.getString("supervisor");
        String field = resultSet.getString("field");

        Thesis thesis = new Thesis(id, title, author, edition, quantityInStock, timesBorrowed, university, supervisor, field);
        thesis.setBorrowedQuantity(borrowedQuantity);
        return thesis;
    }

    public List<Thesis> searchThesis(String keyword) {
        List<Thesis> thesis = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, d.times_borrowed, " +
                "t.university, t.supervisor, t.field " +
                "FROM documents d JOIN thesis t ON d.id = t.id " +
                "WHERE d.id LIKE ? OR d.title LIKE ? OR d.author LIKE ? OR t.university LIKE ? OR t.field LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                thesis.add(mapToThesis(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return thesis;
    }
}
