-- Shopping Database Sample Data
-- Author: Truong Gia Huy - 22698251

-- Insert Categories
INSERT INTO category (name) VALUES 
('Electronics'),
('Clothing'),
('Books'),
('Home & Garden'),
('Sports');

-- Insert Customers
INSERT INTO customer (username, password, name, customer_since) VALUES 
('admin', '$2a$10$dXJ3SW6G7PJI5Vj8vH9kCOYz/8KjJz8KjJz8KjJz8KjJz8KjJz', 'Admin User', '2023-01-01'),
('customer', '$2a$10$dXJ3SW6G7PJI5Vj8vH9kCOYz/8KjJz8KjJz8KjJz8KjJz8KjJz', 'Customer User', '2023-01-15'),
('nguyenvanan', '$2a$10$dXJ3SW6G7PJI5Vj8vH9kCOYz/8KjJz8KjJz8KjJz8KjJz8KjJz', 'Nguyen Van An', '2023-01-15'),
('tranthibinh', '$2a$10$dXJ3SW6G7PJI5Vj8vH9kCOYz/8KjJz8KjJz8KjJz8KjJz8KjJz', 'Tran Thi Binh', '2023-02-20'),
('levancuong', '$2a$10$dXJ3SW6G7PJI5Vj8vH9kCOYz/8KjJz8KjJz8KjJz8KjJz8KjJz', 'Le Van Cuong', '2023-03-10'),
('phamthidung', '$2a$10$dXJ3SW6G7PJI5Vj8vH9kCOYz/8KjJz8KjJz8KjJz8KjJz8KjJz', 'Pham Thi Dung', '2023-04-05'),
('hoangvanem', '$2a$10$dXJ3SW6G7PJI5Vj8vH9kCOYz/8KjJz8KjJz8KjJz8KjJz8KjJz', 'Hoang Van Em', '2023-05-12');

-- Insert Products
INSERT INTO product (name, price, in_stock, category_id) VALUES 
-- Electronics
('iPhone 15 Pro', 999.99, true, 1),
('Samsung Galaxy S24', 899.99, true, 1),
('MacBook Air M2', 1299.99, true, 1),
('Sony WH-1000XM5 Headphones', 399.99, true, 1),
('iPad Pro 12.9"', 1099.99, false, 1),

-- Clothing
('Nike Air Max 270', 150.00, true, 2),
('Adidas Ultraboost 22', 180.00, true, 2),
('Levi\'s 501 Jeans', 89.99, true, 2),
('Uniqlo Basic T-Shirt', 19.99, true, 2),
('Zara Winter Coat', 199.99, false, 2),

-- Books
('Clean Code by Robert Martin', 49.99, true, 3),
('Design Patterns by Gang of Four', 59.99, true, 3),
('Spring Boot in Action', 44.99, true, 3),
('JavaScript: The Good Parts', 39.99, true, 3),
('Effective Java by Joshua Bloch', 54.99, false, 3),

-- Home & Garden
('Philips Hue Smart Bulb Set', 79.99, true, 4),
('Dyson V15 Detect Vacuum', 749.99, true, 4),
('IKEA MALM Bed Frame', 199.99, true, 4),
('KitchenAid Stand Mixer', 429.99, true, 4),
('Nest Learning Thermostat', 249.99, false, 4),

-- Sports
('Wilson Pro Staff Tennis Racket', 229.99, true, 5),
('Nike Basketball', 59.99, true, 5),
('Adidas Football Boots', 149.99, true, 5),
('Yoga Mat Premium', 89.99, true, 5),
('Garmin Forerunner 945', 599.99, false, 5);

-- Insert Comments for Products
INSERT INTO comment (text, product_id) VALUES 
-- iPhone 15 Pro comments
('Amazing camera quality!', 1),
('Battery life is excellent', 1),
('Expensive but worth it', 1),

-- Samsung Galaxy S24 comments
('Great Android phone', 2),
('Display is fantastic', 2),
('Good value for money', 2),

-- MacBook Air M2 comments
('Super fast performance', 3),
('Lightweight and portable', 3),
('Best laptop I\'ve owned', 3),

-- Nike Air Max 270 comments
('Very comfortable shoes', 6),
('Great for running', 6),
('Good design', 6),

-- Clean Code book comments
('Must read for developers', 11),
('Changed how I write code', 11),
('Highly recommended', 11),

-- Design Patterns book comments
('Classic programming book', 12),
('Essential for software engineers', 12),
('Comprehensive coverage', 12),

-- Spring Boot book comments
('Great for learning Spring Boot', 13),
('Practical examples', 13),
('Well written', 13);

-- Insert Orders
INSERT INTO orders (date, customer_id) VALUES 
('2024-01-15', 1),
('2024-01-20', 2),
('2024-02-01', 1),
('2024-02-10', 3),
('2024-02-15', 4),
('2024-03-01', 2),
('2024-03-10', 5),
('2024-03-20', 3),
('2024-04-01', 1),
('2024-04-15', 4);

-- Insert OrderLines
INSERT INTO order_line (order_id, product_id, amount, purchase_price) VALUES 
-- Order 1: Nguyen Van An - Electronics
(1, 1, 1, 999.99),
(1, 4, 2, 399.99),

-- Order 2: Tran Thi Binh - Clothing
(2, 6, 1, 150.00),
(2, 8, 2, 89.99),
(2, 9, 3, 19.99),

-- Order 3: Nguyen Van An - Books
(3, 11, 1, 49.99),
(3, 12, 1, 59.99),
(3, 13, 2, 44.99),

-- Order 4: Le Van Cuong - Mixed
(4, 2, 1, 899.99),
(4, 16, 1, 79.99),
(4, 21, 1, 229.99),

-- Order 5: Pham Thi Dung - Home & Garden
(5, 15, 1, 79.99),
(5, 16, 1, 749.99),
(5, 17, 1, 199.99),

-- Order 6: Tran Thi Binh - Sports
(6, 21, 1, 229.99),
(6, 22, 1, 59.99),
(6, 23, 1, 149.99),

-- Order 7: Hoang Van Em - Electronics & Books
(7, 3, 1, 1299.99),
(7, 11, 1, 49.99),
(7, 14, 1, 39.99),

-- Order 8: Le Van Cuong - Clothing & Sports
(8, 7, 1, 180.00),
(8, 24, 1, 89.99),
(8, 25, 1, 599.99),

-- Order 9: Nguyen Van An - Home & Garden
(9, 18, 1, 429.99),
(9, 19, 1, 249.99),

-- Order 10: Pham Thi Dung - Books & Electronics
(10, 12, 1, 59.99),
(10, 13, 1, 44.99),
(10, 5, 1, 1099.99);
