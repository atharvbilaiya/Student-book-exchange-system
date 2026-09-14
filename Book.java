package bookexchange;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String subject;
    private int semester;
    private BookCondition condition;
    private ListingType listingType;
    private double price;
    private String ownerId;
    private BookStatus status;

    public Book(String bookId, String title, String author, String subject,
                int semester, BookCondition condition, ListingType listingType,
                double price, String ownerId, BookStatus status) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.semester = semester;
        this.condition = condition;
        this.listingType = listingType;
        this.price = price;
        this.ownerId = ownerId;
        this.status = status;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getSubject() { return subject; }
    public int getSemester() { return semester; }
    public BookCondition getCondition() { return condition; }
    public ListingType getListingType() { return listingType; }
    public double getPrice() { return price; }
    public String getOwnerId() { return ownerId; }
    public BookStatus getStatus() { return status; }

    public void setStatus(BookStatus status) { this.status = status; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        String priceText = listingType == ListingType.SELL
                ? String.format("₹%.2f", price) : "-";
        return String.format("%-6s | %-24s | %-18s | %-14s | %-8s | %-9s | %-10s | %s",
                bookId, title, author, subject, condition, listingType, priceText, status);
    }
}
