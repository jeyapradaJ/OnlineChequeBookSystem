-- =========================================
-- ONLINE CHEQUE BOOK REQUEST SYSTEM
-- Database: cheque_book_db
-- =========================================

CREATE DATABASE IF NOT EXISTS cheque_book_db;

USE cheque_book_db;


-- =========================================
-- CUSTOMERS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    phone VARCHAR(255),
    PRIMARY KEY (id)
);


-- =========================================
-- ADMINS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS admins (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    PRIMARY KEY (id)
);


-- =========================================
-- ACCOUNTS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    account_type VARCHAR(255),
    balance DOUBLE,
    customer_id BIGINT NOT NULL,
    PRIMARY KEY (id),

    CONSTRAINT fk_account_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);


-- =========================================
-- CHEQUE BOOK REQUESTS TABLE
-- =========================================

CREATE TABLE IF NOT EXISTS cheque_book_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    number_of_leaves INT NOT NULL,
    status VARCHAR(255),
    account_id BIGINT NOT NULL,
    PRIMARY KEY (id),

    CONSTRAINT fk_cheque_request_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id)
);


-- =========================================
-- CHECK TABLES
-- =========================================

SHOW TABLES;