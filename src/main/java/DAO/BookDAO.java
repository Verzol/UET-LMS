package DAO;

import models.documents.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final Connection connection;
    private final DocumentDAO documentDAO;

    public BookDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.documentDAO = new DocumentDAO();
    }

    public boolean addBook(Book book) {
        try {
            if (exists(book.getId())) {
                System.out.println("Book ID already exists.");
                return false;
            }
            if (existsISBN(book.getISBN())) {
                System.out.println("Book ISBN already exists.");
                return false;
            }

            if (!documentDAO.addDocument(book)) {
                System.out.println("Failed to add book to documents table.");
                return false;
            }

            String query = "INSERT INTO books (id, genre, page_count, ISBN, image_url) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, book.getId());
                statement.setString(2, book.getGenre());
                statement.setInt(3, book.getPageCount());
                statement.setString(4, book.getISBN());
                statement.setString(5, book.getImageUrl());
                statement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBook(Book book) {
        try {
            if (existsISBNForOtherBooks(book.getISBN(), book.getId())) {
                System.out.println("Duplicate ISBN found for another book.");
                return false;
            }

            if (!documentDAO.updateDocument(book)) {
                System.out.println("Failed to update document.");
                return false;
            }

            String query = "UPDATE books SET genre = ?, page_count = ?, ISBN = ?, image_url = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, book.getGenre());
                statement.setInt(2, book.getPageCount());
                statement.setString(3, book.getISBN());
                statement.setString(4, book.getImageUrl());
                statement.setString(5, book.getId());
                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBook(String id) {
        try {
            String query = "DELETE FROM books WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, id);
                statement.executeUpdate();
                return documentDAO.deleteDocument(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, d.times_borrowed, " +
                "b.genre, b.page_count, b.ISBN, b.image_url " +
                "FROM documents d JOIN books b ON d.id = b.id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                books.add(mapToBook(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private Book mapToBook(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        int edition = resultSet.getInt("edition");
        int quantityInStock = resultSet.getInt("quantity_in_stock");
        int borrowedQuantity = resultSet.getInt("borrowed_quantity");
        int timesBorrowed = resultSet.getInt("times_borrowed");
        String genre = resultSet.getString("genre");
        int pageCount = resultSet.getInt("page_count");
        String ISBN = resultSet.getString("ISBN");
        String imageUrl = resultSet.getString("image_url");

        Book book = new Book(id, title, author, edition, quantityInStock, timesBorrowed, genre, pageCount, ISBN, imageUrl);
        book.setBorrowedQuantity(borrowedQuantity);
        return book;
    }

    public boolean exists(String id) {
        String query = "SELECT 1 FROM books WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean existsISBN(String ISBN) {
        String query = "SELECT 1 FROM books WHERE ISBN = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ISBN);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean existsISBNForOtherBooks(String ISBN, String currentBookId) {
        String query = "SELECT 1 FROM books WHERE ISBN = ? AND id != ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, ISBN);
            statement.setString(2, currentBookId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT d.id, d.title, d.author, d.edition, d.quantity_in_stock, d.borrowed_quantity, d.times_borrowed, " +
                "b.genre, b.page_count, b.ISBN, b.image_url " +
                "FROM documents d JOIN books b ON d.id = b.id " +
                "WHERE d.title LIKE ? OR d.author LIKE ? OR b.genre LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String searchPattern = "%" + keyword + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                books.add(mapToBook(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getTopBorrowedBooks(int limit) {
        List<Book> topBooks = new ArrayList<>();
        String query = "SELECT b.id, d.title, d.author, b.genre, b.page_count, b.ISBN, b.image_url, d.times_borrowed " +
                "FROM books b JOIN documents d ON b.id = d.id " +
                "ORDER BY d.times_borrowed DESC LIMIT ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                topBooks.add(mapToBook(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topBooks;
    }
}
