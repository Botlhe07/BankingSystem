import java.util.List;
import java.sql.*;

public class BankingSystem {
    private CustomerDAO customerDAO;
    private BankEmployeeDAO employeeDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private Customer currentCustomer;
    private BankEmployee currentEmployee;

    public BankingSystem() {
        this.customerDAO = new CustomerDAO();
        this.employeeDAO = new BankEmployeeDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();

        // DEBUG: Check what's in database
        System.out.println("=== DATABASE STATUS ===");
        List<Customer> customers = customerDAO.getAllCustomers();
        List<BankEmployee> employees = employeeDAO.getAllEmployees();
        System.out.println("Customers in DB: " + customers.size());
        System.out.println("Employees in DB: " + employees.size());
        System.out.println("======================");

        // ADDED: Debug database contents
        debugDatabaseContents();
    }

    // ADD THIS METHOD:
    private void debugDatabaseContents() {
        System.out.println("=== DEBUG DATABASE CONTENTS ===");

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Show EXACT customer data
            System.out.println("CUSTOMERS:");
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers");
            int customerCount = 0;
            while (rs.next()) {
                customerCount++;
                System.out.println("  ID: " + rs.getString("customer_id") +
                        " | Password: " + rs.getString("password") +
                        " | Name: " + rs.getString("first_name") + " " + rs.getString("last_name") +
                        " | Type: " + rs.getString("customer_type"));
            }
            if (customerCount == 0) {
                System.out.println("  (no customers)");
            }

            // Show EXACT employee data
            System.out.println("EMPLOYEES:");
            rs = stmt.executeQuery("SELECT * FROM employees");
            int employeeCount = 0;
            while (rs.next()) {
                employeeCount++;
                System.out.println("  ID: " + rs.getString("employee_id") +
                        " | Password: " + rs.getString("password") +
                        " | Name: " + rs.getString("first_name") + " " + rs.getString("last_name"));
            }
            if (employeeCount == 0) {
                System.out.println("  (no employees)");
            }

            // Check database file location
            System.out.println("Database URL: " + conn.getMetaData().getURL());

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Debug error: " + e.getMessage());
        }
        System.out.println("=== END DEBUG ===");
    }

    // === LOGIN METHODS ===
    public boolean employeeLogin(String employeeId, String password) {
        BankEmployee employee = employeeDAO.verifyEmployee(employeeId, password);
        if (employee != null) {
            currentEmployee = employee;
            System.out.println("Employee login successful: " + employee.getFirstName());
            return true;
        }
        System.out.println("Employee login failed for: " + employeeId);
        return false;
    }

    public boolean customerLogin(String customerId, String password) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer != null && customer.authenticate(customerId, password)) {
            currentCustomer = customer;
            System.out.println("Customer login successful: " + customer.getDisplayName());
            return true;
        }
        System.out.println("Customer login failed for: " + customerId);
        return false;
    }

    public void logout() {
        if (currentCustomer != null) {
            System.out.println("Logging out customer: " + currentCustomer.getDisplayName());
        }
        if (currentEmployee != null) {
            System.out.println("Logging out employee: " + currentEmployee.getFirstName());
        }
        currentCustomer = null;
        currentEmployee = null;
    }

    // === EMPLOYEE MANAGEMENT ===
    public boolean createEmployee(BankEmployee employee) {
        return employeeDAO.createEmployee(employee);
    }

    public boolean employeeIdExists(String employeeId) {
        return employeeDAO.getEmployeeById(employeeId) != null;
    }

    public List<BankEmployee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    // === CUSTOMER MANAGEMENT ===
    public boolean createCustomer(Customer customer) {
        return customerDAO.createCustomer(customer);
    }

    public boolean customerIdExists(String customerId) {
        return customerDAO.getCustomerById(customerId) != null;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public Customer getCustomerById(String customerId) {
        return customerDAO.getCustomerById(customerId);
    }

    // === ACCOUNT MANAGEMENT ===
    public boolean createAccount(Account account, String customerId) {
        return accountDAO.createAccount(account, customerId);
    }

    public List<Account> getAccountsByCustomerId(String customerId) {
        return accountDAO.getAccountsByCustomer(customerId);
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }

    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        return accountDAO.updateAccountBalance(accountNumber, newBalance);
    }

    // === TRANSACTION MANAGEMENT ===
    // FIXED: Changed from createTransaction to recordTransaction
    public boolean createTransaction(Transaction transaction) {
        return transactionDAO.recordTransaction(transaction);
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        return transactionDAO.getTransactionsByAccount(accountNumber);
    }

    // === TRANSACTION METHODS ===
    public boolean depositToAccount(String accountNumber, double amount, String description) {
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account != null) {
            double newBalance = account.getBalance() + amount;

            // Update account balance
            if (accountDAO.updateAccountBalance(accountNumber, newBalance)) {
                // Create transaction record
                Transaction transaction = new Transaction(accountNumber, "DEPOSIT", amount, newBalance, description);
                if (transactionDAO.recordTransaction(transaction)) {
                    System.out.println("Deposit successful: P" + amount + " to account " + accountNumber);
                    return true;
                }
            }
        }
        System.out.println("Deposit failed for account: " + accountNumber);
        return false;
    }

    public boolean withdrawFromAccount(String accountNumber, double amount, String description) {
        Account account = accountDAO.getAccountByNumber(accountNumber);
        if (account != null && account.getBalance() >= amount) {
            double newBalance = account.getBalance() - amount;

            // Update account balance
            if (accountDAO.updateAccountBalance(accountNumber, newBalance)) {
                // Create transaction record
                Transaction transaction = new Transaction(accountNumber, "WITHDRAWAL", amount, newBalance, description);
                if (transactionDAO.recordTransaction(transaction)) {
                    System.out.println("Withdrawal successful: P" + amount + " from account " + accountNumber);
                    return true;
                }
            }
        }
        System.out.println("Withdrawal failed for account: " + accountNumber);
        return false;
    }

    // === GETTER METHODS ===
    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public BankEmployee getCurrentEmployee() {
        return currentEmployee;
    }

    public List<Account> getCurrentCustomerAccounts() {
        if (currentCustomer != null) {
            return accountDAO.getAccountsByCustomer(currentCustomer.getCustomerId());
        }
        return null;
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    // === DEBUG/HELPER METHODS ===
    public void printSystemStatus() {
        System.out.println("=== BANKING SYSTEM STATUS ===");
        List<BankEmployee> employees = employeeDAO.getAllEmployees();
        List<Customer> customers = customerDAO.getAllCustomers();
        List<Account> accounts = accountDAO.getAllAccounts();

        System.out.println("Employees: " + employees.size());
        System.out.println("Customers: " + customers.size());
        System.out.println("Accounts: " + accounts.size());

        double totalBalance = 0.0;
        for (Account account : accounts) {
            totalBalance += account.getBalance();
        }

        System.out.println("Total Balance: P" + String.format("%.2f", totalBalance));

        if (currentCustomer != null) {
            System.out.println("Current User: Customer - " + currentCustomer.getDisplayName());
        } else if (currentEmployee != null) {
            System.out.println("Current User: Employee - " + currentEmployee.getFirstName());
        } else {
            System.out.println("Current User: None (Logged out)");
        }
        System.out.println("=============================");
    }
}