package bookexchange;

public class BookRequest {
    private String requestId;
    private String bookId;
    private String requesterId;
    private String message;
    private RequestStatus status;

    public BookRequest(String requestId, String bookId, String requesterId,
                       String message, RequestStatus status) {
        this.requestId = requestId;
        this.bookId = bookId;
        this.requesterId = requesterId;
        this.message = message;
        this.status = status;
    }

    public String getRequestId() { return requestId; }
    public String getBookId() { return bookId; }
    public String getRequesterId() { return requesterId; }
    public String getMessage() { return message; }
    public RequestStatus getStatus() { return status; }

    public void setStatus(RequestStatus status) { this.status = status; }

    @Override
    public String toString() {
        return requestId + " | Book: " + bookId +
               " | Student: " + requesterId +
               " | " + message + " | " + status;
    }
}
