package DAO;

import models.documents.Magazine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MagazineDAO {
    private Connection connection;
    private DocumentDAO documentDAO;

    public MagazineDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.documentDAO = new DocumentDAO();
    }

    public boolean addMagazine(Magazine magazine) {
        if (!documentDAO.addDocument(magazine)) {
            return false;
        }
        String query = "INSERT INTO magazines (id, publish_number, month, image_url) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, magazine.getId());
            statement.setString(2, magazine.getPublishNumber());
            statement.setString(3, magazine.getMonth());
            statement.setString(4, magazine.getImageUrl());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Magazine> getAllMagazines() {
        List<Magazine> magazines = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, d.times_borrowed, " +
                "m.publish_number, m.month, m.image_url " +
                "FROM documents d JOIN magazines m ON d.id = m.id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                magazines.add(mapToMagazine(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return magazines;
    }

    public boolean updateMagazine(Magazine magazine) {
        if (!documentDAO.updateDocument(magazine)) {
            return false;
        }
        String query = "UPDATE magazines SET publish_number = ?, month = ?, image_url = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, magazine.getPublishNumber());
            statement.setString(2, magazine.getMonth());
            statement.setString(3, magazine.getImageUrl());
            statement.setString(4, magazine.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMagazine(String id) {
        String query = "DELETE FROM magazines WHERE id = ?";
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
        String query = "SELECT m.id FROM magazines m JOIN documents d ON m.id = d.id WHERE m.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Magazine mapToMagazine(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        int edition = resultSet.getInt("edition");
        int quantityInStock = resultSet.getInt("quantity_in_stock");
        int borrowedQuantity = resultSet.getInt("borrowed_quantity");
        int timesBorrowed = resultSet.getInt("times_borrowed");
        String publishNumber = resultSet.getString("publish_number");
        String month = resultSet.getString("month");
        String imageUrl = resultSet.getString("image_url");

        Magazine magazine = new Magazine(id, title, author, edition, quantityInStock, timesBorrowed, publishNumber, month, imageUrl);
        magazine.setBorrowedQuantity(borrowedQuantity);
        return magazine;
    }

    public List<Magazine> searchMagazines(String keyword) {
        List<Magazine> magazines = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, d.times_borrowed, " +
                "m.publish_number, m.month, m.image_url " +
                "FROM documents d JOIN magazines m ON d.id = m.id " +
                "WHERE d.id LIKE ? OR d.title LIKE ? OR d.author LIKE ? OR m.publish_number LIKE ? OR m.month LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                magazines.add(mapToMagazine(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return magazines;
    }
}
