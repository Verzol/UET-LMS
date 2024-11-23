package DAO;

import models.documents.Journal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JournalDAO {
    private Connection connection;
    private DocumentDAO documentDAO;

    public JournalDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.documentDAO = new DocumentDAO();
    }

    public boolean addJournal(Journal journal) {
        if (!documentDAO.addDocument(journal)) {
            return false;
        }
        String query = "INSERT INTO journals (id, volume, publish_number, image_url) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, journal.getId());
            statement.setInt(2, journal.getVolume());
            statement.setInt(3, journal.getPublishNumber());
            statement.setString(4, journal.getImageUrl());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Journal> getAllJournals() {
        List<Journal> journals = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, d.times_borrowed, " +
                "j.volume, j.publish_number, j.image_url " +
                "FROM documents d JOIN journals j ON d.id = j.id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                journals.add(mapToJournal(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return journals;
    }

    public boolean updateJournal(Journal journal) {
        if (!documentDAO.updateDocument(journal)) {
            return false;
        }
        String query = "UPDATE journals SET volume = ?, publish_number = ?, image_url = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, journal.getVolume());
            statement.setInt(2, journal.getPublishNumber());
            statement.setString(3, journal.getImageUrl());
            statement.setString(4, journal.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteJournal(String id) {
        String query = "DELETE FROM journals WHERE id = ?";
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
        String query = "SELECT j.id FROM journals j JOIN documents d ON j.id = d.id WHERE j.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Journal mapToJournal(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        int edition = resultSet.getInt("edition");
        int quantityInStock = resultSet.getInt("quantity_in_stock");
        int borrowedQuantity = resultSet.getInt("borrowed_quantity");
        int timesBorrowed = resultSet.getInt("times_borrowed");
        int volume = resultSet.getInt("volume");
        int publishNumber = resultSet.getInt("publish_number");
        String imageUrl = resultSet.getString("image_url");

        Journal journal = new Journal(id, title, author, edition, quantityInStock, timesBorrowed, volume, publishNumber, imageUrl);
        journal.setBorrowedQuantity(borrowedQuantity);
        return journal;
    }

    public List<Journal> searchJournal(String keyword) {
        List<Journal> journals = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, d.times_borrowed, " +
                "j.volume, j.publish_number, j.image_url " +
                "FROM documents d JOIN journals j ON d.id = j.id " +
                "WHERE d.id LIKE ? OR d.title LIKE ? OR d.author LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                journals.add(mapToJournal(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return journals;
    }
}
