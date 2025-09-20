# 📚 Library Management System (LMS)

> A complete Spring Boot project for managing a library with **secure authentication, role-based access control, borrowing workflows, and detailed logging**.

---

## ✨ Features

- 🔐 **Secure Authentication** with Spring Security & password hashing  
- 👥 **Role-Based Access Control** (Admin, Librarian, Staff, Member)  
- 📖 **Book Management** with metadata (author, category, publisher, language, ISBN)  
- 👤 **Member Management** with borrowing history  
- 🔄 **Borrow/Return Transactions** with status tracking  
- 📝 **User Activity Logging** for monitoring actions  
- 🌍 **Multi-language & categories** for books  
- 📊 **Borrowing limits & copy availability** logic  
- 📬 **Postman Collection** for easy API testing  

---

## 📝 Design Choices

- **Spring Boot 3 + Java 21** → modern, fast, and reliable framework for backend services  
- **Layered Architecture (Controller → Service → Repository → Entity)** → ensures clean separation of concerns  
- **Spring Data JPA (Hibernate)** → simplifies database operations and mappings  
- **Spring Security** → role-based access and encrypted password storage  
- **DTOs** → to keep controllers clean and enforce validation  
- **HikariCP** → efficient connection pooling for MySQL  
- **Postman Collection** → included for quick API testing  

---

## 🏗️ Architecture & Tech Stack

- ☕ **Java 21**  
- 🚀 **Spring Boot 3.1.4**  
- 🗄️ **MySQL 8**  
- 🛠️ **Maven** (with wrapper)  
- 🔐 **Spring Security (JWT-ready)**  
- 🧪 **JUnit 5** for testing  
- 📬 **Postman** for API testing  

---

## 🗄️ Database Design

### Main Entities
- **SystemUser** → system accounts with roles (ADMIN, LIBRARIAN, STAFF).  
- **Member** → registered library members who borrow books.  
- **Book** → metadata & relations (authors, publisher, categories).  
- **Author** → one-to-many with books.  
- **Publisher** → one-to-many with books.  
- **Category** → for classifying books.  
- **Copy** → physical copies of books with `CopyStatus`.  
- **BorrowingTransaction** → records borrow/return actions with `BorrowStatus`.  
- **UserActivityLog** → audit logs for user actions.  

📌 `schema.sql` and `sample-data.sql` (with hashed passwords) are provided.  

---

## 👥 Roles & Permissions

| Role         | Permissions |
|--------------|-------------|
| 👑 Admin     | Full access: manage users, roles, books, members, logs |
| 📚 Librarian | Manage books, categories, copies, transactions |
| 🧾 Staff     | Handle borrowing/returning transactions |
| 🙋 Member    | Browse & borrow/return books |

---

## ⚙️ Setup Instructions

### ✅ Prerequisites
- Install **Java 21+**  
- Install **Maven 3.6+**  
- Install **MySQL 8+**  

---

### 1️⃣ Database Setup
```sql
CREATE DATABASE library_db;
CREATE USER 'lib_app'@'localhost' IDENTIFIED BY 'yourpassword';
GRANT ALL PRIVILEGES ON library_db.* TO 'lib_app'@'localhost';
FLUSH PRIVILEGES;
```

- Import `schema.sql` for schema.  
- Import `sample-data.sql` for seed data (includes admin account).  

---

### 2️⃣ Configure Connection
In `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=lib_app
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
server.port=8081
```

---

### 3️⃣ Build & Run
```bash
# build
./mvnw clean install -DskipTests

# run
./mvnw spring-boot:run
```

App runs at 👉 **http://localhost:8081**  

---

## 🧪 Testing APIs

- Import **Postman collection** (`Library-Management.postman_collection.json`)  
- Login with seeded admin (`admin/admin123`)  
- Example endpoints:  
  - `/api/auth/login` → authenticate user  
  - `/api/books` → manage books  
  - `/api/members` → manage members  
  - `/api/transactions` → borrow/return  

---

🔥 This LMS is ready to run with just database setup and configuration!  
