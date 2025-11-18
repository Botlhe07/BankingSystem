import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:banking.db";

    static {
        initializeDatabase();
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create tables if they don't exist
            String[] sqlStatements = {
                    // Customers Table
                    "CREATE TABLE IF NOT EXISTS customers (" +
                            "    customer_id VARCHAR(20) PRIMARY KEY," +
                            "    first_name VARCHAR(50) NOT NULL," +
                            "    last_name VARCHAR(50) NOT NULL," +
                            "    address VARCHAR(200)," +
                            "    phone_number VARCHAR(15)," +
                            "    email VARCHAR(100)," +
                            "    customer_type VARCHAR(20) NOT NULL," +
                            "    company_name VARCHAR(100)," +
                            "    company_address VARCHAR(200)," +
                            "    employment_info VARCHAR(200)," +
                            "    registration_number VARCHAR(100)," +
                            "    password VARCHAR(255) NOT NULL," +
                            "    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")",

                    // Employees Table
                    "CREATE TABLE IF NOT EXISTS employees (" +
                            "    employee_id VARCHAR(20) PRIMARY KEY," +
                            "    password VARCHAR(255) NOT NULL," +
                            "    first_name VARCHAR(50) NOT NULL," +
                            "    last_name VARCHAR(50) NOT NULL," +
                            "    position VARCHAR(50) NOT NULL," +
                            "    department VARCHAR(50) NOT NULL," +
                            "    is_active BOOLEAN DEFAULT TRUE," +
                            "    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")",

                    // Accounts Table
                    "CREATE TABLE IF NOT EXISTS accounts (" +
                            "    account_number VARCHAR(20) PRIMARY KEY," +
                            "    customer_id VARCHAR(20) NOT NULL," +
                            "    account_type VARCHAR(20) NOT NULL," +
                            "    balance DECIMAL(15,2) DEFAULT 0.00," +
                            "    interest_rate DECIMAL(5,4) DEFAULT 0.0000," +
                            "    branch VARCHAR(100)," +
                            "    is_active BOOLEAN DEFAULT TRUE," +
                            "    opened_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "    minimum_balance DECIMAL(15,2) DEFAULT 0.00," +
                            "    initial_deposit DECIMAL(15,2) DEFAULT 0.00," +
                            "    employer_name VARCHAR(100)," +
                            "    employer_address VARCHAR(200)," +
                            "    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)" +
                            ")",

                    // ADD THIS: Account Signatories Table (was missing!)
                    "CREATE TABLE IF NOT EXISTS account_signatories (" +
                            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    account_number VARCHAR(20) NOT NULL," +
                            "    signatory_name VARCHAR(100) NOT NULL," +
                            "    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "    FOREIGN KEY (account_number) REFERENCES accounts(account_number)" +
                            ")",

                    // Transactions Table
                    "CREATE TABLE IF NOT EXISTS transactions (" +
                            "    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "    account_number VARCHAR(20) NOT NULL," +
                            "    transaction_type VARCHAR(20) NOT NULL," +
                            "    amount DECIMAL(15,2) NOT NULL," +
                            "    balance_after DECIMAL(15,2) NOT NULL," +
                            "    description VARCHAR(200)," +
                            "    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "    FOREIGN KEY (account_number) REFERENCES accounts(account_number)" +
                            ")"
            };

            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}