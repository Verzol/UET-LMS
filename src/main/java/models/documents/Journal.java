package models.documents;

public class Journal extends Document {
    private int volume;
    private int publishNumber;
    private String imageUrl;

    public Journal() {
        super();
    }

    public Journal(String id, String title, String author, int edition,
                   int quantityInStock, int volume, int publishNumber, String imageUrl) {
        super(id, title, author, edition, quantityInStock);
        this.volume = volume;
        this.publishNumber = publishNumber;
        this.imageUrl = imageUrl;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getPublishNumber() {
        return publishNumber;
    }

    public void setPublishNumber(int publishNumber) {
        this.publishNumber = publishNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
