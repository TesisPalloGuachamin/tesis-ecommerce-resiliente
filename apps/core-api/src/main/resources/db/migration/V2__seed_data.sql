-- Seed data for demo user and products

-- Insert demo user
INSERT INTO users (email, password, name, enabled, created_at)
VALUES ('demo@example.com', '$2a$10$7J3s2T1r1Vm5z1l1E1T1e.2Q1r1V1m1z1l1E1T1e.2Q1r1V1m1z1l1E', 'Demo User', true, EXTRACT(EPOCH FROM NOW())::BIGINT * 1000);

-- Insert demo products
INSERT INTO products (sku, name, description, price, stock, active, created_at)
VALUES
    ('PROD001', 'Laptop Dell XPS 13', 'High-performance laptop with Intel i7 processor', 1299.99, 10, true, EXTRACT(EPOCH FROM NOW())::BIGINT * 1000),
    ('PROD002', 'Wireless Mouse Logitech', 'Ergonomic wireless mouse with USB receiver', 29.99, 50, true, EXTRACT(EPOCH FROM NOW())::BIGINT * 1000);

