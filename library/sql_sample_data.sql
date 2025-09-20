USE library_db;

-- ROLES
INSERT INTO roles (id, name, description, created_at) VALUES
(1, 'ROLE_ADMIN', 'Full admin access', NOW()),
(2, 'ROLE_LIBRARIAN', 'Manage books and borrowings', NOW()),
(3, 'ROLE_MEMBER', 'Regular library member', NOW());

-- SETTINGS
INSERT INTO settings (`id`,`key`,`value`,`description`) VALUES
(1,'default.max_borrow_limit','5','Default maximum concurrent borrows per member'),
(2,'borrow.period.days','14','Default number of days for each borrow'),
(3,'fine.per.day','1.00','Fine amount per overdue day');

-- LANGUAGES
INSERT INTO languages (id, code, name) VALUES
(1, 'en', 'English'),
(2, 'ar', 'Arabic');

-- CATEGORIES
INSERT INTO categories (id, name, description) VALUES
(1, 'General','General books'),
(2, 'Science','Science & Technology'),
(3, 'Fiction','Fiction & Literature');

-- AUTHORS
INSERT INTO authors (id, name, bio, created_at) VALUES
(1, 'Unknown Author', 'Auto seed', NOW()),
(2, 'Jane Austen', 'English novelist', NOW()),
(3, 'Isaac Asimov', 'Science fiction author', NOW());

-- USERS
-- Passwords: (Mix of normak and Bcrypt - as sometimes fails with ready Bcrypt :)
--   $2a$12$D9j2YEKdP9Mljb1I/h8rCO2C2mJ9XySxXlHqYhtCecyq4s7g7dP7S
--   librarian123
--   member123
INSERT INTO users (id, username, email, password, full_name, enabled, max_borrow_limit, created_at, updated_at) VALUES
(1, 'admin', 'admin@example.com', 'admin123', 'Administrator', TRUE, 10, NOW(), NOW()),
(2, 'librarian1', 'librarian1@example.com', '$2a$12$kBrX88g5qemlBwya9jF9TeLrD0f5UxkGwfoIsf80zPjD5xjXN5sK2', 'Librarian One', TRUE, 7, NOW(), NOW()),
(3, 'member1', 'member1@example.com', '$2a$12$hG0pIYkJZcV8X.Cx2Bl3JOFsX8So0L1R5ZYnUh1xC2qMpuoXkT3ty', 'Member One', TRUE, 5, NOW(), NOW());

-- USER_ROLES
INSERT INTO user_roles (user_id, role_id) VALUES
(1,1),
(1,2),
(2,2),
(3,3);

-- BOOKS
INSERT INTO books (id, title, isbn, summary, publisher, language_id, category_id, total_copies, created_at, updated_at) VALUES
(1, 'Pride and Prejudice', 'ISBN-1111', 'A classic novel by Jane Austen', 'T. Egerton', 1, 3, 3, NOW(), NOW()),
(2, 'Foundation', 'ISBN-2222', 'Science fiction series by Isaac Asimov', 'Gnome Press', 1, 2, 2, NOW(), NOW()),
(3, 'General Knowledge', 'ISBN-3333', 'A general knowledge compendium', 'Knowledge House', 1, 1, 1, NOW(), NOW());

-- BOOK_AUTHORS
INSERT INTO book_authors (book_id, author_id) VALUES
(1,2),
(2,3),
(3,1);

-- BOOK_COPIES
INSERT INTO book_copies (id, book_id, copy_number, barcode, status, created_at) VALUES
(1, 1, 1, 'BOOK-1-COPY-1', 'AVAILABLE', NOW()),
(2, 1, 2, 'BOOK-1-COPY-2', 'BORROWED', NOW()),
(3, 1, 3, 'BOOK-1-COPY-3', 'AVAILABLE', NOW()),
(4, 2, 1, 'BOOK-2-COPY-1', 'AVAILABLE', NOW()),
(5, 2, 2, 'BOOK-2-COPY-2', 'AVAILABLE', NOW()),
(6, 3, 1, 'BOOK-3-COPY-1', 'AVAILABLE', NOW());

-- BORROW_RECORDS
INSERT INTO borrow_records (id, user_id, book_copy_id, borrowed_at, due_at, returned_at, status, fine_amount) VALUES
(1, 3, 2, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), NULL, 'BORROWED', 0.00),
(2, 3, 4, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 'RETURNED', 0.00);

-- ACTIVITY_LOGS
INSERT INTO activity_logs (id, user_id, action, details, ip, created_at) VALUES
(1, 1, 'SEED', 'Initial roles created', '127.0.0.1', NOW()),
(2, 1, 'CREATE_USER', 'Created admin user', '127.0.0.1', NOW()),
(3, 3, 'BORROW_BOOK', 'member1 borrowed BOOK-1-COPY-2', '127.0.0.1', NOW());

-- update books.total_copies
UPDATE books SET total_copies = (SELECT COUNT(*) FROM book_copies WHERE book_copies.book_id = books.id);
