package models.documents;

public class Journal extends Document {
    private int volume;
    private int publishNumber;

    public Journal() {
        super();
    }

    public Journal (String id, String title, String author, int edtion
            , int quanityInStock, int volume, int publishNumber) {
        super(id, title, author, edtion, quanityInStock);
        this.volume = volume;
        this.publishNumber = publishNumber;
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
}
