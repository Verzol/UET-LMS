package models.documents;

import java.sql.Date;

public class BookError {
    private int id;
    private String documentId;
    private String errorDescription;
    private Date errorDate;
    private boolean fixedStatus;
    private Date fixedDate;
    private int reportedByUserId;
    private String resolutionNotes;
    private BookError bookError;


    public BookError(int id, String documentId, String errorDescription, Date errorDate, boolean fixedStatus, Date fixedDate, int reportedByUserId, String resolutionNotes) {
        this.id = id;
        this.documentId = documentId;
        this.errorDescription = errorDescription;
        this.errorDate = errorDate;
        this.fixedStatus = fixedStatus;
        this.fixedDate = fixedDate;
        this.reportedByUserId = reportedByUserId;
        this.resolutionNotes = resolutionNotes;
    }

    public BookError() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public Date getErrorDate() {
        return errorDate;
    }

    public void setErrorDate(Date errorDate) {
        this.errorDate = errorDate;
    }

    public boolean isFixedStatus() {
        return fixedStatus;
    }

    public void setFixedStatus(boolean fixedStatus) {
        this.fixedStatus = fixedStatus;
    }

    public Date getFixedDate() {
        return fixedDate;
    }

    public void setFixedDate(Date fixedDate) {
        this.fixedDate = fixedDate;
    }

    public int getReportedByUserId() {
        return reportedByUserId;
    }

    public void setReportedByUserId(int reportedByUserId) {
        this.reportedByUserId = reportedByUserId;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public BookError getBookError() {
        return bookError;
    }

    public void setBookError(BookError bookError) {
        this.bookError = bookError;
    }
}
