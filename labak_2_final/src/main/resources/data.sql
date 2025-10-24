-- Очистка таблиц
DELETE FROM payments;
DELETE FROM claims;
DELETE FROM policy_coverage;
DELETE FROM policies;
DELETE FROM coverages;
DELETE FROM customers;

-- Сброс последовательностей (для H2)
ALTER TABLE customers ALTER COLUMN id RESTART WITH 1;
ALTER TABLE coverages ALTER COLUMN id RESTART WITH 1;
ALTER TABLE policies ALTER COLUMN id RESTART WITH 1;
ALTER TABLE claims ALTER COLUMN id RESTART WITH 1;
ALTER TABLE payments ALTER COLUMN id RESTART WITH 1;

-- Клиенты (БЕЗ указания ID)
INSERT INTO customers (first_name, last_name, email, phone) VALUES
                                                                ('Иван', 'Иванов', 'ivan@mail.ru', '+79161234567'),
                                                                ('Петр', 'Петров', 'petr@mail.ru', '+79167654321');

-- Покрытия (БЕЗ указания ID)
INSERT INTO coverages (name, description, limit_amount) VALUES
                                                            ('Авто-КАСКО', 'Полное покрытие автомобиля', 1000000.00),
                                                            ('Гражданская ответственность', 'ОСАГО', 400000.00),
                                                            ('Медицинское страхование', 'Покрытие медицинских расходов', 500000.00);

-- Полисы (БЕЗ указания ID)
INSERT INTO policies (policy_number, start_date, end_date, premium, status, customer_id) VALUES
                                                                                             ('POL001', '2024-01-01', '2024-12-31', 50000.00, 'ACTIVE', 1),
                                                                                             ('POL002', '2024-02-01', '2024-11-30', 30000.00, 'ACTIVE', 2);

-- Связь полисов с покрытиями
INSERT INTO policy_coverage (policy_id, coverage_id) VALUES
                                                         (1, 1),
                                                         (1, 2),
                                                         (2, 3);

-- Платежи (БЕЗ указания ID)
INSERT INTO payments (payment_number, payment_date, amount, payment_method, status, policy_id) VALUES
                                                                                                   ('PAY001', '2024-01-05', 50000.00, 'BANK_TRANSFER', 'COMPLETED', 1),
                                                                                                   ('PAY002', '2024-02-05', 30000.00, 'CARD', 'COMPLETED', 2);

-- Заявления (БЕЗ указания ID)
INSERT INTO claims (claim_number, claim_date, description, claimed_amount, status, policy_id, coverage_id) VALUES
                                                                                                               ('CLAIM001', '2024-03-01', 'ДТП по вине третьего лица', 150000.00, 'APPROVED', 1, 1),
                                                                                                               ('CLAIM002', '2024-04-01', 'Медицинские расходы', 50000.00, 'PENDING', 2, 3);