package bookexchange;

import java.sql.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:data/bookexchange.db";

    public DatabaseManager() {
        createDataFolder();
        createTables();
    }

    private void createDataFolder() {
        try {
            Files.createDirectories(Path.of("data"));
        } catch (IOException e) {
            System.out.println("Could not create data folder: " + e.getMessage());
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void createTables() {
        String students = """
            CREATE TABLE IF NOT EXISTS students (
                student_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                branch TEXT NOT NULL,
                semester INTEGER NOT NULL,
                contact TEXT NOT NULL
            )
            """;

        String books = """
            CREATE TABLE IF NOT EXISTS books (
                book_id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                subject TEXT NOT NULL,
                semester INTEGER NOT NULL,
                condition TEXT NOT NULL,
                listing_type TEXT NOT NULL,
                price REAL NOT NULL,
                owner_id TEXT NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY (owner_id) REFERENCES students(student_id)
            )
            """;

        String requests = """
            CREATE TABLE IF NOT EXISTS requests (
                request_id TEXT PRIMARY KEY,
                book_id TEXT NOT NULL,
                requester_id TEXT NOT NULL,
                message TEXT NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY (book_id) REFERENCES books(book_id),
                FOREIGN KEY (requester_id) REFERENCES students(student_id)
            )
            """;

        try (Connection con = connect(); Statement st = con.createStatement()) {
            st.execute(students);
            st.execute(books);
            st.execute(requests);
        } catch (SQLException e) {
            System.out.println("Database setup error: " + e.getMessage());
        }
    }

    public boolean studentExists(String id) {
        String sql = "SELECT 1 FROM students WHERE student_id = ?";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public boolean bookExists(String id) {
        String sql = "SELECT 1 FROM books WHERE book_id = ?";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public boolean requestExists(String id) {
        String sql = "SELECT 1 FROM requests WHERE request_id = ?";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public void addStudent(Student s) {
        String sql = "INSERT INTO students VALUES (?, ?, ?, ?, ?)";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getBranch());
            ps.setInt(4, s.getSemester());
            ps.setString(5, s.getContact());
            ps.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            System.out.println("Could not add student: " + e.getMessage());
        }
    }

    public void addBook(Book b) {
        String sql = "INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getBookId());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getAuthor());
            ps.setString(4, b.getSubject());
            ps.setInt(5, b.getSemester());
            ps.setString(6, b.getCondition().name());
            ps.setString(7, b.getListingType().name());
            ps.setDouble(8, b.getPrice());
            ps.setString(9, b.getOwnerId());
            ps.setString(10, b.getStatus().name());
            ps.executeUpdate();
            System.out.println("Book listing added successfully.");
        } catch (SQLException e) {
            System.out.println("Could not add book: " + e.getMessage());
        }
    }

    public List<Book> getAvailableBooks() {
        return getBooks("SELECT * FROM books WHERE status = 'AVAILABLE'");
    }

    public List<Book> getAllBooks() {
        return getBooks("SELECT * FROM books");
    }

    public List<Book> searchBooks(String field, String value) {
        String sql;
        if (field.equals("semester")) {
            sql = "SELECT * FROM books WHERE semester = ? AND status = 'AVAILABLE'";
        } else {
            sql = "SELECT * FROM books WHERE LOWER(" + field + ") LIKE LOWER(?) AND status = 'AVAILABLE'";
        }

        List<Book> result = new ArrayList<>();
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (field.equals("semester")) {
                ps.setInt(1, Integer.parseInt(value));
            } else {
                ps.setString(1, "%" + value + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(bookFromResult(rs));
            }
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Search error: " + e.getMessage());
        }
        return result;
    }

    private List<Book> getBooks(String sql) {
        List<Book> result = new ArrayList<>();
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(bookFromResult(rs));
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return result;
    }

    public Book getBook(String id) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return bookFromResult(rs);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    public Student getStudent(String id) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getString("student_id"),
                            rs.getString("name"),
                            rs.getString("branch"),
                            rs.getInt("semester"),
                            rs.getString("contact")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    private Book bookFromResult(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("subject"),
                rs.getInt("semester"),
                BookCondition.valueOf(rs.getString("condition")),
                ListingType.valueOf(rs.getString("listing_type")),
                rs.getDouble("price"),
                rs.getString("owner_id"),
                BookStatus.valueOf(rs.getString("status"))
        );
    }

    public void addRequest(BookRequest r) {
        String sql = "INSERT INTO requests VALUES (?, ?, ?, ?, ?)";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getRequestId());
            ps.setString(2, r.getBookId());
            ps.setString(3, r.getRequesterId());
            ps.setString(4, r.getMessage());
            ps.setString(5, r.getStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not add request: " + e.getMessage());
        }
    }

    public List<BookRequest> getRequests() {
        List<BookRequest> result = new ArrayList<>();
        String sql = "SELECT * FROM requests ORDER BY request_id";
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new BookRequest(
                        rs.getString("request_id"),
                        rs.getString("book_id"),
                        rs.getString("requester_id"),
                        rs.getString("message"),
                        RequestStatus.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return result;
    }

    public BookRequest getRequest(String id) {
        String sql = "SELECT * FROM requests WHERE request_id = ?";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BookRequest(
                            rs.getString("request_id"),
                            rs.getString("book_id"),
                            rs.getString("requester_id"),
                            rs.getString("message"),
                            RequestStatus.valueOf(rs.getString("status"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    public void updateRequestStatus(String requestId, RequestStatus status) {
        String sql = "UPDATE requests SET status = ? WHERE request_id = ?";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, requestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not update request: " + e.getMessage());
        }
    }

    public void updateBookStatus(String bookId, BookStatus status) {
        String sql = "UPDATE books SET status = ? WHERE book_id = ?";
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Could not update book: " + e.getMessage());
        }
    }

    public void deleteBook(String bookId) {
        String deleteRequests = "DELETE FROM requests WHERE book_id = ?";
        String deleteBook = "DELETE FROM books WHERE book_id = ?";

        try (Connection con = connect()) {
            try (PreparedStatement ps = con.prepareStatement(deleteRequests)) {
                ps.setString(1, bookId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(deleteBook)) {
                ps.setString(1, bookId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Could not delete book: " + e.getMessage());
        }
    }

    public List<Book> getBooksByOwner(String ownerId) {
        String sql = "SELECT * FROM books WHERE owner_id = ?";
        List<Book> result = new ArrayList<>();
        try (Connection con = connect(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(bookFromResult(rs));
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return result;
    }

    public List<Student> getAllStudents() {
        List<Student> result = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Student(
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("branch"),
                        rs.getInt("semester"),
                        rs.getString("contact")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return result;
    }
}
