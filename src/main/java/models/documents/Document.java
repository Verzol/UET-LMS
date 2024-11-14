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

    public Document() {
        this.id = "";
        this.title = "";
        this.author = "";
        this.edition = 0;
        this.quantityInStock = 0;
        this.borrowedQuantity = 0;
    }

    public Document(String id, String title, String author, int edition, int quantityInStock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.edition = edition;
        this.quantityInStock = quantityInStock;
        this.borrowedQuantity = 0;
    }

    @Override
    public void borrowItem() {
        if (borrowedQuantity >= quantityInStock) {
            System.out.println(title + " not available for borrowing.");
        } else {
            borrowedQuantity++;
            System.out.println(title + " has been borrowed. Available: " + getAvailableQuantity());
        }
    }

    @Override
    public void returnItem() {
        if (borrowedQuantity > 0) {
            borrowedQuantity--;
            System.out.println(title + " has been returned. Available: " + getAvailableQuantity());
        } else {
            System.out.println("No borrowed copies of " + title + " to return.");
        }
    }

    public int getAvailableQuantity() {
        return quantityInStock - borrowedQuantity;
    }

    public boolean isAvailable() {
        return borrowedQuantity < quantityInStock;
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
    }

    public int getBorrowedQuantity() {
        return borrowedQuantity;
    }

    public void setBorrowedQuantity(int borrowedQuantity) {
        this.borrowedQuantity = borrowedQuantity;
    }
}
