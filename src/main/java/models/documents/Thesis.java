package models.documents;

public class Thesis extends Document {
    private String university;
    private String supervisor;
    private String field;

    public Thesis() {
        super();
    }

    public Thesis(String id, String title, String author, int edition
            , int quantityInStock, String university, String supervisor, String field) {
        super(id, title, author, edition, quantityInStock);
        this.university = university;
        this.supervisor = supervisor;
        this.field = field;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
