-- Initialize Car Reservation Database
-- This script runs automatically when MySQL container starts


USE car_rsvt;


-- Create driver table first (referenced by vehicle)
CREATE TABLE IF NOT EXISTS driver (
    driver_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    phone_number VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Create customer table
CREATE TABLE IF NOT EXISTS customer (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    status VARCHAR(20) DEFAULT 'A',
    payment_method_1 VARCHAR(100),
    payment_method_2 VARCHAR(100),
    detail_payment_method_1 VARCHAR(255),
    detail_payment_method_2 VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Create vehicle table (references driver)
CREATE TABLE IF NOT EXISTS vehicle (
    vehicle_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    driver_id BIGINT,
    vehicle_type VARCHAR(50) NOT NULL,
    license_plate VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    FOREIGN KEY (driver_id) REFERENCES driver(driver_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Create reservation table
CREATE TABLE IF NOT EXISTS reservation (
    reservation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    pickup_time DATETIME(6),
    pickup_location VARCHAR(255),
    dropoff_location VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE,
    INDEX idx_customer_reservation (customer_id),
    INDEX idx_vehicle_reservation (vehicle_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Create payment table
CREATE TABLE IF NOT EXISTS payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    method VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id) ON DELETE CASCADE,
    INDEX idx_reservation_payment (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Create payment_method table
CREATE TABLE IF NOT EXISTS payment_method (
    payment_method_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    method_type VARCHAR(50),
    details VARCHAR(255),
    primary_method BOOLEAN DEFAULT FALSE,
    created_at DATETIME(6),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    INDEX idx_customer_payment_method (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Create feedback table
CREATE TABLE IF NOT EXISTS feedback (
    feedback_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    FOREIGN KEY (reservation_id) REFERENCES reservation(reservation_id) ON DELETE CASCADE,
    INDEX idx_reservation_feedback (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- Insert sample data
INSERT INTO customer (username, password, email, phone_number, status) VALUES
('john', 'doe', 'john.doe@example.com', '555-0101', 'A'),
('jane', 'smith', 'jane.smith@example.com', '555-0102', 'A'),
('alex', 'brown', 'alex.brown@example.com', '555-0103', 'A');

INSERT INTO driver (name, license_number, status, phone_number) VALUES
('Ahmed Hassan', 'DL123456', 'AVAILABLE', '555-1001'),
('Mohammed Ali', 'DL234567', 'AVAILABLE', '555-1002'),
('Fatima Ibrahim', 'DL345678', 'AVAILABLE', '555-1003');

INSERT INTO vehicle (driver_id, vehicle_type, license_plate, status) VALUES
(1, 'SUV', 'ABC-1234', 'AVAILABLE'),
(2, 'SEDAN', 'DEF-5678', 'AVAILABLE'),
(3, 'VAN', 'GHI-9012', 'AVAILABLE');
