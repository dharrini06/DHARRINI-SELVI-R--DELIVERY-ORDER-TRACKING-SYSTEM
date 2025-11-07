CREATE DATABASE deliverydb;

USE deliverydb;

CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100),
    product_name VARCHAR(100),
    quantity INT,
    price DECIMAL(10,2),
    order_date DATE
);

INSERT INTO orders (customer_name, product_name, quantity, price, order_date)
VALUES 
('Ravi Kumar', 'Laptop', 1, 55000.00, '2025-11-07'),
('Priya Sharma', 'Headphones', 2, 2500.00, '2025-11-06');
