-- Banking System Database Schema

-- Drop existing tables (clean start)
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS account_signatories;

-- 1. Customers Table WITH PASSWORD
CREATE TABLE customers (
    customer_id VARCHAR(20) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    address VARCHAR(200),
    phone_number VARCHAR(15),
    email VARCHAR(100),
    customer_type VARCHAR(20) NOT NULL, -- 'INDIVIDUAL' or 'COMPANY'
    company_name VARCHAR(100), -- Only for company customers
    company_address VARCHAR(200), -- Only for company customers
    employment_info VARCHAR(200), -- Only for cheque accounts
    password VARCHAR(100) NOT NULL, -- Password for customer login
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Accounts Table
CREATE TABLE accounts (
    account_number VARCHAR(20) PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    account_type VARCHAR(20) NOT NULL, -- 'SAVINGS', 'INVESTMENT', 'CHEQUE'
    balance DECIMAL(15,2) DEFAULT 0.00,
    interest_rate DECIMAL(5,4) DEFAULT 0.0000,
    branch VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    opened_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    minimum_balance DECIMAL(15,2) DEFAULT 0.00,

    -- Investment account specific
    initial_deposit DECIMAL(15,2) DEFAULT 0.00,

    -- Cheque account specific
    employer_name VARCHAR(100),
    employer_address VARCHAR(200),

    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- 3. Transactions Table
CREATE TABLE transactions (
    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_number VARCHAR(20) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL, -- 'DEPOSIT', 'WITHDRAWAL', 'INTEREST'
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    description VARCHAR(200),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);

-- Employees table for bank staff
CREATE TABLE employees (
    employee_id VARCHAR(20) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    position VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE account_signatories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_number VARCHAR(20) NOT NULL,
    signatory_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);

-- Display confirmation
SELECT 'Database tables created successfully!' as status;