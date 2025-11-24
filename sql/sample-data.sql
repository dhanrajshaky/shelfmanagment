-- sample-data.sql
-- Run this if you use MySQL and want example shelves & books
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS shelf (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  location VARCHAR(150),
  capacity INT NOT NULL DEFAULT 50,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS book (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(150),
  isbn VARCHAR(50) UNIQUE,
  shelf_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_shelf FOREIGN KEY (shelf_id) REFERENCES shelf(id) ON DELETE SET NULL
);

INSERT INTO shelf (name, location, capacity) VALUES
('Fiction A','Floor 1 - Aisle 1',100),
('Non-Fiction B','Floor 1 - Aisle 2',80),
('Reference','Floor 2 - Aisle 1',40);

INSERT INTO book (title, author, isbn, shelf_id) VALUES
('The Hobbit','J.R.R. Tolkien','9780000000001',1),
('Clean Code','Robert C. Martin','9780132350884',2),
('Encyclopedia of Things','Various','9781111111111',3);
