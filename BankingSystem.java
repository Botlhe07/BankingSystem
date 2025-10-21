import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankingSystem {
    private List<Customer> customers;
    private List<BankEmployee> employees;
    private Customer currentCustomer;
    private BankEmployee currentEmployee;
    private static final String DATA_FILE = "banking_data.dat";

    public BankingSystem() {
        this.customers = new ArrayList<>();
        this.employees = new ArrayList<>();
        loadData();
    }

    public void initializeSampleData() {
        if (customers.isEmpty() && employees.isEmpty()) {
            System.out.println("Banking System initialized with no existing data.");
        } else {
            System.out.println("Banking System initialized with existing data: " +
                    customers.size() + " customers, " + employees.size() + " employees");
        }
    }

    // === DATA PERSISTENCE METHODS ===
    @SuppressWarnings("unchecked")
    public void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            employees = (List<BankEmployee>) ois.readObject();
            customers = (List<Customer>) ois.readObject();
            System.out.println("Data loaded successfully: " + customers.size() + " customers, " + employees.size() + " employees");
        } catch (FileNotFoundException e) {
            System.out.println("No existing data file found. Starting with empty system.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
            employees = new ArrayList<>();
            customers = new ArrayList<>();
        }
    }

    public void saveAllData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(employees);
            oos.writeObject(customers);
            System.out.println("Data saved successfully: " + customers.size() + " customers, " + employees.size() + " employees");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    // === AUTOSAVE METHODS ===
    public void autoSave() {
        saveAllData();
    }

    public void createNewEmployeeAndSave(String employeeId, String password, String firstName, String lastName) {
        createNewEmployee(employeeId, password, firstName, lastName);
        autoSave();
    }

    public void createNewCustomerAndSave(String customerType, String username, String password, String address,
                                         String phoneNumber, Map<String, String> additionalInfo) {
        createNewCustomer(customerType, username, password, address, phoneNumber, additionalInfo);
        autoSave();
    }

    public void createAccountAndSave(String username, String accountType, String branch,
                                     Map<String, String> additionalInfo, List<String> signatories) {
        createAccountForCustomer(username, accountType, branch, additionalInfo, signatories);
        autoSave();
    }

    // === LOGIN METHODS ===
    public boolean customerLogin(String username, String password) {
        for (Customer customer : customers) {
            if (customer.authenticate(username, password)) {
                currentCustomer = customer;
                currentEmployee = null;
                System.out.println("Customer login successful: " + customer.getDisplayName());
                return true;
            }
        }
        System.out.println("Customer login failed for: " + username);
        return false;
    }

    public boolean employeeLogin(String employeeId, String password) {
        for (BankEmployee employee : employees) {
            if (employee.authenticate(employeeId, password)) {
                currentEmployee = employee;
                currentCustomer = null;
                System.out.println("Employee login successful: " + employee.getFirstName() + " " + employee.getLastName());
                return true;
            }
        }
        System.out.println("Employee login failed for: " + employeeId);
        return false;
    }

    public void logout() {
        if (currentCustomer != null) {
            System.out.println("Logging out customer: " + currentCustomer.getDisplayName());
        }
        if (currentEmployee != null) {
            System.out.println("Logging out employee: " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
        }
        currentCustomer = null;
        currentEmployee = null;
        saveAllData();
    }

    // === EMPLOYEE MANAGEMENT ===
    public void createNewEmployee(String employeeId, String password, String firstName, String lastName) {
        BankEmployee newEmployee = new BankEmployee(employeeId, password, firstName, lastName);
        employees.add(newEmployee);
        System.out.println("New employee created: " + employeeId);
    }

    public boolean employeeIdExists(String employeeId) {
        for (BankEmployee employee : employees) {
            if (employee.getEmployeeId().equals(employeeId)) {
                return true;
            }
        }
        return false;
    }

    // === CUSTOMER MANAGEMENT ===
    public void createNewCustomer(String customerType, String username, String password, String address,
                                  String phoneNumber, Map<String, String> additionalInfo) {
        Customer newCustomer;

        if (customerType.equals("individual")) {
            newCustomer = new IndividualCustomer(username, password,
                    additionalInfo.get("firstName"), additionalInfo.get("lastName"),
                    address, phoneNumber, additionalInfo.get("dateOfBirth"),
                    additionalInfo.get("governmentId"), additionalInfo.get("sourceOfIncome"));
        } else {
            newCustomer = new CompanyCustomer(username, password, additionalInfo.get("companyName"),
                    address, phoneNumber, additionalInfo.get("registrationNumber"),
                    additionalInfo.get("contactName"), additionalInfo.get("companyAddress"));
        }

        customers.add(newCustomer);
        System.out.println("New customer created: " + username + " (" + customerType + ")");
    }

    public boolean customerUsernameExists(String username) {
        for (Customer customer : customers) {
            if (customer.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    // === ACCOUNT MANAGEMENT WITH SIGNATORIES ===
    public void createAccountForCustomer(String username, String accountType, String branch,
                                         Map<String, String> additionalInfo, List<String> signatories) {
        Customer customer = findCustomerByUsername(username);
        if (customer == null) {
            System.out.println("Customer not found: " + username);
            return;
        }

        Account newAccount = null;
        String accountNumber = generateAccountNumber();

        switch (accountType) {
            case "Savings":
                newAccount = new SavingsAccount(accountNumber, 0.0, branch);
                break;
            case "Investment":
                double initialDeposit = 0.0;
                try {
                    initialDeposit = Double.parseDouble(additionalInfo.getOrDefault("initialDeposit", "0"));
                } catch (NumberFormatException e) {
                    initialDeposit = 0.0;
                }
                newAccount = new InvestmentAccount(accountNumber, initialDeposit, branch);
                break;
            case "Cheque":
                newAccount = new ChequeAccount(accountNumber, 0.0, branch,
                        additionalInfo.get("employerName"), additionalInfo.get("employerAddress"));
                break;
            default:
                System.out.println("Unknown account type: " + accountType);
                return;
        }

        if (newAccount != null) {
            // Add signatories to the account
            for (String signatory : signatories) {
                if (signatory != null && !signatory.trim().isEmpty()) {
                    newAccount.addSignatory(signatory);
                }
            }
            customer.addAccount(newAccount);
            System.out.println("Account created: " + accountNumber + " for customer: " + username);
        }
    }

    // === TRANSACTION METHODS WITH SIGNATORY SUPPORT ===
    public boolean depositToAccount(String accountNumber, double amount, String signatory) {
        Account account = findAccountByNumber(accountNumber);
        if (account != null) {
            if (account.hasSignatory(signatory)) {
                account.deposit(amount);
                // Create transaction with signatory
                Transaction transaction = new Transaction("DEPOSIT", amount, "Deposit to account", signatory);
                account.getTransactionHistory().add(transaction);
                System.out.println("Deposit successful: P" + amount + " to account " + accountNumber + " by " + signatory);
                saveAllData(); // Save immediately after transaction
                return true;
            } else {
                System.out.println("Deposit failed: Signatory " + signatory + " not authorized for account " + accountNumber);
            }
        } else {
            System.out.println("Deposit failed: Account not found - " + accountNumber);
        }
        return false;
    }

    public boolean withdrawFromAccount(String accountNumber, double amount, String signatory) {
        Account account = findAccountByNumber(accountNumber);
        if (account != null) {
            if (account.hasSignatory(signatory)) {
                if (account.withdraw(amount)) {
                    // Create transaction with signatory
                    Transaction transaction = new Transaction("WITHDRAWAL", amount, "Withdrawal from account", signatory);
                    account.getTransactionHistory().add(transaction);
                    System.out.println("Withdrawal successful: P" + amount + " from account " + accountNumber + " by " + signatory);
                    saveAllData(); // Save immediately after transaction
                    return true;
                } else {
                    System.out.println("Withdrawal failed: Insufficient funds in account " + accountNumber);
                }
            } else {
                System.out.println("Withdrawal failed: Signatory " + signatory + " not authorized for account " + accountNumber);
            }
        } else {
            System.out.println("Withdrawal failed: Account not found - " + accountNumber);
        }
        return false;
    }

    // === SIGNATORY MANAGEMENT ===
    public boolean addSignatoryToAccount(String accountNumber, String signatoryName) {
        Account account = findAccountByNumber(accountNumber);
        if (account != null) {
            account.addSignatory(signatoryName);
            saveAllData();
            System.out.println("Signatory added: " + signatoryName + " to account " + accountNumber);
            return true;
        }
        return false;
    }

    public boolean removeSignatoryFromAccount(String accountNumber, String signatoryName) {
        Account account = findAccountByNumber(accountNumber);
        if (account != null) {
            account.removeSignatory(signatoryName);
            saveAllData();
            System.out.println("Signatory removed: " + signatoryName + " from account " + accountNumber);
            return true;
        }
        return false;
    }

    public List<String> getAccountSignatories(String accountNumber) {
        Account account = findAccountByNumber(accountNumber);
        if (account != null) {
            return account.getSignatories();
        }
        return new ArrayList<>();
    }

    // === ACCOUNT FINDING METHODS ===
    private Customer findCustomerByUsername(String username) {
        for (Customer customer : customers) {
            if (customer.getUsername().equals(username)) {
                return customer;
            }
        }
        return null;
    }

    private Account findAccountByNumber(String accountNumber) {
        for (Customer customer : customers) {
            for (Account account : customer.getAccounts()) {
                if (account.getAccountNumber().equals(accountNumber)) {
                    return account;
                }
            }
        }
        return null;
    }

    // === INTEREST PROCESSING ===
    public void processMonthlyInterest() {
        double totalInterestPaid = 0.0;
        int processedAccounts = 0;

        for (Customer customer : customers) {
            for (Account account : customer.getAccounts()) {
                if (account instanceof InterestBearing) {
                    double interest = ((InterestBearing) account).calculateInterest();
                    account.deposit(interest);
                    // Add system-authorized interest transaction
                    Transaction interestTransaction = new Transaction("INTEREST", interest, "Monthly interest payment", "System");
                    account.getTransactionHistory().add(interestTransaction);
                    totalInterestPaid += interest;
                    processedAccounts++;

                    System.out.println("Interest paid: P" + interest + " to account " +
                            account.getAccountNumber() + " (" + customer.getDisplayName() + ")");
                }
            }
        }

        saveAllData(); // Save after processing all interest
        System.out.println("Interest processing completed: " + processedAccounts +
                " accounts processed. Total interest paid: P" + totalInterestPaid);
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
            return currentCustomer.getAccounts();
        }
        return new ArrayList<>();
    }

    public List<Account> getAllAccounts() {
        List<Account> allAccounts = new ArrayList<>();
        for (Customer customer : customers) {
            allAccounts.addAll(customer.getAccounts());
        }
        return allAccounts;
    }

    // === UTILITY METHODS ===
    private String generateAccountNumber() {
        int totalAccounts = customers.stream()
                .mapToInt(c -> c.getAccounts().size())
                .sum();
        return "ACC" + (1000 + totalAccounts + 1);
    }

    // === DEBUG/HELPER METHODS ===
    public void printSystemStatus() {
        System.out.println("=== BANKING SYSTEM STATUS ===");
        System.out.println("Employees: " + employees.size());
        System.out.println("Customers: " + customers.size());

        int totalAccounts = 0;
        double totalBalance = 0.0;
        for (Customer customer : customers) {
            totalAccounts += customer.getAccounts().size();
            for (Account account : customer.getAccounts()) {
                totalBalance += account.getBalance();
            }
        }

        System.out.println("Total Accounts: " + totalAccounts);
        System.out.println("Total Balance: P" + String.format("%.2f", totalBalance));

        if (currentCustomer != null) {
            System.out.println("Current User: Customer - " + currentCustomer.getDisplayName());
        } else if (currentEmployee != null) {
            System.out.println("Current User: Employee - " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
        } else {
            System.out.println("Current User: None (Logged out)");
        }
        System.out.println("=============================");
    }

    // Method to help with debugging - get customer by username
    public Customer getCustomerByUsername(String username) {
        return findCustomerByUsername(username);
    }

    // Method to help with debugging - get account by number
    public Account getAccountByNumber(String accountNumber) {
        return findAccountByNumber(accountNumber);
    }
}