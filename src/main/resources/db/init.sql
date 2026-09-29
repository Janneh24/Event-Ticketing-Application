CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO users (username, password, role, enabled)
SELECT 'organizer', 'organizer', 'ORGANIZER', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'organizer');

CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    event_date VARCHAR(20) NOT NULL,
    venue_name VARCHAR(255) NOT NULL,
    total_seats INT NOT NULL DEFAULT 0,
    available_seats INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    seat_number VARCHAR(50) NOT NULL,
    section VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    UNIQUE (event_id, seat_number),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ticket_reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_date VARCHAR(20) NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    total_amount DOUBLE NOT NULL,
    user_id BIGINT,
    event_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE
);

-- Seed Default Users
INSERT INTO users (username, password, role, enabled)
SELECT 'customer', 'customer', 'CUSTOMER', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'customer');

-- Seed Sample Events
INSERT INTO events (id, title, event_date, venue_name, total_seats, available_seats)
SELECT 1, 'Coldplay World Tour 2026', '2026-10-15', 'Wembley Stadium', 6, 6
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 1);

INSERT INTO events (id, title, event_date, venue_name, total_seats, available_seats)
SELECT 2, 'Champions League Final 2026', '2026-11-20', 'San Siro Stadium', 4, 4
WHERE NOT EXISTS (SELECT 1 FROM events WHERE id = 2);

-- Seed Sample Seats for Event 1
INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 1, 'A-1', 'VIP', 150.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 1 AND seat_number = 'A-1');

INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 1, 'A-2', 'VIP', 150.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 1 AND seat_number = 'A-2');

INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 1, 'B-1', 'REGULAR', 75.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 1 AND seat_number = 'B-1');

INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 1, 'B-2', 'REGULAR', 75.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 1 AND seat_number = 'B-2');

INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 1, 'C-1', 'BALCONY', 45.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 1 AND seat_number = 'C-1');

INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 1, 'C-2', 'BALCONY', 45.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 1 AND seat_number = 'C-2');

-- Seed Sample Seats for Event 2
INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 2, 'VIP-1', 'VIP', 250.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 2 AND seat_number = 'VIP-1');

INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 2, 'VIP-2', 'VIP', 250.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 2 AND seat_number = 'VIP-2');

INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 2, 'CAT1-1', 'CATEGORY 1', 120.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 2 AND seat_number = 'CAT1-1');

INSERT INTO seats (event_id, seat_number, section, price, status)
SELECT 2, 'CAT1-2', 'CATEGORY 1', 120.0, 'AVAILABLE'
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE event_id = 2 AND seat_number = 'CAT1-2');

