import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // Create new account - now requires customerId as parameter
    public boolean createAccount(Account account, String customerId) {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, interest_rate, branch, initial_deposit, employer_name, employer_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, customerId);
            pstmt.setString(3, getAccountType(account));
            pstmt.setDouble(4, account.getBalance());

            // Set default interest rates based on account type
            double interestRate = getDefaultInterestRate(account);
            pstmt.setDouble(5, interestRate);

            pstmt.setString(6, account.getBranch());

            // Handle different account types
            if (account instanceof InvestmentAccount) {
                // For investment accounts, use balance as initial deposit
                pstmt.setDouble(7, account.getBalance());
                pstmt.setString(8, null);
                pstmt.setString(9, null);
            } else if (account instanceof ChequeAccount) {
                ChequeAccount chequeAccount = (ChequeAccount) account;
                pstmt.setDouble(7, 0.00);
                pstmt.setString(8, chequeAccount.getEmployerName());
                pstmt.setString(9, chequeAccount.getEmployerAddress());
            } else {
                pstmt.setDouble(7, 0.00);
                pstmt.setString(8, null);
                pstmt.setString(9, null);
            }

            int rowsAffected = pstmt.executeUpdate();

            // FIXED: Always add a default signatory when creating accounts
            if (rowsAffected > 0) {
                // Get customer name for the default signatory
                CustomerDAO customerDAO = new CustomerDAO();
                Customer customer = customerDAO.getCustomerById(customerId);
                String defaultSignatory = customer != null ? customer.getDisplayName() : "Account Owner";

                // Add default signatory
                addSignatory(account.getAccountNumber(), defaultSignatory);
                System.out.println("✅ Added default signatory: " + defaultSignatory);

                // Also add to the account object for immediate use
                account.addSignatory(defaultSignatory);
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
            return false;
        }
    }

    // FIXED: Get account by account number - REMOVED is_active filter
    public Account getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?"; // Removed AND is_active = TRUE

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                String branch = rs.getString("branch");

                Account account = null;
                switch (accountType) {
                    case "SAVINGS":
                        account = new SavingsAccount(accountNumber, balance, branch);
                        break;
                    case "INVESTMENT":
                        account = new InvestmentAccount(accountNumber, balance, branch);
                        break;
                    case "CHEQUE":
                        String employerName = rs.getString("employer_name");
                        String employerAddress = rs.getString("employer_address");
                        account = new ChequeAccount(accountNumber, balance, branch, employerName, employerAddress);
                        break;
                    default:
                        System.out.println("Unknown account type: " + accountType);
                        return null;
                }

                // Load signatories for the account
                if (account != null) {
                    List<String> signatories = getSignatories(accountNumber);
                    for (String signatory : signatories) {
                        account.addSignatory(signatory);
                    }

                    // FIXED: If no signatories found, add a default one
                    if (signatories.isEmpty()) {
                        String customerId = getCustomerIdForAccount(accountNumber);
                        if (customerId != null) {
                            CustomerDAO customerDAO = new CustomerDAO();
                            Customer customer = customerDAO.getCustomerById(customerId);
                            String defaultSignatory = customer != null ? customer.getDisplayName() : "Account Owner";
                            account.addSignatory(defaultSignatory);
                            System.out.println("✅ Added default signatory to existing account: " + defaultSignatory);
                        }
                    }
                }

                return account;
            }

        } catch (SQLException e) {
            System.out.println("Error getting account: " + e.getMessage());
        }
        return null;
    }

    // FIXED: Get all accounts for a customer - REMOVED is_active filter
    public List<Account> getAccountsByCustomer(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?"; // Removed AND is_active = TRUE

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = getAccountByNumber(rs.getString("account_number"));
                if (account != null) {
                    accounts.add(account);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting customer accounts: " + e.getMessage());
        }
        return accounts;
    }

    // FIXED: Update account balance - REMOVED is_active filter
    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?"; // Removed AND is_active = TRUE

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Balance updated successfully: " + accountNumber + " -> " + newBalance);
                return true;
            } else {
                System.out.println("❌ No rows affected when updating balance for: " + accountNumber);
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error updating account balance: " + e.getMessage());
            return false;
        }
    }

    // FIXED: Get all accounts (for employee view) - REMOVED is_active filter
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts"; // Removed WHERE is_active = TRUE

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Account account = getAccountByNumber(rs.getString("account_number"));
                if (account != null) {
                    accounts.add(account);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting all accounts: " + e.getMessage());
        }
        return accounts;
    }

    // FIXED: Get customer ID for an account - REMOVED is_active filter
    public String getCustomerIdForAccount(String accountNumber) {
        String sql = "SELECT customer_id FROM accounts WHERE account_number = ?"; // Removed AND is_active = TRUE

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("customer_id");
            }

        } catch (SQLException e) {
            System.out.println("Error getting customer ID for account: " + e.getMessage());
        }
        return null;
    }

    // === SIGNATORY MANAGEMENT METHODS ===

    private void saveSignatories(String accountNumber, List<String> signatories) {
        String sql = "INSERT INTO account_signatories (account_number, signatory_name) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (String signatory : signatories) {
                pstmt.setString(1, accountNumber);
                pstmt.setString(2, signatory);
                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            System.out.println("Error saving signatories: " + e.getMessage());
        }
    }

    public List<String> getSignatories(String accountNumber) {
        List<String> signatories = new ArrayList<>();
        String sql = "SELECT signatory_name FROM account_signatories WHERE account_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                signatories.add(rs.getString("signatory_name"));
            }

        } catch (SQLException e) {
            System.out.println("Error getting signatories: " + e.getMessage());
        }
        return signatories;
    }

    public boolean addSignatory(String accountNumber, String signatoryName) {
        String sql = "INSERT INTO account_signatories (account_number, signatory_name) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            pstmt.setString(2, signatoryName);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error adding signatory: " + e.getMessage());
            return false;
        }
    }

    public boolean removeSignatory(String accountNumber, String signatoryName) {
        String sql = "DELETE FROM account_signatories WHERE account_number = ? AND signatory_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            pstmt.setString(2, signatoryName);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error removing signatory: " + e.getMessage());
            return false;
        }
    }

    // NEW METHOD: Debug account information
    public void debugAccountInfo(String accountNumber) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM accounts WHERE account_number = ?")) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("🔍 ACCOUNT DEBUG INFO:");
                System.out.println("Account Number: " + rs.getString("account_number"));
                System.out.println("Customer ID: " + rs.getString("customer_id"));
                System.out.println("Account Type: " + rs.getString("account_type"));
                System.out.println("Balance: " + rs.getDouble("balance"));
                System.out.println("Branch: " + rs.getString("branch"));

                // Check if is_active column exists
                try {
                    boolean isActive = rs.getBoolean("is_active");
                    System.out.println("Is Active: " + isActive);
                } catch (SQLException e) {
                    System.out.println("is_active column not found in database");
                }
            } else {
                System.out.println("❌ Account not found: " + accountNumber);
            }

        } catch (SQLException e) {
            System.out.println("Debug error: " + e.getMessage());
        }
    }

    // Helper methods
    private String getAccountType(Account account) {
        if (account instanceof SavingsAccount) return "SAVINGS";
        if (account instanceof InvestmentAccount) return "INVESTMENT";
        if (account instanceof ChequeAccount) return "CHEQUE";
        return "UNKNOWN";
    }

    private double getDefaultInterestRate(Account account) {
        // Set default interest rates based on account type
        if (account instanceof SavingsAccount) return 0.0005;    // 0.05% monthly (0.6% annual)
        if (account instanceof InvestmentAccount) return 0.05;   // 5% monthly (60% annual)
        if (account instanceof ChequeAccount) return 0.0;        // 0%
        return 0.0;
    }
}