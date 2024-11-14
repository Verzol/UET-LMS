package models.documents;

public class Book extends Document {
    private String genre;
    private int pageCount;

    public Book() {
        super();
    }

    public Book(String id, String title, String author, int edition, int quantityInStock, String genre, int pageCount) {
        super(id, title, author, edition, quantityInStock);
        this.genre = genre;
        this.pageCount = pageCount;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

}
