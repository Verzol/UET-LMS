package DAO;

import models.documents.Journal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JournalDAO {
    private Connection connection;

    public JournalDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addJournal(Journal journal) {
        String queryDocuments = "INSERT INTO documents (id, title, author, edition, " +
                "quantity_in_stock, borrowed_quantity) VALUES (?, ?, ?, ?, ?, ?)";
        String queryJournals = "INSERT INTO journals (id, volume, publish_number, image_url) VALUES (?, ?, ?, ?)";

        try (PreparedStatement docStatement = connection.prepareStatement(queryDocuments);
             PreparedStatement journalStatement = connection.prepareStatement(queryJournals)) {

            docStatement.setString(1, journal.getId());
            docStatement.setString(2, journal.getTitle());
            docStatement.setString(3, journal.getAuthor());
            docStatement.setInt(4, journal.getEdition());
            docStatement.setInt(5, journal.getQuantityInStock());
            docStatement.setInt(6, journal.getBorrowedQuantity());
            docStatement.executeUpdate();

            journalStatement.setString(1, journal.getId());
            journalStatement.setInt(2, journal.getVolume());
            journalStatement.setInt(3, journal.getPublishNumber());
            journalStatement.setString(4, journal.getImageUrl());
            journalStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Journal> getAllJournals() {
        List<Journal> journals = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, "
                + "j.volume, j.publish_number, j.image_url "
                + "FROM documents d JOIN journals j ON d.id = j.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int edition = resultSet.getInt("edition");
                int quantityInStock = resultSet.getInt("quantity_in_stock");
                int borrowedQuantity = resultSet.getInt("borrowed_quantity");
                int volume = resultSet.getInt("volume");
                int publishNumber = resultSet.getInt("publish_number");
                String imageUrl = resultSet.getString("image_url");

                Journal journal = new Journal(id, title, author, edition, quantityInStock, volume, publishNumber, imageUrl);
                journal.setBorrowedQuantity(borrowedQuantity);
                journals.add(journal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return journals;
    }

    public boolean updateJournal(Journal journal) {
        String queryDocuments = "UPDATE documents SET title = ?, author = ?, edition = ?"
                + ", quantity_in_stock = ?, borrowed_quantity = ? WHERE id = ?";
        String queryJournals = "UPDATE journals SET volume = ?, publish_number = ?, image_url = ? WHERE id = ?";

        try (PreparedStatement docStatement = connection.prepareStatement(queryDocuments);
             PreparedStatement journalStatement = connection.prepareStatement(queryJournals)) {

            docStatement.setString(1, journal.getTitle());
            docStatement.setString(2, journal.getAuthor());
            docStatement.setInt(3, journal.getEdition());
            docStatement.setInt(4, journal.getQuantityInStock());
            docStatement.setInt(5, journal.getBorrowedQuantity());
            docStatement.setString(6, journal.getId());
            docStatement.executeUpdate();

            journalStatement.setInt(1, journal.getVolume());
            journalStatement.setInt(2, journal.getPublishNumber());
            journalStatement.setString(3, journal.getImageUrl());
            journalStatement.setString(4, journal.getId());
            journalStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteJournal(String id) {
        String queryDocuments = "DELETE FROM documents WHERE id = ?";
        String queryJournals = "DELETE FROM journals WHERE id = ?";

        try (PreparedStatement journalStatement = connection.prepareStatement(queryJournals);
             PreparedStatement docStatement = connection.prepareStatement(queryDocuments)) {

            journalStatement.setString(1, id);
            journalStatement.executeUpdate();

            docStatement.setString(1, id);
            return docStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean exists(String id) {
        String query = "SELECT id FROM documents WHERE id = ? AND EXISTS (SELECT id FROM journals WHERE id = ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            statement.setString(2, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
