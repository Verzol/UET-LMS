package models.documents;

public class Magazine extends Document {
    private String publishNumber;
    private String month;
    private String imageUrl;

    public Magazine() {
        super();
    }

    public Magazine(String id, String title, String author, int edition, int quantityInStock,
                    int timesBorrowed, String publishNumber, String month, String imageUrl) {
        super(id, title, author, edition, quantityInStock, timesBorrowed);
        this.publishNumber = publishNumber;
        this.month = month;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
