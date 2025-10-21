import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Collections; // Add this import

public class CustomerListScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public CustomerListScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Customer List", "View all registered customers");

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> navigationController.showEmployeeDashboard());

        // Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        // Customers table
        Label tableTitle = new Label("All Customers");
        tableTitle.getStyleClass().add("card-title");

        TableView<Customer> customersTable = createCustomersTable();

        // Action buttons
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button viewAccountsButton = new Button("View Customer Accounts");
        viewAccountsButton.getStyleClass().addAll("btn", "btn-primary");
        viewAccountsButton.setOnAction(e -> viewCustomerAccounts(customersTable));

        Button createStatementButton = new Button("Generate Statement");
        createStatementButton.getStyleClass().addAll("btn", "btn-success");
        createStatementButton.setOnAction(e -> generateCustomerStatement(customersTable));

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().addAll("btn", "btn-outline");
        refreshButton.setOnAction(e -> refreshTable(customersTable));

        actionButtons.getChildren().addAll(viewAccountsButton, createStatementButton, refreshButton);

        content.getChildren().addAll(backButton, tableTitle, customersTable, actionButtons);
        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().add("banking-styles.css");
    }

    private HBox createHeader(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("header-title");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("header-subtitle");

        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);

        HBox header = new HBox(20, titleBox);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(15, 30, 15, 30));
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        return header;
    }

    private TableView<Customer> createCustomersTable() {
        TableView<Customer> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        // Create columns
        TableColumn<Customer, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Customer, String> typeCol = new TableColumn<>("Customer Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("customerType"));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Display Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone Number");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Customer, String> accountsCol = new TableColumn<>("Number of Accounts");
        accountsCol.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            int accountCount = customer.getAccounts().size();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(accountCount));
        });

        TableColumn<Customer, String> totalBalanceCol = new TableColumn<>("Total Balance (BWP)");
        totalBalanceCol.setCellValueFactory(cellData -> {
            Customer customer = cellData.getValue();
            double totalBalance = customer.getAccounts().stream()
                    .mapToDouble(Account::getBalance)
                    .sum();
            return new javafx.beans.property.SimpleStringProperty("P" + String.format("%.2f", totalBalance));
        });

        table.getColumns().addAll(usernameCol, typeCol, nameCol, phoneCol, addressCol, accountsCol, totalBalanceCol);

        // Load actual customers from banking system
        refreshTable(table);

        return table;
    }

    private void refreshTable(TableView<Customer> table) {
        List<Customer> customers = bankingSystem.getAllCustomers();
        ObservableList<Customer> customerData = FXCollections.observableArrayList(customers);
        table.setItems(customerData);

        if (customers.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Customers", "No customers found in the system.");
        }
    }

    private void viewCustomerAccounts(TableView<Customer> table) {
        Customer selectedCustomer = table.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a customer first.");
            return;
        }

        List<Account> accounts = selectedCustomer.getAccounts();
        StringBuilder accountsInfo = new StringBuilder();
        accountsInfo.append("=== CUSTOMER ACCOUNTS ===\n\n");
        accountsInfo.append("Customer: ").append(selectedCustomer.getDisplayName()).append("\n");
        accountsInfo.append("Username: ").append(selectedCustomer.getUsername()).append("\n");
        accountsInfo.append("Customer Type: ").append(selectedCustomer.getCustomerType()).append("\n");
        accountsInfo.append("Total Accounts: ").append(accounts.size()).append("\n\n");

        if (accounts.isEmpty()) {
            accountsInfo.append("No accounts found for this customer.");
        } else {
            double totalBalance = 0.0;
            accountsInfo.append("ACCOUNTS:\n");
            accountsInfo.append("----------------------------------------\n");

            for (Account account : accounts) {
                accountsInfo.append("Account Number: ").append(account.getAccountNumber()).append("\n");
                accountsInfo.append("Account Type: ").append(account.getAccountType()).append("\n");
                accountsInfo.append("Balance: ").append(account.getFormattedBalance()).append("\n");
                accountsInfo.append("Branch: ").append(account.getBranch()).append("\n");
                accountsInfo.append("Signatories: ").append(account.getSignatories().size()).append("\n");
                accountsInfo.append("Transactions: ").append(account.getTransactionHistory().size()).append("\n");

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

        showTextAlert("Accounts for " + selectedCustomer.getDisplayName(), accountsInfo.toString());
    }

    private void generateCustomerStatement(TableView<Customer> table) {
        Customer selectedCustomer = table.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a customer first.");
            return;
        }

        List<Account> accounts = selectedCustomer.getAccounts();
        StringBuilder statement = new StringBuilder();
        statement.append("=== CUSTOMER ACCOUNT STATEMENT ===\n\n");
        statement.append("Customer: ").append(selectedCustomer.getDisplayName()).append("\n");
        statement.append("Username: ").append(selectedCustomer.getUsername()).append("\n");
        statement.append("Customer Type: ").append(selectedCustomer.getCustomerType()).append("\n");
        statement.append("Address: ").append(selectedCustomer.getAddress()).append("\n");
        statement.append("Phone: ").append(selectedCustomer.getPhoneNumber()).append("\n");
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

                List<Transaction> transactions = account.getTransactionHistory();
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

        showTextAlert("Account Statement - " + selectedCustomer.getDisplayName(), statement.toString());
    }

    private void showTextAlert(String title, String content) {
        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}