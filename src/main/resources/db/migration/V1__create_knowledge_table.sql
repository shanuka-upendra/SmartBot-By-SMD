CREATE TABLE knowledge
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    question   VARCHAR(255) NOT NULL,
    answer     VARCHAR(255) NOT NULL,
    category   VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);