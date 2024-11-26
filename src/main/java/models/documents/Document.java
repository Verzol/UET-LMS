package models.documents;

import models.interfaces.Borrowable;
import models.interfaces.Identifiable;

public abstract class Document implements Borrowable, Identifiable {
    private String id;
    private String title;
    private String author;
    private int edition;
    private int quantityInStock;
    private int borrowedQuantity;
    private int timesBorrowed;
    private String borrowStatus;
    private boolean hasIssues;

    public Document(String id, String title, String author, int edition, int quantityInStock, int timesBorrowed) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.edition = edition;
        this.quantityInStock = quantityInStock;
        this.borrowedQuantity = 0;
        this.timesBorrowed = timesBorrowed;
        this.borrowStatus = "Available";
        this.hasIssues = false;
    }

    public Document() {
        this.borrowStatus = "Available";
        this.borrowedQuantity = 0;
        this.timesBorrowed = 0;
    }

    @Override
    public void borrowItem() {
        if (borrowedQuantity < quantityInStock) {
            borrowedQuantity++;
            timesBorrowed++;
            updateBorrowStatus();
        } else {
            throw new IllegalStateException("No available copies to borrow.");
        }
    }

    @Override
    public void returnItem() {
        if (borrowedQuantity > 0) {
            borrowedQuantity--;
            updateBorrowStatus();
        } else {
            throw new IllegalStateException("No borrowed copies to return.");
        }
    }

    public void updateBorrowStatus() {
        if (borrowedQuantity < quantityInStock) {
            this.borrowStatus = "Available";
        } else {
            this.borrowStatus = "Cannot borrow (Out of stock)";
        }
    }

    public int getAvailableQuantity() {
        return quantityInStock - borrowedQuantity;
    }

    public boolean isAvailable() {
        return borrowedQuantity < quantityInStock;
    }

    public int getTimesBorrowed() {
        return timesBorrowed;
    }

    public void setTimesBorrowed(int timesBorrowed) {
        this.timesBorrowed = timesBorrowed;
    }

    public void resetTimesBorrowed() {
        this.timesBorrowed = 0;
    }

    public String showDetail() {
        StringBuilder details = new StringBuilder();
        details.append("Document Details:\n");
        details.append("ID: ").append(id).append("\n");
        details.append("Title: ").append(title).append("\n");
        details.append("Author: ").append(author).append("\n");
        details.append("Edition: ").append(edition).append("\n");
        details.append("Quantity in Stock: ").append(quantityInStock).append("\n");
        details.append("Borrowed Quantity: ").append(borrowedQuantity).append("\n");
        details.append("Available Quantity: ").append(getAvailableQuantity()).append("\n");
        details.append("Times Borrowed: ").append(timesBorrowed).append("\n");
        details.append("Borrow Status: ").append(borrowStatus).append("\n");
        return details.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
        updateBorrowStatus();
    }

    public int getBorrowedQuantity() {
        return borrowedQuantity;
    }

    public void setBorrowedQuantity(int borrowedQuantity) {
        this.borrowedQuantity = borrowedQuantity;
        updateBorrowStatus();
    }

    public String getBorrowStatus() {
        return borrowStatus;
    }

    public void setBorrowStatus(String borrowStatus) {
        this.borrowStatus = borrowStatus;
    }

    public void incrementTimesBorrowed() {
        this.timesBorrowed++;
    }

    public boolean isHasIssues() {
        return hasIssues;
    }

    public void setHasIssues(boolean hasIssues) {
        this.hasIssues = hasIssues;
    }
}
