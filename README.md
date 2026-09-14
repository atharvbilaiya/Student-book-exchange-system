# Student Book Exchange and Donation Management System

A simple command-line Java project for college students to donate, sell, lend or exchange used academic books.

## Features

1. Add Student
2. Add Book Listing
3. View All Available Books
4. Search Book
5. Send Book Request
6. View Requests
7. Accept / Reject Request
8. Update Book Status
9. Delete Book Listing
10. Save Data to File
11. Load Data from File
12. View My Listings
13. Exit

The project also uses JDBC with SQLite for persistent database storage.

## Requirements

- Java JDK 17 or later
- Apache Maven 3.8+ recommended
- Internet connection on the first Maven build so Maven can download the SQLite JDBC dependency

Check Java:

```bash
java -version
```

Check Maven:

```bash
mvn -version
```

## Run the project

From the project root:

```bash
mvn clean compile
mvn exec:java
```

Or package it:

```bash
mvn clean package
mvn exec:java
```

The SQLite database is created automatically at:

```text
data/bookexchange.db
```

A separate text backup can be created through menu option 10:

```text
data/students.txt
data/books.txt
data/requests.txt
```

## First-time use

A simple demonstration sequence is:

1. Add two or three students.
2. Add a book listing for one student.
3. View available books.
4. Search for the book.
5. Send a request using another student's ID.
6. View requests.
7. Accept the request using the owner's ID.
8. Check the updated book status.

## Database

The application creates three tables automatically:

- `students`
- `books`
- `requests`

All database operations use `PreparedStatement`.

## Project structure

```text
Student-Book-Exchange-System/
├── pom.xml
├── README.md
├── PROJECT_REPORT.md
├── schema.sql
├── data/
├── src/
│   └── main/
│       └── java/
│           └── bookexchange/
│               ├── Main.java
│               ├── BookExchangeSystem.java
│               ├── DatabaseManager.java
│               ├── FileManager.java
│               ├── Student.java
│               ├── Book.java
│               ├── BookRequest.java
│               ├── ListingType.java
│               ├── BookCondition.java
│               ├── BookStatus.java
│               └── RequestStatus.java
└── .gitignore
```

## Notes

This is intentionally a small console application. It avoids a GUI and unnecessary external services so that the program is easy to run, understand and demonstrate from a terminal.
