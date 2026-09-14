# Project Report
## Student Book Exchange and Donation Management System

### 1. Introduction

Students often have academic books that they no longer need after completing a semester. At the same time, other students may need the same books but may not want to purchase them at full price. This project provides a small command-line system where students can list academic books for donation, selling, lending or exchange.

### 2. Problem Statement

There is no simple common place in a college for students to find and exchange used academic books. The proposed system keeps student and book information, allows searching, and manages requests between students.

### 3. Objectives

- Store basic student information.
- Create academic book listings.
- Support DONATE, SELL, LEND and EXCHANGE listing types.
- Search available books.
- Allow students to request books.
- Allow owners to accept or reject requests.
- Change book status after an accepted request.
- Store information using JDBC and SQLite.
- Provide a simple text-file backup.

### 4. Scope

The project is designed for a small college-level environment. It is a local command-line application and does not include online payments, delivery services, chat, or a graphical interface.

### 5. Technologies Used

- Java 17
- Object-Oriented Programming
- Java Collections
- Exception Handling
- File I/O and Streams
- JDBC
- SQLite
- Maven

### 6. Main Classes

#### Student
Stores student ID, name, branch, semester and contact information.

#### Book
Stores book details such as title, author, subject, condition, listing type, price, owner and availability status.

#### BookRequest
Stores request ID, book ID, requester ID, message and request status.

#### DatabaseManager
Handles SQLite database creation and CRUD operations using JDBC.

#### FileManager
Writes database records to simple text files using Java file I/O.

#### BookExchangeSystem
Contains the menu and application logic.

### 7. Enumerations

- ListingType: DONATE, SELL, LEND, EXCHANGE
- BookCondition: NEW, GOOD, FAIR, POOR
- BookStatus: AVAILABLE, REQUESTED, EXCHANGED, DONATED, SOLD, LENT
- RequestStatus: PENDING, ACCEPTED, REJECTED

### 8. Database Design

#### students

| Column | Type | Description |
|---|---|---|
| student_id | TEXT | Primary key |
| name | TEXT | Student name |
| branch | TEXT | Branch |
| semester | INTEGER | Semester |
| contact | TEXT | Contact information |

#### books

| Column | Type | Description |
|---|---|---|
| book_id | TEXT | Primary key |
| title | TEXT | Book title |
| author | TEXT | Author |
| subject | TEXT | Subject |
| semester | INTEGER | Related semester |
| condition | TEXT | Book condition |
| listing_type | TEXT | Donate/Sell/Lend/Exchange |
| price | REAL | Price for selling |
| owner_id | TEXT | Student who listed the book |
| status | TEXT | Current book status |

#### requests

| Column | Type | Description |
|---|---|---|
| request_id | TEXT | Primary key |
| book_id | TEXT | Requested book |
| requester_id | TEXT | Student requesting |
| message | TEXT | Request message |
| status | TEXT | Pending/Accepted/Rejected |

### 9. Working Flow

1. A student is added to the system.
2. The student adds a book listing.
3. The book starts with AVAILABLE status.
4. Another student searches for the book.
5. The student sends a request.
6. The book changes to REQUESTED.
7. The owner views the request.
8. The owner accepts or rejects it.
9. If accepted, the final status depends on the listing type:
   - SELL → SOLD
   - DONATE → DONATED
   - LEND → LENT
   - EXCHANGE → EXCHANGED
10. Other pending requests for an accepted book are rejected.

### 10. Java Concepts Demonstrated

The project uses classes, objects, constructors, private data members, getters/setters, enums, switch expressions, loops, conditional statements, ArrayList/Lists, exception handling, file streams and JDBC.

### 11. Testing

| Test | Expected result |
|---|---|
| Add unique student | Student is stored |
| Add duplicate student ID | Duplicate is rejected |
| Add book for unknown student | Listing is rejected |
| Add SELL book | Price is requested |
| Add DONATE book | Price is not requested |
| Search by title | Matching available books appear |
| Search by subject | Matching available books appear |
| Request unavailable book | Request is rejected |
| Request own book | Request is rejected |
| Accept request by wrong owner | Action is rejected |
| Accept SELL request | Book becomes SOLD |
| Accept DONATE request | Book becomes DONATED |
| Reject request | Book becomes AVAILABLE |
| Delete listing by non-owner | Deletion is rejected |
| Invalid menu input | Program asks again |
| Invalid numeric input | InputMismatchException is handled |

### 12. Limitations

- It is a local command-line application.
- It does not have online user authentication.
- It does not process real payments.
- Lending return dates are not included.
- Exchange matching is handled manually by students.

### 13. Future Improvements

- Add a GUI or web interface.
- Add login and user authentication.
- Add lending return dates.
- Add notifications.
- Add better exchange matching.
- Add administrator functions.

### 14. Conclusion

The Student Book Exchange and Donation Management System provides a simple solution for sharing used academic books between students. The project is intentionally small enough to understand and demonstrate while covering important Java programming concepts from the course.
