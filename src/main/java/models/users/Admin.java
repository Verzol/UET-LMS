package models.users;

import models.documents.Document;

import java.util.ArrayList;
import java.util.List;

public class Admin extends Person {

    public Admin(String username, String password, String firstName, String lastName, String email, String phone) {
        super(username, password, firstName, lastName, email, phone);
    }

    public void addDocument(List<Document> documents, Document newDocument) {
        documents.add(newDocument);
        System.out.println("Document " + newDocument.getTitle() + " has been added.");
    }

    public void removeDocument(List<Document> documents, String documentId) {
        documents.removeIf(document -> document.getId().equals(documentId));
        System.out.println("Document with ID " + documentId + " has been removed.");
    }

    public void updateDocument(Document document, String newTitle, String newAuthor, int newEdition, int newStock) {
        document.setTitle(newTitle);
        document.setAuthor(newAuthor);
        document.setEdition(newEdition);
        document.setQuantityInStock(newStock);
        System.out.println("Document " + document.getId() + " has been updated.");
    }

}
