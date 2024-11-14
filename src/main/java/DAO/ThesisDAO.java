package DAO;

import models.documents.Thesis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThesisDAO {
    private Connection connection;

    public ThesisDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addThesis(Thesis thesis) {
        String queryDocuments = "INSERT INTO documents (id, title, author, edition, quantity_in_stock" +
                ", borrowed_quantity) VALUES (?, ?, ?, ?, ?, ?)";
        String queryThesis = "INSERT INTO thesis (id, university, supervisor, field) VALUES (?, ?, ?, ?)";

        try (PreparedStatement docStatement = connection.prepareStatement(queryDocuments);
             PreparedStatement thesisStatement = connection.prepareStatement(queryThesis)) {

            docStatement.setString(1, thesis.getId());
            docStatement.setString(2, thesis.getTitle());
            docStatement.setString(3, thesis.getAuthor());
            docStatement.setInt(4, thesis.getEdition());
            docStatement.setInt(5, thesis.getQuantityInStock());
            docStatement.setInt(6, thesis.getBorrowedQuantity());
            docStatement.executeUpdate();

            thesisStatement.setString(1, thesis.getId());
            thesisStatement.setString(2, thesis.getUniversity());
            thesisStatement.setString(3, thesis.getSupervisor());
            thesisStatement.setString(4, thesis.getField());
            thesisStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Thesis> getAllThesis() {
        List<Thesis> thesisArrayList = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, "
                + "t.university, t.supervisor, t.field "
                + "FROM documents d JOIN thesis t ON d.id = t.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int edition = resultSet.getInt("edition");
                int quantityInStock = resultSet.getInt("quantity_in_stock");
                int borrowedQuantity = resultSet.getInt("borrowed_quantity");
                String university = resultSet.getString("university");
                String supervisor = resultSet.getString("supervisor");
                String field = resultSet.getString("field");

                Thesis thesis = new Thesis(id, title, author, edition, quantityInStock, university, supervisor, field);
                thesis.setBorrowedQuantity(borrowedQuantity);
                thesisArrayList.add(thesis);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return thesisArrayList;
    }

    public boolean updateThesis(Thesis thesis) {
        String queryDocuments = "UPDATE documents SET title = ?, author = ?, edition = ?,"
                + " quantity_in_stock = ?, borrowed_quantity = ? WHERE id = ?";
        String queryThesis = "UPDATE thesis SET university = ?, supervisor = ?, field = ? WHERE id = ?";

        try (PreparedStatement docStatement = connection.prepareStatement(queryDocuments);
             PreparedStatement thesisStatement = connection.prepareStatement(queryThesis)) {

            docStatement.setString(1, thesis.getTitle());
            docStatement.setString(2, thesis.getAuthor());
            docStatement.setInt(3, thesis.getEdition());
            docStatement.setInt(4, thesis.getQuantityInStock());
            docStatement.setInt(5, thesis.getBorrowedQuantity());
            docStatement.setString(6, thesis.getId());
            docStatement.executeUpdate();

            thesisStatement.setString(1, thesis.getUniversity());
            thesisStatement.setString(2, thesis.getSupervisor());
            thesisStatement.setString(3, thesis.getField());
            thesisStatement.setString(4, thesis.getId());
            thesisStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteThesis(String id) {
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
        String query = "SELECT id FROM documents WHERE id = ? AND EXISTS (SELECT id FROM thesis WHERE id = ?)";

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
