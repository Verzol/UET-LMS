package models.documents;

public class Book extends Document {
    private String ISBN;
    private String genre;
    private int pageCount;
    private String imageUrl;
    private BookError bookError;

    public Book() {
        super();
    }

    public Book(String id, String title, String author, int edition, int quantityInStock,
                int timesBorrowed, String genre, int pageCount, String ISBN, String imageUrl) {
        super(id, title, author, edition, quantityInStock, timesBorrowed);
        this.genre = genre;
        this.pageCount = pageCount;
        this.ISBN = ISBN;
        this.imageUrl = imageUrl;
    }

    public Book(String id, String title, String author, int edition, int quantityInStock,
                int timesBorrowed, String genre, int pageCount, String ISBN) {
        super(id, title, author, edition, quantityInStock, timesBorrowed);
        this.genre = genre;
        this.pageCount = pageCount;
        this.ISBN = ISBN;
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

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BookError getBookError() {
        return bookError;
    }

    public void setBookError(BookError bookError) {
        this.bookError = bookError;
    }
}
