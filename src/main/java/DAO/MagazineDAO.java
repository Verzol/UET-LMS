package DAO;

import models.documents.Magazine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MagazineDAO {
    private Connection connection;

    public MagazineDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addMagazine(Magazine magazine) {
        String queryDocuments = "INSERT INTO documents (id, title, author, edition, quantity_in_stock, borrowed_quantity) VALUES (?, ?, ?, ?, ?, ?)";
        String queryMagazines = "INSERT INTO magazines (id, publish_number, month) VALUES (?, ?, ?)";

        try (PreparedStatement docStatement = connection.prepareStatement(queryDocuments);
             PreparedStatement magazineStatement = connection.prepareStatement(queryMagazines)) {

            docStatement.setString(1, magazine.getId());
            docStatement.setString(2, magazine.getTitle());
            docStatement.setString(3, magazine.getAuthor());
            docStatement.setInt(4, magazine.getEdition());
            docStatement.setInt(5, magazine.getQuantityInStock());
            docStatement.setInt(6, magazine.getBorrowedQuantity());
            docStatement.executeUpdate();

            magazineStatement.setString(1, magazine.getId());
            magazineStatement.setString(2, magazine.getPublishNumber());
            magazineStatement.setString(3, magazine.getMonth());
            magazineStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Magazine> getAllMagazines() {
        List<Magazine> magazines = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, "
                + "m.publish_number, m.month "
                + "FROM documents d JOIN magazines m ON d.id = m.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int edition = resultSet.getInt("edition");
                int quantityInStock = resultSet.getInt("quantity_in_stock");
                int borrowedQuantity = resultSet.getInt("borrowed_quantity");
                String publishNumber = resultSet.getString("publish_number");
                String month = resultSet.getString("month");

                Magazine magazine = new Magazine(id, title, author, edition, quantityInStock, publishNumber, month);
                magazine.setBorrowedQuantity(borrowedQuantity);
                magazines.add(magazine);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return magazines;
    }

    public boolean updateMagazine(Magazine magazine) {
        String queryDocuments = "UPDATE documents SET title = ?, author = ?, edition = ?, quantity_in_stock = ?, borrowed_quantity = ? WHERE id = ?";
        String queryMagazines = "UPDATE magazines SET publish_number = ?, month = ? WHERE id = ?";

        try (PreparedStatement docStatement = connection.prepareStatement(queryDocuments);
             PreparedStatement magazineStatement = connection.prepareStatement(queryMagazines)) {

            docStatement.setString(1, magazine.getTitle());
            docStatement.setString(2, magazine.getAuthor());
            docStatement.setInt(3, magazine.getEdition());
            docStatement.setInt(4, magazine.getQuantityInStock());
            docStatement.setInt(5, magazine.getBorrowedQuantity());
            docStatement.setString(6, magazine.getId());
            docStatement.executeUpdate();

            magazineStatement.setString(1, magazine.getPublishNumber());
            magazineStatement.setString(2, magazine.getMonth());
            magazineStatement.setString(3, magazine.getId());
            magazineStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteMagazine(String id) {
        String queryDocuments = "DELETE FROM documents WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(queryDocuments)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean exists(String id) {
        String query = "SELECT id FROM documents WHERE id = ? AND EXISTS (SELECT id FROM magazines WHERE id = ?)";

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
