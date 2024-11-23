package DAO;

import models.documents.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentDAO {
    private Connection connection;

    public DocumentDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addDocument(Document document) {
        String query = "INSERT INTO documents (id, title, author, edition, quantity_in_stock, borrowed_quantity, times_borrowed) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, document.getId());
            statement.setString(2, document.getTitle());
            statement.setString(3, document.getAuthor());
            statement.setInt(4, document.getEdition());
            statement.setInt(5, document.getQuantityInStock());
            statement.setInt(6, document.getBorrowedQuantity());
            statement.setInt(7, document.getTimesBorrowed());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDocument(String id) {
        String query = "DELETE FROM documents WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean exists(String id) {
        String query = "SELECT id FROM documents WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDocument(Document document) {
        String query = "UPDATE documents SET title = ?, author = ?, edition = ?, quantity_in_stock = ?, borrowed_quantity = ?, times_borrowed = ? " +
                "WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, document.getTitle());
            statement.setString(2, document.getAuthor());
            statement.setInt(3, document.getEdition());
            statement.setInt(4, document.getQuantityInStock());
            statement.setInt(5, document.getBorrowedQuantity());
            statement.setInt(6, document.getTimesBorrowed());
            statement.setString(7, document.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
