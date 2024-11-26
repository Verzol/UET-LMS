package service;

import controller.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DocumentsService {

    public static class Document {
        private String id;
        private String title;

        public Document(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

    /**
     * Lấy danh sách các sách từ bảng documents.
     */
    public List<Document> getAllDocuments() {
        List<Document> documents = new ArrayList<>();
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        try {
            String query = "SELECT id, title FROM documents";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                documents.add(new Document(rs.getString("id"), rs.getString("title")));
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error fetching documents: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                System.out.println("Error closing the connection: " + e.getMessage());
            }
        }
        return documents;
    }

    /**
     * Cập nhật mô tả của sách trong cơ sở dữ liệu.
     */
    public void updateBookDescription(String id, String description) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();

        try {
            String updateQuery = "UPDATE documents SET bookdescription = ? WHERE id = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setString(1, description);
            updateStmt.setString(2, id);
            updateStmt.executeUpdate();
            updateStmt.close();
        } catch (Exception e) {
            System.out.println("Error updating book description: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                System.out.println("Error closing the connection: " + e.getMessage());
            }
        }
    }
}
