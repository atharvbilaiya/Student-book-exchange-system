package bookexchange;

import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class BookExchangeSystem {
    private final Scanner scanner;
    private final DatabaseManager db;
    private final FileManager fileManager;

    public BookExchangeSystem() {
        scanner = new Scanner(System.in);
        db = new DatabaseManager();
        fileManager = new FileManager();
    }

    public void start() {
        System.out.println("\nWelcome to Student Book Exchange System!");

        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> addBook();
                case 3 -> viewAvailableBooks();
                case 4 -> searchBook();
                case 5 -> sendRequest();
                case 6 -> viewRequests();
                case 7 -> acceptOrRejectRequest();
                case 8 -> updateBookStatus();
                case 9 -> deleteBook();
                case 10 -> fileManager.save(db);
                case 11 -> loadData();
                case 12 -> viewMyListings();
                case 13 -> running = false;
                default -> System.out.println("Please choose a number from 1 to 13.");
            }
        }

        scanner.close();
        System.out.println("Thank you for using the system.");
    }

    private void showMenu() {
        System.out.println("""

                =============================================
                    STUDENT BOOK EXCHANGE SYSTEM
                =============================================
                1.  Add Student
                2.  Add Book Listing
                3.  View All Available Books
                4.  Search Book
                5.  Send Book Request
                6.  View Requests
                7.  Accept / Reject Request
                8.  Update Book Status
                9.  Delete Book Listing
                10. Save Data to File
                11. Load Data from File
                12. View My Listings
                13. Exit
                =============================================
                """);
    }

    private void addStudent() {
        System.out.println("\n--- Add Student ---");
        String id = readRequired("Student ID: ");

        if (db.studentExists(id)) {
            System.out.println("Student ID already exists.");
            return;
        }

        String name = readRequired("Name: ");
        String branch = readRequired("Branch: ");
        int semester = readPositiveInt("Semester: ");
        String contact = readRequired("Contact number/email: ");

        db.addStudent(new Student(id, name, branch, semester, contact));
    }

    private void addBook() {
        System.out.println("\n--- Add Book Listing ---");
        String bookId = readRequired("Book ID: ");

        if (db.bookExists(bookId)) {
            System.out.println("Book ID already exists.");
            return;
        }

        String ownerId = readRequired("Owner Student ID: ");
        if (!db.studentExists(ownerId)) {
            System.out.println("Student does not exist. Add the student first.");
            return;
        }

        String title = readRequired("Book title: ");
        String author = readRequired("Author name: ");
        String subject = readRequired("Subject: ");
        int semester = readPositiveInt("Semester: ");

        BookCondition condition = readEnum(
                "Condition (NEW/GOOD/FAIR/POOR): ",
                BookCondition.class);

        ListingType type = readEnum(
                "Listing type (DONATE/SELL/LEND/EXCHANGE): ",
                ListingType.class);

        double price = 0;
        if (type == ListingType.SELL) {
            price = readNonNegativeDouble("Price: ");
        }

        Book book = new Book(bookId, title, author, subject, semester,
                condition, type, price, ownerId, BookStatus.AVAILABLE);

        db.addBook(book);
    }

    private void viewAvailableBooks() {
        System.out.println("\n--- Available Books ---");
        List<Book> books = db.getAvailableBooks();

        if (books.isEmpty()) {
            System.out.println("No available books found.");
            return;
        }

        printBookHeader();
        books.forEach(System.out::println);
    }

    private void searchBook() {
        System.out.println("""

                --- Search Book ---
                1. Title
                2. Author
                3. Subject
                4. Semester
                """);

        int choice = readInt("Search by: ");
        String field;

        switch (choice) {
            case 1 -> field = "title";
            case 2 -> field = "author";
            case 3 -> field = "subject";
            case 4 -> field = "semester";
            default -> {
                System.out.println("Invalid search option.");
                return;
            }
        }

        String value = readRequired("Enter search value: ");
        List<Book> results = db.searchBooks(field, value);

        if (results.isEmpty()) {
            System.out.println("No matching available books found.");
            return;
        }

        printBookHeader();
        results.forEach(System.out::println);
    }

    private void sendRequest() {
        System.out.println("\n--- Send Book Request ---");
        String requestId = readRequired("Request ID: ");

        if (db.requestExists(requestId)) {
            System.out.println("Request ID already exists.");
            return;
        }

        String bookId = readRequired("Book ID: ");
        Book book = db.getBook(bookId);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        if (book.getStatus() != BookStatus.AVAILABLE) {
            System.out.println("This book is not available.");
            return;
        }

        String requesterId = readRequired("Your Student ID: ");
        if (!db.studentExists(requesterId)) {
            System.out.println("Student does not exist.");
            return;
        }

        if (requesterId.equals(book.getOwnerId())) {
            System.out.println("Owner cannot request their own book.");
            return;
        }

        String message = readRequired("Short message: ");

        db.addRequest(new BookRequest(requestId, bookId, requesterId,
                message, RequestStatus.PENDING));
        db.updateBookStatus(bookId, BookStatus.REQUESTED);

        System.out.println("Request sent successfully.");
    }

    private void viewRequests() {
        System.out.println("\n--- All Requests ---");
        List<BookRequest> requests = db.getRequests();

        if (requests.isEmpty()) {
            System.out.println("No requests found.");
            return;
        }

        requests.forEach(System.out::println);
    }

    private void acceptOrRejectRequest() {
        System.out.println("\n--- Accept / Reject Request ---");
        String requestId = readRequired("Request ID: ");
        BookRequest request = db.getRequest(requestId);

        if (request == null) {
            System.out.println("Request not found.");
            return;
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            System.out.println("This request is already " + request.getStatus() + ".");
            return;
        }

        Book book = db.getBook(request.getBookId());
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        String ownerId = readRequired("Enter owner Student ID: ");
        if (!ownerId.equals(book.getOwnerId())) {
            System.out.println("Only the owner can accept or reject this request.");
            return;
        }

        System.out.println("Book: " + book.getTitle());
        System.out.println("Requester: " + request.getRequesterId());
        System.out.println("Message: " + request.getMessage());

        String decision = readRequired("Enter A to accept or R to reject: ");

        if (decision.equalsIgnoreCase("A")) {
            db.updateRequestStatus(requestId, RequestStatus.ACCEPTED);
            BookStatus finalStatus = getFinalStatus(book.getListingType());
            db.updateBookStatus(book.getBookId(), finalStatus);

            rejectOtherPendingRequests(book.getBookId(), requestId);
            System.out.println("Request accepted. Book status changed to " + finalStatus + ".");
        } else if (decision.equalsIgnoreCase("R")) {
            db.updateRequestStatus(requestId, RequestStatus.REJECTED);
            db.updateBookStatus(book.getBookId(), BookStatus.AVAILABLE);
            System.out.println("Request rejected. Book is available again.");
        } else {
            System.out.println("Invalid decision.");
        }
    }

    private BookStatus getFinalStatus(ListingType type) {
        return switch (type) {
            case SELL -> BookStatus.SOLD;
            case DONATE -> BookStatus.DONATED;
            case LEND -> BookStatus.LENT;
            case EXCHANGE -> BookStatus.EXCHANGED;
        };
    }

    private void rejectOtherPendingRequests(String bookId, String acceptedRequestId) {
        for (BookRequest request : db.getRequests()) {
            if (request.getBookId().equals(bookId)
                    && !request.getRequestId().equals(acceptedRequestId)
                    && request.getStatus() == RequestStatus.PENDING) {
                db.updateRequestStatus(request.getRequestId(), RequestStatus.REJECTED);
            }
        }
    }

    private void updateBookStatus() {
        System.out.println("\n--- Update Book Status ---");
        String bookId = readRequired("Book ID: ");
        Book book = db.getBook(bookId);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        String ownerId = readRequired("Owner Student ID: ");
        if (!ownerId.equals(book.getOwnerId())) {
            System.out.println("Only the owner can update this listing.");
            return;
        }

        BookStatus status = readEnum(
                "New status (AVAILABLE/REQUESTED/EXCHANGED/DONATED/SOLD/LENT): ",
                BookStatus.class);

        db.updateBookStatus(bookId, status);
        System.out.println("Book status updated.");
    }

    private void deleteBook() {
        System.out.println("\n--- Delete Book Listing ---");
        String bookId = readRequired("Book ID: ");
        Book book = db.getBook(bookId);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        String ownerId = readRequired("Owner Student ID: ");
        if (!ownerId.equals(book.getOwnerId())) {
            System.out.println("Only the owner can delete this listing.");
            return;
        }

        String confirm = readRequired("Delete this listing? (Y/N): ");
        if (confirm.equalsIgnoreCase("Y")) {
            db.deleteBook(bookId);
            System.out.println("Book listing deleted.");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private void loadData() {
        System.out.println("\n--- Saved Data ---");
        System.out.println("Students: " + db.getAllStudents().size());
        System.out.println("Books: " + db.getAllBooks().size());
        System.out.println("Requests: " + db.getRequests().size());
        System.out.println("Data is already available in the database.");
    }

    private void viewMyListings() {
        System.out.println("\n--- My Listings ---");
        String ownerId = readRequired("Student ID: ");

        if (!db.studentExists(ownerId)) {
            System.out.println("Student does not exist.");
            return;
        }

        List<Book> books = db.getBooksByOwner(ownerId);
        if (books.isEmpty()) {
            System.out.println("No listings found for this student.");
            return;
        }

        printBookHeader();
        books.forEach(System.out::println);
    }

    private void printBookHeader() {
        System.out.println("ID     | Title                    | Author             | Subject        | Condition | Type      | Price      | Status");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

    private String readRequired(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field cannot be empty.");
        }
    }

    private int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    private int readPositiveInt(String message) {
        while (true) {
            int value = readInt(message);
            if (value > 0) return value;
            System.out.println("Value must be greater than zero.");
        }
    }

    private double readNonNegativeDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = scanner.nextDouble();
                scanner.nextLine();
                if (value >= 0) return value;
                System.out.println("Price cannot be negative.");
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid price.");
                scanner.nextLine();
            }
        }
    }

    private <T extends Enum<T>> T readEnum(String message, Class<T> enumClass) {
        while (true) {
            String value = readRequired(message).toUpperCase();
            try {
                return Enum.valueOf(enumClass, value);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid option. Try again.");
            }
        }
    }
}
