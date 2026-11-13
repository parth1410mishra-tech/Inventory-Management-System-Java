CREATE DATABASE IF NOT EXISTS retail_db;
USE retail_db;
CREATE TABLE IF NOT EXISTS products (
 product_id INT PRIMARY KEY, product_name VARCHAR(100) NOT NULL,
 category VARCHAR(60) NOT NULL, quantity INT NOT NULL DEFAULT 0,
 price DECIMAL(10,2) NOT NULL,
 CHECK (quantity >= 0), CHECK (price > 0)
);