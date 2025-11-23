-- ============================
-- FULL RESET WITH CASCADE
-- ============================
TRUNCATE TABLE
    claims,
    payments,
    policy_coverage,
    policies,
    customers,
    coverages
RESTART IDENTITY CASCADE;

-- ============================
-- CUSTOMERS
-- ============================
INSERT INTO customers (first_name, last_name, email, phone)
VALUES
    ('Maxim', 'Ivanov', 'maxim23@easdgxample.com', '1234567890'),
    ('Olga', 'Petrova', 'olgaas@examplasfe.com', '9876543210');

-- ============================
-- COVERAGES
-- ============================
INSERT INTO coverages (name, description, limit_amount)
VALUES
    ('Health',  'Health medical coverage', 500000),
    ('Property', 'Property insurance coverage', 1000000),
    ('Travel',   'Travel incidents coverage', 200000);

-- ============================
-- POLICIES
-- ============================
INSERT INTO policies (policy_number, start_date, end_date, premium, status, customer_id)
VALUES
    ('POL-001', '2025-01-01', '2025-12-31', 15000, 'ACTIVE', 1),
    ('POL-002', '2025-02-01', '2026-01-31', 20000, 'ACTIVE', 2);

-- ============================
-- POLICY-COVERAGE LINKS
-- ============================
INSERT INTO policy_coverage (policy_id, coverage_id)
VALUES
    (1, 1),
    (1, 3),
    (2, 2);

-- ============================
-- PAYMENTS
-- ============================
INSERT INTO payments (payment_number, payment_date, amount, payment_method, status, policy_id)
VALUES
    ('PAY-1001', '2025-01-10', 15000, 'CARD', 'PAID', 1),
    ('PAY-2001', '2025-02-15', 20000, 'CASH', 'PAID', 2);

-- ============================
-- CLAIMS
-- ============================
INSERT INTO claims (claim_number, claim_date, claimed_amount, description, status, policy_id, coverage_id)
VALUES
    ('CLM-5001', '2025-03-01', 100000, 'Hospital visit', 'PENDING', 1, 1),
    ('CLM-5002', '2025-04-05', 50000, 'Lost luggage', 'APPROVED', 1, 3);
