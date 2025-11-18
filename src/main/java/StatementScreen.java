import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class StatementScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private TextArea statementTextArea;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private ComboBox<String> statementTypeComboBox;
    private ComboBox<String> customerComboBox;

    public StatementScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        createUI();
    }

    private void createUI() {
        // Create main container
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titleLabel = new Label("Account Statement");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // FIXED: Back button - goes to employee dashboard for employees
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> {
            if (bankingSystem.getCurrentEmployee() != null) {
                navigationController.showEmployeeDashboard();
            } else {
                navigationController.showCustomerDashboard();
            }
        });

        // Controls container
        VBox controlsContainer = new VBox(15);
        controlsContainer.setAlignment(Pos.CENTER);

        // Customer selection (for employees)
        if (bankingSystem.getCurrentEmployee() != null) {
            Label customerLabel = new Label("Select Customer:");
            customerComboBox = new ComboBox<>();
            loadCustomers();
            HBox customerBox = new HBox(10, customerLabel, customerComboBox);
            customerBox.setAlignment(Pos.CENTER);
            controlsContainer.getChildren().add(customerBox);
        }

        // Date selection
        HBox dateBox = new HBox(15);
        dateBox.setAlignment(Pos.CENTER);
        Label startDateLabel = new Label("From:");
        startDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
        Label endDateLabel = new Label("To:");
        endDatePicker = new DatePicker(LocalDate.now());
        dateBox.getChildren().addAll(startDateLabel, startDatePicker, endDateLabel, endDatePicker);

        // Statement type selection
        HBox typeBox = new HBox(15);
        typeBox.setAlignment(Pos.CENTER);
        Label typeLabel = new Label("Statement Type:");
        statementTypeComboBox = new ComboBox<>();
        statementTypeComboBox.getItems().addAll("Summary", "Detailed Transactions");
        statementTypeComboBox.setValue("Summary");
        typeBox.getChildren().addAll(typeLabel, statementTypeComboBox);

        // Generate button
        Button generateButton = new Button("Generate Statement");
        generateButton.setStyle("-fx-background-color: #2e8b57; -fx-text-fill: white;");
        generateButton.setOnAction(e -> generateStatement());

        // Add controls to container
        controlsContainer.getChildren().addAll(dateBox, typeBox, generateButton);

        // Statement display area
        Label statementLabel = new Label("Statement:");
        statementTextArea = new TextArea();
        statementTextArea.setEditable(false);
        statementTextArea.setWrapText(true);
        statementTextArea.setPrefHeight(400);
        statementTextArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12px;");

        // Add all components to main container
        mainContainer.getChildren().addAll(
                titleLabel,
                backButton,
                controlsContainer,
                statementLabel,
                statementTextArea
        );

        // Create scene
        scene = new Scene(mainContainer, 900, 700);
    }

    private void loadCustomers() {
        if (customerComboBox != null) {
            List<Customer> customers = customerDAO.getAllCustomers();
            customerComboBox.getItems().clear();
            for (Customer customer : customers) {
                customerComboBox.getItems().add(customer.getCustomerId() + " - " + customer.getDisplayName());
            }
            if (!customers.isEmpty()) {
                customerComboBox.setValue(customerComboBox.getItems().get(0));
            }
        }
    }

    private void generateStatement() {
        try {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String statementType = statementTypeComboBox.getValue();

            // Validate dates
            if (startDate == null || endDate == null) {
                showAlert("Error", "Please select both start and end dates.");
                return;
            }

            if (startDate.isAfter(endDate)) {
                showAlert("Error", "Start date cannot be after end date.");
                return;
            }

            // Get customer
            Customer customer = getSelectedCustomer();
            if (customer == null) {
                showAlert("Error", "No customer selected or found.");
                return;
            }

            // Generate and display statement
            String statement = generateCustomerStatement(customer, startDate, endDate, statementType);
            statementTextArea.setText(statement);

        } catch (Exception e) {
            showAlert("Error", "Failed to generate statement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Customer getSelectedCustomer() {
        // If employee is logged in, use selected customer from combo box
        if (bankingSystem.getCurrentEmployee() != null && customerComboBox != null) {
            String selected = customerComboBox.getValue();
            if (selected != null) {
                String customerId = selected.split(" - ")[0];
                return customerDAO.getCustomerById(customerId);
            }
        }
        // If customer is logged in, use current customer
        else if (bankingSystem.getCurrentCustomer() != null) {
            return bankingSystem.getCurrentCustomer();
        }
        return null;
    }

    private List<Transaction> getTransactionsByAccountAndDateRange(String accountNumber, LocalDate startDate, LocalDate endDate) {
        // Get all transactions for the account
        List<Transaction> allTransactions = transactionDAO.getTransactionsByAccount(accountNumber);

        // Filter by date range using Java stream
        return allTransactions.stream()
                .filter(transaction -> {
                    LocalDate transactionDate = transaction.getTimestamp().toLocalDate();
                    return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    private String generateCustomerStatement(Customer customer, LocalDate startDate, LocalDate endDate, String statementType) {
        StringBuilder statement = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Statement header
        statement.append("=== BANKING STATEMENT ===\n\n");
        statement.append("Customer: ").append(customer.getDisplayName()).append("\n");
        statement.append("Customer ID: ").append(customer.getCustomerId()).append("\n");
        statement.append("Customer Type: ").append(customer.getCustomerType()).append("\n");
        statement.append("Statement Period: ").append(startDate.format(dateFormatter))
                .append(" to ").append(endDate.format(dateFormatter)).append("\n");
        statement.append("Statement Date: ").append(LocalDate.now().format(dateFormatter)).append("\n");
        statement.append("Statement Type: ").append(statementType).append("\n\n");

        // Get customer accounts
        List<Account> accounts = accountDAO.getAccountsByCustomer(customer.getCustomerId());

        if (accounts.isEmpty()) {
            statement.append("No accounts found for this customer.\n");
            return statement.toString();
        }

        double totalBalance = 0.0;
        int totalTransactions = 0;

        // Account summary section
        statement.append("=== ACCOUNT SUMMARY ===\n");
        statement.append("========================================\n");

        for (Account account : accounts) {
            statement.append("Account: ").append(account.getAccountNumber()).append("\n");
            statement.append("Type: ").append(account.getAccountType()).append("\n");
            statement.append("Current Balance: ").append(account.getFormattedBalance()).append("\n");
            statement.append("Branch: ").append(account.getBranch()).append("\n");

            // Get transactions for this account within date range
            List<Transaction> transactions = getTransactionsByAccountAndDateRange(
                    account.getAccountNumber(), startDate, endDate);

            totalTransactions += transactions.size();
            totalBalance += account.getBalance();

            statement.append("Transactions in period: ").append(transactions.size()).append("\n");

            if (statementType.equals("Detailed Transactions") && !transactions.isEmpty()) {
                statement.append("\nTransaction Details:\n");
                statement.append("----------------------------------------\n");
                // Sort transactions by timestamp (newest first)
                transactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));

                for (Transaction transaction : transactions) {
                    statement.append(transaction.toString()).append("\n");
                }
            } else if (statementType.equals("Detailed Transactions") && transactions.isEmpty()) {
                statement.append("\nNo transactions in selected period.\n");
            }

            statement.append("----------------------------------------\n\n");
        }

        // Summary section
        statement.append("=== SUMMARY ===\n");
        statement.append("Total Accounts: ").append(accounts.size()).append("\n");
        statement.append("Total Balance: P").append(String.format("%.2f", totalBalance)).append("\n");
        statement.append("Total Transactions in Period: ").append(totalTransactions).append("\n\n");

        statement.append("=== END OF STATEMENT ===\n");
        statement.append("Generated by Banking System\n");
        statement.append("For inquiries, contact your branch.");

        return statement.toString();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}