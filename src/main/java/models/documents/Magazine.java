package models.documents;

public class Magazine extends Document {
    private String publishNumber;
    private String month;

    public Magazine() {
        super();
    }

    public Magazine(String id, String title, String author, int edtion, int quantityInStock
            , String publishNumber, String month) {
        super(id, title, author, edtion, quantityInStock);
        this.publishNumber = publishNumber;
        this.month = month;
    }

    public String getPublishNumber() {
        return publishNumber;
    }

    public void setPublishNumber(String publishNumber) {
        this.publishNumber = publishNumber;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
