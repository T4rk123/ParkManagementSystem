-- Создание таблиц
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

CREATE TABLE attractions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    waiting_time INTEGER,
    capacity INTEGER
);

-- Тестовые данные (ADMIN хардкодится здесь; генерируется хэш BCrypt.hashpw("admin123", BCrypt.gensalt()) в Java)
INSERT INTO users (username, password, role) VALUES 
('admin', '$2a$10$examplehashedpassword', 'ADMIN'),  -- Замени на реальный хэш для 'admin123'
('user', '$2a$10$examplehashedpassword', 'USER');    -- Замени на реальный хэш для 'user123'

INSERT INTO attractions (name, description, price, waiting_time, capacity) VALUES 
('Американские горки', 'Экстремальный аттракцион', 500.00, 15, 20),
('Карусель', 'Для детей', 200.00, 5, 12),
('Колесо обозрения', 'Вид на парк', 300.00, 30, 8);
