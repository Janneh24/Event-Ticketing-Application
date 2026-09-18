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
