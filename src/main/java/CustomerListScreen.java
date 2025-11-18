import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Collections;

public class CustomerListScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    // UI components
    private TableView<Customer> customerTable;
    private ObservableList<Customer> customerData;
    private TextField searchField;

    public CustomerListScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader();

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> navigationController.showEmployeeDashboard());

        // Main content
        VBox content = createContent();

        VBox mainContent = new VBox(20, backButton, content);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);

        mainLayout.setTop(header);
        mainLayout.setCenter(mainContent);

        scene = new Scene(mainLayout, 1200, 800);
    }

    private HBox createHeader() {
        Label titleLabel = new Label("Customer Management");
        titleLabel.getStyleClass().add("header-title");

        Label subtitleLabel = new Label("View and manage all customers");
        subtitleLabel.getStyleClass().add("header-subtitle");

        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);

        HBox header = new HBox(20, titleBox);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(15, 30, 15, 30));
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        return header;
    }

    private VBox createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        // Search and controls section
        VBox controlsSection = createControlsSection();

        // Table section
        VBox tableSection = createTableSection();

        content.getChildren().addAll(controlsSection, tableSection);
        return content;
    }

    private VBox createControlsSection() {
        VBox section = new VBox(15);
        section.setMaxWidth(1000);

        Label sectionTitle = new Label("Customer List");
        sectionTitle.getStyleClass().add("section-title");

        // Search and action buttons
        HBox controlsBox = new HBox(15);
        controlsBox.setAlignment(Pos.CENTER_LEFT);

        // Search field
        Label searchLabel = new Label("Search:");
        searchLabel.getStyleClass().add("form-label");

        searchField = new TextField();
        searchField.setPromptText("Search by name or ID...");
        searchField.getStyleClass().add("form-field");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterCustomers());

        // Action buttons
        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().addAll("btn", "btn-outline");
        refreshButton.setOnAction(e -> loadCustomers());

        Button viewAccountsButton = new Button("View Accounts");
        viewAccountsButton.getStyleClass().addAll("btn", "btn-primary");
        viewAccountsButton.setOnAction(e -> viewCustomerAccounts(customerTable));

        Button generateStatementButton = new Button("Generate Statement");
        generateStatementButton.getStyleClass().addAll("btn", "btn-success");
        generateStatementButton.setOnAction(e -> generateCustomerStatement(customerTable));

        Button registerCustomerButton = new Button("Register New Customer");
        registerCustomerButton.getStyleClass().addAll("btn", "btn-info");
        registerCustomerButton.setOnAction(e -> navigationController.showCustomerRegistration());

        controlsBox.getChildren().addAll(
                searchLabel, searchField, refreshButton, viewAccountsButton,
                generateStatementButton, registerCustomerButton
        );

        section.getChildren().addAll(sectionTitle, controlsBox);
        return section;
    }

    private VBox createTableSection() {
        VBox section = new VBox(10);
        section.setMaxWidth(1000);

        // Create table
        customerTable = new TableView<>();
        customerTable.setPrefHeight(500);
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create columns
        TableColumn<Customer, String> idCol = new TableColumn<>("Customer ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        idCol.setPrefWidth(120);

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        nameCol.setPrefWidth(150);

        TableColumn<Customer, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("customerType"));
        typeCol.setPrefWidth(100);

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setPrefWidth(120);

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(150);

        // FIXED: Accounts column with proper lambda expression
        TableColumn<Customer, String> accountsCol = new TableColumn<>("Accounts");
        accountsCol.setCellValueFactory(cellData -> {
            String customerId = cellData.getValue().getCustomerId();
            int accountCount = getCustomerAccountCount(customerId);
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(accountCount));
        });
        accountsCol.setPrefWidth(80);

        // FIXED: Balance column with proper lambda expression
        TableColumn<Customer, String> balanceCol = new TableColumn<>("Total Balance");
        balanceCol.setCellValueFactory(cellData -> {
            String customerId = cellData.getValue().getCustomerId();
            double totalBalance = getCustomerTotalBalance(customerId);
            return new javafx.beans.property.SimpleStringProperty(String.format("P%.2f", totalBalance));
        });
        balanceCol.setPrefWidth(120);

        customerTable.getColumns().addAll(idCol, nameCol, typeCol, phoneCol, emailCol, accountsCol, balanceCol);

        // Load initial data
        loadCustomers();

        section.getChildren().add(customerTable);
        return section;
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            customerData = FXCollections.observableArrayList(customers);
            customerTable.setItems(customerData);

            // Update statistics
            updateStatistics(customers);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load customers: " + e.getMessage());
        }
    }

    private void updateStatistics(List<Customer> customers) {
        int totalCustomers = customers.size();
        int totalAccounts = customers.stream()
                .mapToInt(c -> getCustomerAccountCount(c.getCustomerId()))
                .sum();
        double totalBalance = customers.stream()
                .mapToDouble(c -> getCustomerTotalBalance(c.getCustomerId()))
                .sum();

        System.out.println("Customer Statistics:");
        System.out.println("Total Customers: " + totalCustomers);
        System.out.println("Total Accounts: " + totalAccounts);
        System.out.println("Total Balance: P" + String.format("%.2f", totalBalance));
    }

    private void filterCustomers() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            customerTable.setItems(customerData);
        } else {
            ObservableList<Customer> filteredData = FXCollections.observableArrayList();
            for (Customer customer : customerData) {
                if (customer.getCustomerId().toLowerCase().contains(searchText) ||
                        customer.getDisplayName().toLowerCase().contains(searchText) ||
                        customer.getPhoneNumber().toLowerCase().contains(searchText) ||
                        customer.getEmail().toLowerCase().contains(searchText)) {
                    filteredData.add(customer);
                }
            }
            customerTable.setItems(filteredData);
        }
    }

    // UPDATED METHODS:
    private int getCustomerAccountCount(String customerId) {
        try {
            List<Account> accounts = accountDAO.getAccountsByCustomer(customerId);
            return accounts.size();
        } catch (Exception e) {
            System.out.println("Error getting account count for customer " + customerId + ": " + e.getMessage());
            return 0;
        }
    }

    private double getCustomerTotalBalance(String customerId) {
        try {
            List<Account> accounts = accountDAO.getAccountsByCustomer(customerId);
            return accounts.stream().mapToDouble(Account::getBalance).sum();
        } catch (Exception e) {
            System.out.println("Error getting total balance for customer " + customerId + ": " + e.getMessage());
            return 0.0;
        }
    }

    private void viewCustomerAccounts(TableView<Customer> table) {
        Customer selectedCustomer = table.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a customer first.");
            return;
        }

        // Get fresh customer data with accounts from database
        Customer freshCustomer = customerDAO.getCustomerById(selectedCustomer.getCustomerId());
        List<Account> accounts = accountDAO.getAccountsByCustomer(selectedCustomer.getCustomerId());

        StringBuilder accountsInfo = new StringBuilder();
        accountsInfo.append("=== CUSTOMER ACCOUNTS ===\n\n");
        accountsInfo.append("Customer: ").append(freshCustomer.getDisplayName()).append("\n");
        accountsInfo.append("Customer ID: ").append(freshCustomer.getCustomerId()).append("\n");
        accountsInfo.append("Customer Type: ").append(freshCustomer.getCustomerType()).append("\n");
        accountsInfo.append("Total Accounts: ").append(accounts.size()).append("\n\n");

        if (accounts.isEmpty()) {
            accountsInfo.append("No accounts found for this customer.");
        } else {
            double totalBalance = 0.0;
            accountsInfo.append("ACCOUNTS:\n");
            accountsInfo.append("----------------------------------------\n");

            for (Account account : accounts) {
                // Get transaction count for this account
                List<Transaction> transactions = transactionDAO.getTransactionsByAccount(account.getAccountNumber());

                accountsInfo.append("Account Number: ").append(account.getAccountNumber()).append("\n");
                accountsInfo.append("Account Type: ").append(account.getAccountType()).append("\n");
                accountsInfo.append("Balance: ").append(account.getFormattedBalance()).append("\n");
                accountsInfo.append("Branch: ").append(account.getBranch()).append("\n");
                accountsInfo.append("Signatories: ").append(account.getSignatories().size()).append("\n");
                accountsInfo.append("Transactions: ").append(transactions.size()).append("\n");

                // Show signatories
                List<String> signatories = account.getSignatories();
                if (!signatories.isEmpty()) {
                    accountsInfo.append("Authorized Signatories:\n");
                    for (String signatory : signatories) {
                        accountsInfo.append("  • ").append(signatory).append("\n");
                    }
                }

                accountsInfo.append("----------------------------------------\n");
                totalBalance += account.getBalance();
            }

            accountsInfo.append("\nTOTAL BALANCE: P").append(String.format("%.2f", totalBalance));
        }

        showTextAlert("Accounts for " + freshCustomer.getDisplayName(), accountsInfo.toString());
    }

    private void generateCustomerStatement(TableView<Customer> table) {
        Customer selectedCustomer = table.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a customer first.");
            return;
        }

        // Get fresh data from database
        Customer freshCustomer = customerDAO.getCustomerById(selectedCustomer.getCustomerId());
        List<Account> accounts = accountDAO.getAccountsByCustomer(selectedCustomer.getCustomerId());

        StringBuilder statement = new StringBuilder();
        statement.append("=== CUSTOMER ACCOUNT STATEMENT ===\n\n");
        statement.append("Customer: ").append(freshCustomer.getDisplayName()).append("\n");
        statement.append("Customer ID: ").append(freshCustomer.getCustomerId()).append("\n");
        statement.append("Customer Type: ").append(freshCustomer.getCustomerType()).append("\n");
        statement.append("Address: ").append(freshCustomer.getAddress()).append("\n");
        statement.append("Phone: ").append(freshCustomer.getPhoneNumber()).append("\n");
        statement.append("Statement Date: ").append(java.time.LocalDate.now()).append("\n\n");

        if (accounts.isEmpty()) {
            statement.append("No accounts found for this customer.\n");
        } else {
            double totalBalance = 0;
            int totalTransactions = 0;

            statement.append("ACCOUNT SUMMARY:\n");
            statement.append("========================================\n");

            for (Account account : accounts) {
                statement.append("Account: ").append(account.getAccountNumber()).append("\n");
                statement.append("Type: ").append(account.getAccountType()).append("\n");
                statement.append("Balance: ").append(account.getFormattedBalance()).append("\n");
                statement.append("Branch: ").append(account.getBranch()).append("\n");

                List<Transaction> transactions = transactionDAO.getTransactionsByAccount(account.getAccountNumber());
                totalTransactions += transactions.size();
                totalBalance += account.getBalance();

                if (transactions.isEmpty()) {
                    statement.append("No transactions.\n");
                } else {
                    statement.append("Recent Transactions:\n");
                    // Show last 5 transactions
                    List<Transaction> recentTransactions = transactions.size() > 5 ?
                            transactions.subList(transactions.size() - 5, transactions.size()) : transactions;
                    Collections.reverse(recentTransactions); // Show newest first

                    for (Transaction transaction : recentTransactions) {
                        statement.append("  ").append(transaction.toString()).append("\n");
                    }
                }
                statement.append("----------------------------------------\n");
            }

            statement.append("\n=== SUMMARY ===\n");
            statement.append("Total Accounts: ").append(accounts.size()).append("\n");
            statement.append("Total Balance: P").append(String.format("%.2f", totalBalance)).append("\n");
            statement.append("Total Transactions: ").append(totalTransactions).append("\n");
        }

        showTextAlert("Account Statement - " + freshCustomer.getDisplayName(), statement.toString());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showTextAlert(String title, String content) {
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(620, 450);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}