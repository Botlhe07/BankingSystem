import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankingSystem {
    private List<Customer> customers;
    private List<BankEmployee> employees;
    private Customer currentCustomer;
    private BankEmployee currentEmployee;
    private Scanner scanner;
    private DatabaseManager dbManager;

    public BankingSystem() {
        this.customers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.dbManager = new DatabaseManager();
    }

    public void initializeSampleData() {
        // No sample employees or customers - everything will be created by users
        System.out.println("Banking System initialized. No pre-existing data.");
    }

    public void run() {
        System.out.println("==========================================");
        System.out.println("    WELCOME TO THE BANKING SYSTEM");
        System.out.println("==========================================");

        boolean running = true;

        while (running) {
            if (currentCustomer == null && currentEmployee == null) {
                showMainMenu();
            } else if (currentCustomer != null) {
                showCustomerMenu();
            } else if (currentEmployee != null) {
                showEmployeeMenu();
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Register New Bank Employee");
        System.out.println("3. Exit");
        System.out.print("Please select an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                showLoginTypeMenu();
                break;
            case 2:
                registerNewEmployee();
                break;
            case 3:
                saveAllData();
                System.out.println("Thank you for using the Banking System. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void showLoginTypeMenu() {
        System.out.println("\n=== LOGIN TYPE ===");
        System.out.println("1. Customer Login");
        System.out.println("2. Bank Employee Login");
        System.out.println("3. Back to Main Menu");
        System.out.print("Please select an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                customerLogin();
                break;
            case 2:
                employeeLogin();
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void registerNewEmployee() {
        System.out.println("\n=== REGISTER NEW BANK EMPLOYEE ===");

        System.out.print("Employee ID: ");
        String empId = scanner.nextLine();

        // Check if employee ID already exists
        for (BankEmployee employee : employees) {
            if (employee.getEmployeeId().equals(empId)) {
                System.out.println("Employee ID already exists. Please choose a different one.");
                return;
            }
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Password validation
        if (password.length() < 8 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            System.out.println("Password must be at least 8 characters with both letters and numbers");
            return;
        }

        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();

        BankEmployee newEmployee = new BankEmployee(empId, password, firstName, lastName);
        employees.add(newEmployee);

        System.out.println("\n==========================================");
        System.out.println("  EMPLOYEE REGISTERED SUCCESSFULLY!");
        System.out.println("==========================================");
        System.out.println("Employee ID: " + empId);
        System.out.println("Password: " + password);
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("You can now login with these credentials.");
    }

    private void customerLogin() {
        System.out.println("\n=== CUSTOMER LOGIN ===");

        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
            System.out.println("Please ask a bank employee to create your customer account first.");
            return;
        }

        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        for (Customer customer : customers) {
            if (customer.authenticate(username, password)) {
                currentCustomer = customer;
                System.out.println("\n==========================================");
                System.out.println("  LOGIN SUCCESSFUL! Welcome, " + customer.getDisplayName());
                System.out.println("==========================================");
                return;
            }
        }

        System.out.println("Invalid username or password. Please try again.");
    }

    private void employeeLogin() {
        System.out.println("\n=== BANK EMPLOYEE LOGIN ===");

        if (employees.isEmpty()) {
            System.out.println("No bank employees registered yet.");
            System.out.println("Please register a new bank employee first.");
            return;
        }

        System.out.print("Employee ID: ");
        String empId = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        for (BankEmployee employee : employees) {
            if (employee.authenticate(empId, password)) {
                currentEmployee = employee;
                System.out.println("\n==========================================");
                System.out.println("  LOGIN SUCCESSFUL! Welcome, " + employee.getFirstName() + " " + employee.getLastName());
                System.out.println("==========================================");
                return;
            }
        }

        System.out.println("Invalid employee ID or password. Please try again.");
    }

    private void showCustomerMenu() {
        System.out.println("\n=== CUSTOMER DASHBOARD ===");
        System.out.println("Welcome, " + currentCustomer.getDisplayName() + "!");
        System.out.println("1. View All My Accounts");
        System.out.println("2. View Account Balance");
        System.out.println("3. Deposit Funds");
        System.out.println("4. Withdraw Funds");
        System.out.println("5. View Transaction History");
        System.out.println("6. Logout");
        System.out.print("Please select an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                viewAllAccounts();
                break;
            case 2:
                viewAccountBalance();
                break;
            case 3:
                depositFunds();
                break;
            case 4:
                withdrawFunds();
                break;
            case 5:
                viewTransactionHistory();
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void showEmployeeMenu() {
        System.out.println("\n=== EMPLOYEE DASHBOARD ===");
        System.out.println("Welcome, " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + "!");
        System.out.println("1. Create New Customer");
        System.out.println("2. Open New Account");
        System.out.println("3. View All Customers");
        System.out.println("4. View Customer Accounts");
        System.out.println("5. View Customer Transaction History");
        System.out.println("6. Process Monthly Interest");
        System.out.println("7. Generate Customer Statement");
        System.out.println("8. Register New Bank Employee");
        System.out.println("9. Logout");
        System.out.print("Please select an option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                createNewCustomer();
                break;
            case 2:
                openNewAccount();
                break;
            case 3:
                viewAllCustomers();
                break;
            case 4:
                viewCustomerAccounts();
                break;
            case 5:
                viewCustomerTransactionHistory();
                break;
            case 6:
                processMonthlyInterest();
                break;
            case 7:
                generateCustomerStatement();
                break;
            case 8:
                registerNewEmployee();
                break;
            case 9:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    // All other methods remain the same (createNewCustomer, openNewAccount, viewAllAccounts, etc.)
    // ... [rest of your methods unchanged]

    private void createNewCustomer() {
        System.out.println("\n=== CREATE NEW CUSTOMER ===");
        System.out.println("1. Individual Customer");
        System.out.println("2. Company Customer");
        System.out.print("Select customer type: ");

        int customerType = getIntInput();

        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Password validation
        if (password.length() < 8 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            System.out.println("Password must be at least 8 characters with both letters and numbers");
            return;
        }

        if (customerType == 1) {
            System.out.print("First Name: ");
            String firstName = scanner.nextLine();
            System.out.print("Last Name: ");
            String lastName = scanner.nextLine();
            System.out.print("Address: ");
            String address = scanner.nextLine();
            System.out.print("Phone Number: ");
            String phoneNumber = scanner.nextLine();
            System.out.print("Date of Birth (YYYY-MM-DD): ");
            String dateOfBirth = scanner.nextLine();
            System.out.print("Government ID: ");
            String governmentId = scanner.nextLine();
            System.out.print("Source of Income: ");
            String sourceOfIncome = scanner.nextLine();

            IndividualCustomer newCustomer = new IndividualCustomer(username, password, firstName, lastName,
                    address, phoneNumber, dateOfBirth, governmentId, sourceOfIncome);
            customers.add(newCustomer);
            System.out.println("Individual customer created successfully!");
            System.out.println("Username: " + username + " | Password: " + password);

        } else if (customerType == 2) {
            System.out.print("Company Name: ");
            String companyName = scanner.nextLine();
            System.out.print("Address: ");
            String address = scanner.nextLine();
            System.out.print("Phone Number: ");
            String phoneNumber = scanner.nextLine();
            System.out.print("Company Registration Number: ");
            String registrationNumber = scanner.nextLine();
            System.out.print("Company Contact Name: ");
            String contactName = scanner.nextLine();
            System.out.print("Company Address: ");
            String companyAddress = scanner.nextLine();

            CompanyCustomer newCustomer = new CompanyCustomer(username, password, companyName,
                    address, phoneNumber, registrationNumber, contactName, companyAddress);
            customers.add(newCustomer);
            System.out.println("Company customer created successfully!");
            System.out.println("Username: " + username + " | Password: " + password);

        } else {
            System.out.println("Invalid customer type.");
        }
    }

    private void openNewAccount() {
        System.out.println("\n=== OPEN NEW ACCOUNT ===");

        if (customers.isEmpty()) {
            System.out.println("No customers available. Please create a customer first.");
            return;
        }

        System.out.println("Select a customer:");
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            System.out.println((i + 1) + ". " + customer.getDisplayName());
        }

        System.out.print("Enter customer number: ");
        int customerIndex = getIntInput() - 1;

        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Invalid customer selection.");
            return;
        }

        Customer selectedCustomer = customers.get(customerIndex);

        System.out.println("Select account type:");
        System.out.println("1. Savings Account");
        System.out.println("2. Investment Account");
        System.out.println("3. Cheque Account");
        System.out.print("Enter account type: ");

        int accountType = getIntInput();
        System.out.print("Branch: ");
        String branch = scanner.nextLine();

        Account newAccount = null;

        switch (accountType) {
            case 1:
                newAccount = new SavingsAccount(generateAccountNumber(), 0.0, branch);
                break;
            case 2:
                System.out.print("Initial deposit (minimum $500): $");
                double initialDeposit = getDoubleInput();
                if (initialDeposit < 500.0) {
                    System.out.println("Investment account requires a minimum of $500.");
                    return;
                }
                newAccount = new InvestmentAccount(generateAccountNumber(), initialDeposit, branch);
                break;
            case 3:
                System.out.print("Employer Name: ");
                String employerName = scanner.nextLine();
                System.out.print("Employer Address: ");
                String employerAddress = scanner.nextLine();
                newAccount = new ChequeAccount(generateAccountNumber(), 0.0, branch, employerName, employerAddress);
                break;
            default:
                System.out.println("Invalid account type.");
                return;
        }

        selectedCustomer.addAccount(newAccount);
        System.out.println("Account created successfully. Account Number: " + newAccount.getAccountNumber());
    }

    private void viewAllAccounts() {
        System.out.println("\n=== YOUR ACCOUNTS ===");
        List<Account> accounts = currentCustomer.getAccounts();

        if (accounts.isEmpty()) {
            System.out.println("You don't have any accounts yet.");
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.println((i + 1) + ". " + account.getAccountNumber() + " - " +
                    account.getAccountType() + " - Balance: $" + account.getBalance());
        }
    }

    // ... [Include all your other existing methods here unchanged]
    // viewAccountBalance, depositFunds, withdrawFunds, viewTransactionHistory,
    // viewAllCustomers, viewCustomerAccounts, viewCustomerTransactionHistory,
    // processMonthlyInterest, generateCustomerStatement, logout, saveAllData,
    // generateAccountNumber, getIntInput, getDoubleInput

    private void viewAccountBalance() {
        List<Account> accounts = currentCustomer.getAccounts();

        if (accounts.isEmpty()) {
            System.out.println("You don't have any accounts yet.");
            return;
        }

        System.out.println("\nSelect an account to view balance:");
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.println((i + 1) + ". " + account.getAccountNumber() + " - " + account.getAccountType());
        }

        System.out.print("Enter account number: ");
        int accountIndex = getIntInput() - 1;

        if (accountIndex >= 0 && accountIndex < accounts.size()) {
            Account selectedAccount = accounts.get(accountIndex);
            System.out.println("Account Balance: $" + selectedAccount.getBalance());
        } else {
            System.out.println("Invalid account selection.");
        }
    }

    private void depositFunds() {
        List<Account> accounts = currentCustomer.getAccounts();

        if (accounts.isEmpty()) {
            System.out.println("You don't have any accounts yet.");
            return;
        }

        System.out.println("\nSelect an account to deposit to:");
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.println((i + 1) + ". " + account.getAccountNumber() + " - " + account.getAccountType());
        }

        System.out.print("Enter account number: ");
        int accountIndex = getIntInput() - 1;

        if (accountIndex >= 0 && accountIndex < accounts.size()) {
            Account selectedAccount = accounts.get(accountIndex);
            System.out.print("Enter amount to deposit: $");
            double amount = getDoubleInput();

            if (amount > 0) {
                selectedAccount.deposit(amount);
                System.out.println("Deposit successful. New balance: $" + selectedAccount.getBalance());
            } else {
                System.out.println("Invalid amount. Please enter a positive value.");
            }
        } else {
            System.out.println("Invalid account selection.");
        }
    }

    private void withdrawFunds() {
        List<Account> accounts = currentCustomer.getAccounts();

        if (accounts.isEmpty()) {
            System.out.println("You don't have any accounts yet.");
            return;
        }

        System.out.println("\nSelect an account to withdraw from:");
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.println((i + 1) + ". " + account.getAccountNumber() + " - " + account.getAccountType());
        }

        System.out.print("Enter account number: ");
        int accountIndex = getIntInput() - 1;

        if (accountIndex >= 0 && accountIndex < accounts.size()) {
            Account selectedAccount = accounts.get(accountIndex);

            if (selectedAccount instanceof SavingsAccount) {
                System.out.println("Withdrawals are not allowed from Savings accounts.");
                return;
            }

            System.out.print("Enter amount to withdraw: $");
            double amount = getDoubleInput();

            if (amount > 0) {
                if (selectedAccount.withdraw(amount)) {
                    System.out.println("Withdrawal successful. New balance: $" + selectedAccount.getBalance());
                } else {
                    System.out.println("Insufficient funds or invalid amount.");
                }
            } else {
                System.out.println("Invalid amount. Please enter a positive value.");
            }
        } else {
            System.out.println("Invalid account selection.");
        }
    }

    private void viewTransactionHistory() {
        List<Account> accounts = currentCustomer.getAccounts();

        if (accounts.isEmpty()) {
            System.out.println("You don't have any accounts yet.");
            return;
        }

        System.out.println("\nSelect an account to view transaction history:");
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.println((i + 1) + ". " + account.getAccountNumber() + " - " + account.getAccountType());
        }

        System.out.print("Enter account number: ");
        int accountIndex = getIntInput() - 1;

        if (accountIndex >= 0 && accountIndex < accounts.size()) {
            Account selectedAccount = accounts.get(accountIndex);
            List<Transaction> transactions = selectedAccount.getTransactionHistory();

            System.out.println("\nTransaction History for Account: " + selectedAccount.getAccountNumber());
            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
            } else {
                for (Transaction transaction : transactions) {
                    System.out.println(transaction);
                }
            }
        } else {
            System.out.println("Invalid account selection.");
        }
    }

    private void viewAllCustomers() {
        System.out.println("\n=== ALL CUSTOMERS ===");

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            System.out.println((i + 1) + ". " + customer.getDisplayName() + " - " + customer.getCustomerType());
        }
    }

    private void viewCustomerAccounts() {
        System.out.println("\n=== VIEW CUSTOMER ACCOUNTS ===");

        if (customers.isEmpty()) {
            System.out.println("No customers available.");
            return;
        }

        System.out.println("Select a customer:");
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            System.out.println((i + 1) + ". " + customer.getDisplayName());
        }

        System.out.print("Enter customer number: ");
        int customerIndex = getIntInput() - 1;

        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Invalid customer selection.");
            return;
        }

        Customer selectedCustomer = customers.get(customerIndex);
        List<Account> accounts = selectedCustomer.getAccounts();

        System.out.println("\nAccounts for " + selectedCustomer.getDisplayName() + ":");
        if (accounts.isEmpty()) {
            System.out.println("No accounts found for this customer.");
        } else {
            for (Account account : accounts) {
                System.out.println(account.getAccountNumber() + " - " +
                        account.getAccountType() + " - Balance: $" + account.getBalance());
            }
        }
    }

    private void viewCustomerTransactionHistory() {
        System.out.println("\n=== VIEW CUSTOMER TRANSACTION HISTORY ===");

        if (customers.isEmpty()) {
            System.out.println("No customers available.");
            return;
        }

        System.out.println("Select a customer:");
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            System.out.println((i + 1) + ". " + customer.getDisplayName());
        }

        System.out.print("Enter customer number: ");
        int customerIndex = getIntInput() - 1;

        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Invalid customer selection.");
            return;
        }

        Customer selectedCustomer = customers.get(customerIndex);
        List<Account> accounts = selectedCustomer.getAccounts();

        if (accounts.isEmpty()) {
            System.out.println("No accounts found for this customer.");
            return;
        }

        System.out.println("Select an account:");
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            System.out.println((i + 1) + ". " + account.getAccountNumber() + " - " + account.getAccountType());
        }

        System.out.print("Enter account number: ");
        int accountIndex = getIntInput() - 1;

        if (accountIndex < 0 || accountIndex >= accounts.size()) {
            System.out.println("Invalid account selection.");
            return;
        }

        Account selectedAccount = accounts.get(accountIndex);
        List<Transaction> transactions = selectedAccount.getTransactionHistory();

        System.out.println("\nTransaction History for Account: " + selectedAccount.getAccountNumber());
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

    private void processMonthlyInterest() {
        System.out.println("\n=== PROCESS MONTHLY INTEREST ===");

        int processedAccounts = 0;
        double totalInterestPaid = 0.0;

        for (Customer customer : customers) {
            for (Account account : customer.getAccounts()) {
                if (account instanceof InterestBearing) {
                    double interest = ((InterestBearing) account).calculateInterest();
                    account.deposit(interest);
                    processedAccounts++;
                    totalInterestPaid += interest;

                    System.out.println("Paid $" + interest + " interest to account " +
                            account.getAccountNumber() + " (" + customer.getDisplayName() + ")");
                }
            }
        }

        System.out.println("Interest processing completed. Processed " + processedAccounts +
                " accounts. Total interest paid: $" + totalInterestPaid);
    }

    private void generateCustomerStatement() {
        System.out.println("\n=== GENERATE CUSTOMER STATEMENT ===");

        if (customers.isEmpty()) {
            System.out.println("No customers available.");
            return;
        }

        System.out.println("Select a customer:");
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            System.out.println((i + 1) + ". " + customer.getDisplayName());
        }

        System.out.print("Enter customer number: ");
        int customerIndex = getIntInput() - 1;

        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Invalid customer selection.");
            return;
        }

        Customer selectedCustomer = customers.get(customerIndex);
        List<Account> accounts = selectedCustomer.getAccounts();

        if (accounts.isEmpty()) {
            System.out.println("No accounts found for this customer.");
            return;
        }

        System.out.println("\n=== CUSTOMER STATEMENT for " + selectedCustomer.getDisplayName() + " ===");
        System.out.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("==============================================");

        double totalBalance = 0.0;
        int totalTransactions = 0;

        for (Account account : accounts) {
            System.out.println("\nAccount: " + account.getAccountNumber() + " (" + account.getAccountType() + ")");
            System.out.println("Balance: $" + account.getBalance());
            totalBalance += account.getBalance();

            List<Transaction> transactions = account.getTransactionHistory();
            totalTransactions += transactions.size();

            if (transactions.isEmpty()) {
                System.out.println("No transactions for this account.");
            } else {
                System.out.println("Transaction History:");
                for (Transaction transaction : transactions) {
                    System.out.println("  " + transaction);
                }
            }
            System.out.println("----------------------------------------------");
        }

        System.out.println("SUMMARY:");
        System.out.println("Total Accounts: " + accounts.size());
        System.out.println("Total Balance: $" + totalBalance);
        System.out.println("Total Transactions: " + totalTransactions);
        System.out.println("==============================================");
    }

    private void logout() {
        if (currentCustomer != null) {
            System.out.println("Logging out customer: " + currentCustomer.getDisplayName());
            currentCustomer = null;
        } else if (currentEmployee != null) {
            System.out.println("Logging out employee: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
            currentEmployee = null;
        }
        System.out.println("Returning to main menu...");
    }

    private void saveAllData() {
        dbManager.saveData(this);
    }

    private String generateAccountNumber() {
        return "ACC" + (1000 + customers.stream()
                .mapToInt(c -> c.getAccounts().size())
                .sum() + 1);
    }

    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}