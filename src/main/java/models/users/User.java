package models.users;

import javafx.fxml.FXML;
import models.documents.Book;
import models.documents.Document;

import java.util.ArrayList;
import java.util.List;

public class User extends Person {
    private List<Document> borrowedDocuments;
    private int maxDocumentAllowed;

    public User(String username, String password, String firstName, String lastName,
                String email, String phone, int maxDocumentAllowed) {
        super(username, password, firstName, lastName, email, phone);
        this.borrowedDocuments = new ArrayList<Document>();
        this.maxDocumentAllowed = maxDocumentAllowed;
    }

    public List<Document> getBorrowedDocuments() {
        return borrowedDocuments;
    }

    public int getMaxDocumentAllowed() {
        return maxDocumentAllowed;
    }

    public void setBorrowedDocuments(List<Document> borrowedDocuments) {
        this.borrowedDocuments = borrowedDocuments;
    }

    public void setMaxDocumentAllowed(int maxDocumentAllowed) {
        this.maxDocumentAllowed = maxDocumentAllowed;
    }

    public boolean borrowDocument(Document document) {
        if (borrowedDocuments.size() < maxDocumentAllowed && document.isAvailable()) {
            borrowedDocuments.add(document);
            document.borrowItem();
            return true;
        } else {
            return false;
        }
    }

    public boolean returnDocument(Document document) {
        if(borrowedDocuments.contains(document)) {
            borrowedDocuments.remove(document);
            document.returnItem();
            return true;
        } else {
            return false;
        }
    }
}