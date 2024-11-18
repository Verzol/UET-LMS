package DAO;

import models.documents.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private Connection connection;

    public BookDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean addBook(Book book) {
        String queryDocuments = "INSERT INTO documents (id, title, author, edition, quantity_in_stock, ISBN) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String queryBooks = "INSERT INTO books (id, genre, page_count, image_url) VALUES (?, ?, ?, ?)";

        try (PreparedStatement docStatement = connection.prepareStatement(queryDocuments);
             PreparedStatement bookStatement = connection.prepareStatement(queryBooks)) {

            // Insert into 'documents' table
            docStatement.setString(1, book.getId());
            docStatement.setString(2, book.getTitle());
            docStatement.setString(3, book.getAuthor());
            docStatement.setInt(4, book.getEdition());
            docStatement.setInt(5, book.getQuantityInStock());
            docStatement.setString(6, book.getISBN());
            docStatement.executeUpdate();

            bookStatement.setString(1, book.getId());
            bookStatement.setString(2, book.getGenre());
            bookStatement.setInt(3, book.getPageCount());
            bookStatement.setString(4, book.getImageUrl());
            bookStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, "
                + "b.genre, b.page_count, b.ISBN, b.image_url "
                + "FROM documents d JOIN books b ON d.id = b.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int edition = resultSet.getInt("edition");
                int quantityInStock = resultSet.getInt("quantity_in_stock");
                int borrowedQuantity = resultSet.getInt("borrowed_quantity");
                String genre = resultSet.getString("genre");
                int pageCount = resultSet.getInt("page_count");
                String ISBN = resultSet.getString("ISBN");
                String imageUrl = resultSet.getString("image_url");

                Book book = new Book(id, title, author, edition, quantityInStock, genre, pageCount, ISBN, imageUrl);
                book.setBorrowedQuantity(borrowedQuantity);
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }


    public boolean updateBook(Book book) {
        String queryDocuments = "UPDATE documents SET title = ?, author = ?, edition = ?" +
                ", quantity_in_stock = ?, borrowed_quantity = ? WHERE id = ?";
        String queryBooks = "UPDATE books SET genre = ?, page_count = ?, ISBN = ?, image_url = ? WHERE id = ?";

        try (PreparedStatement docStatement = connection.prepareStatement(queryDocuments);
             PreparedStatement bookStatement = connection.prepareStatement(queryBooks)) {

            docStatement.setString(1, book.getTitle());
            docStatement.setString(2, book.getAuthor());
            docStatement.setInt(3, book.getEdition());
            docStatement.setInt(4, book.getQuantityInStock());
            docStatement.setInt(5, book.getBorrowedQuantity());
            docStatement.setString(6, book.getId());
            docStatement.executeUpdate();

            bookStatement.setString(1, book.getGenre());
            bookStatement.setInt(2, book.getPageCount());
            bookStatement.setString(3, book.getISBN());
            bookStatement.setString(4, book.getImageUrl());
            bookStatement.setString(5, book.getId());
            bookStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBook(String id) {
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
        String query = "SELECT d.id FROM documents d JOIN books b ON d.id = b.id WHERE d.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Book> getAvailableBooks() {
        return getAllBooks().stream()
                .filter(book -> book.getQuantityInStock() > 0)
                .toList();
    }
}
