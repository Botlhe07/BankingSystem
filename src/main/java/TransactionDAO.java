import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // FIXED: Record transaction with proper constructor
    public boolean recordTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, balance_after, description, transaction_date) VALUES (?, ?, ?, ?, ?, ?)";

        System.out.println("🔍 TRANSACTION DEBUG:");
        System.out.println("Account: " + transaction.getAccountNumber());
        System.out.println("Type: " + transaction.getTransactionType());
        System.out.println("Amount: " + transaction.getAmount());
        System.out.println("Balance After: " + transaction.getBalanceAfter());
        System.out.println("Description: " + transaction.getDescription());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getAccountNumber());
            pstmt.setString(2, transaction.getTransactionType());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setDouble(4, transaction.getBalanceAfter());
            pstmt.setString(5, transaction.getDescription());
            pstmt.setTimestamp(6, Timestamp.valueOf(transaction.getTimestamp()));

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Transaction recorded: " + (rowsAffected > 0));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error recording transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get transactions by account number
    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // FIXED: Use proper constructor that matches your Transaction class
                Transaction transaction = new Transaction(
                        rs.getString("account_number"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getDouble("balance_after"),
                        rs.getString("description")
                );
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.out.println("Error getting transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Get transactions by account and date range
    public List<Transaction> getTransactionsByAccountAndDate(String accountNumber, java.util.Date startDate, java.util.Date endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? AND transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            pstmt.setTimestamp(2, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(3, new Timestamp(endDate.getTime()));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // FIXED: Use proper constructor that matches your Transaction class
                Transaction transaction = new Transaction(
                        rs.getString("account_number"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getDouble("balance_after"),
                        rs.getString("description")
                );
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.out.println("Error getting transactions by date: " + e.getMessage());
        }
        return transactions;
    }

    // Get all transactions (for reporting)
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // FIXED: Use proper constructor that matches your Transaction class
                Transaction transaction = new Transaction(
                        rs.getString("account_number"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getDouble("balance_after"),
                        rs.getString("description")
                );
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.out.println("Error getting all transactions: " + e.getMessage());
        }
        return transactions;
    }

    // Get transaction count for an account
    public int getTransactionCount(String accountNumber) {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.out.println("Error getting transaction count: " + e.getMessage());
        }
        return 0;
    }

    // NEW: Debug method to check database structure
    public void debugTransactionTable() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("🔍 TRANSACTION TABLE DEBUG:");

            // Check table structure
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(transactions)");
            System.out.println("Transaction Table Columns:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("name") + " (" + rs.getString("type") + ")");
            }

            // Check if table has data
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM transactions");
            rs.next();
            System.out.println("Total transactions in database: " + rs.getInt("count"));

        } catch (SQLException e) {
            System.out.println("Debug error: " + e.getMessage());
        }
    }
}