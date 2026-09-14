package bookexchange;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class FileManager {
    private static final Path DATA_DIR = Path.of("data");

    public void save(DatabaseManager db) {
        try {
            Files.createDirectories(DATA_DIR);

            saveStudents(db.getAllStudents());
            saveBooks(db.getAllBooks());
            saveRequests(db.getRequests());

            System.out.println("Data saved to data/ folder.");
        } catch (IOException e) {
            System.out.println("File save error: " + e.getMessage());
        }
    }

    private void saveStudents(List<Student> students) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(DATA_DIR.resolve("students.txt"))) {
            for (Student s : students) {
                writer.write(s.getStudentId() + "|" + clean(s.getName()) + "|" +
                        clean(s.getBranch()) + "|" + s.getSemester() + "|" +
                        clean(s.getContact()));
                writer.newLine();
            }
        }
    }

    private void saveBooks(List<Book> books) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(DATA_DIR.resolve("books.txt"))) {
            for (Book b : books) {
                writer.write(String.join("|",
                        b.getBookId(), clean(b.getTitle()), clean(b.getAuthor()),
                        clean(b.getSubject()), String.valueOf(b.getSemester()),
                        b.getCondition().name(), b.getListingType().name(),
                        String.valueOf(b.getPrice()), b.getOwnerId(), b.getStatus().name()));
                writer.newLine();
            }
        }
    }

    private void saveRequests(List<BookRequest> requests) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(DATA_DIR.resolve("requests.txt"))) {
            for (BookRequest r : requests) {
                writer.write(String.join("|",
                        r.getRequestId(), r.getBookId(), r.getRequesterId(),
                        clean(r.getMessage()), r.getStatus().name()));
                writer.newLine();
            }
        }
    }

    private String clean(String text) {
        return text.replace("|", "/").replace("\n", " ");
    }
}
