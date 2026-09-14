CREATE TABLE IF NOT EXISTS students (
    student_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    branch TEXT NOT NULL,
    semester INTEGER NOT NULL,
    contact TEXT NOT NULL
);

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
);

CREATE TABLE IF NOT EXISTS requests (
    request_id TEXT PRIMARY KEY,
    book_id TEXT NOT NULL,
    requester_id TEXT NOT NULL,
    message TEXT NOT NULL,
    status TEXT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (requester_id) REFERENCES students(student_id)
);
