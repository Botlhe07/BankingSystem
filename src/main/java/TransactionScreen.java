import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class TransactionScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private String transactionType;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    // Form fields
    private ComboBox<String> accountComboBox;
    private TextField amountField;
    private TextField signatoryField;
    private TextArea descriptionArea;
    private Label resultLabel;

    public TransactionScreen(NavigationController navigationController, BankingSystem bankingSystem, String transactionType) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.transactionType = transactionType;
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader();

        // FIXED: Back button - Always go to correct dashboard
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> {
            // FIXED: Always go to customer dashboard for customers, employee dashboard for employees
            if (bankingSystem.getCurrentEmployee() != null) {
                navigationController.showEmployeeDashboard();
            } else {
                navigationController.showCustomerDashboard();
            }
        });

        // Form content
        VBox formContent = createTransactionForm();

        VBox content = new VBox(20, backButton, formContent);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 800, 600);
        scene.getStylesheets().add("banking-styles.css");
    }

    private HBox createHeader() {
        String titleText = transactionType + " Transaction";
        Label titleLabel = new Label(titleText);
        titleLabel.getStyleClass().add("header-title");

        String subtitleText = "Process " + transactionType.toLowerCase() + " for customer accounts";
        Label subtitleLabel = new Label(subtitleText);
        subtitleLabel.getStyleClass().add("header-subtitle");

        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);

        HBox header = new HBox(20, titleBox);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(15, 30, 15, 30));
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        return header;
    }

    private VBox createTransactionForm() {
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(500);
        formContainer.getStyleClass().add("form-container");
        formContainer.setPadding(new Insets(25));

        Label formTitle = new Label(transactionType + " Funds");
        formTitle.getStyleClass().add("form-title");

        // Account selection
        VBox accountSelection = createAccountSelection();

        // Amount input
        VBox amountInput = createAmountInput();

        // Signatory input
        VBox signatoryInput = createSignatoryInput();

        // Description input
        VBox descriptionInput = createDescriptionInput();

        // Result label
        resultLabel = new Label();
        resultLabel.getStyleClass().add("result-label");
        resultLabel.setWrapText(true);

        // Submit button
        Button submitButton = new Button("Process " + transactionType);
        submitButton.getStyleClass().addAll("btn", "btn-primary");
        submitButton.setPrefHeight(45);
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(e -> processTransaction());

        formContainer.getChildren().addAll(
                formTitle, accountSelection, amountInput, signatoryInput,
                descriptionInput, submitButton, resultLabel
        );

        return formContainer;
    }

    private VBox createAccountSelection() {
        VBox accountBox = new VBox(10);

        Label accountLabel = new Label("Select Account:");
        accountLabel.getStyleClass().add("form-label");

        accountComboBox = new ComboBox<>();
        accountComboBox.getStyleClass().add("form-field");
        loadAccounts();

        accountBox.getChildren().addAll(accountLabel, accountComboBox);
        return accountBox;
    }

    private void loadAccounts() {
        try {
            List<Account> accounts;
            if (bankingSystem.getCurrentEmployee() != null) {
                // Employee can see all accounts
                accounts = getAllAccounts();
            } else {
                // Customer can only see their own accounts
                accounts = getCurrentCustomerAccounts();
            }

            accountComboBox.getItems().clear();
            for (Account account : accounts) {
                String displayText = account.getAccountNumber() + " - " +
                        account.getAccountType() + " - " +
                        account.getFormattedBalance();
                accountComboBox.getItems().add(displayText);
            }

            if (!accounts.isEmpty()) {
                accountComboBox.setValue(accountComboBox.getItems().get(0));
            }
        } catch (Exception e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    private VBox createAmountInput() {
        VBox amountBox = new VBox(10);

        Label amountLabel = new Label("Amount (P):");
        amountLabel.getStyleClass().add("form-label");

        amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.getStyleClass().add("form-field");

        amountBox.getChildren().addAll(amountLabel, amountField);
        return amountBox;
    }

    private VBox createSignatoryInput() {
        VBox signatoryBox = new VBox(10);

        Label signatoryLabel = new Label("Authorized Signatory:");
        signatoryLabel.getStyleClass().add("form-label");

        signatoryField = new TextField();
        signatoryField.setPromptText("Enter full name (optional - will auto-fill)");
        signatoryField.getStyleClass().add("form-field");

        signatoryBox.getChildren().addAll(signatoryLabel, signatoryField);
        return signatoryBox;
    }

    private VBox createDescriptionInput() {
        VBox descriptionBox = new VBox(10);

        Label descriptionLabel = new Label("Description (Optional):");
        descriptionLabel.getStyleClass().add("form-label");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter transaction description");
        descriptionArea.setPrefHeight(80);
        descriptionArea.getStyleClass().add("form-field");

        descriptionBox.getChildren().addAll(descriptionLabel, descriptionArea);
        return descriptionBox;
    }

    private void processTransaction() {
        try {
            System.out.println("🚀 STARTING TRANSACTION PROCESS...");

            // Validate inputs
            if (accountComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select an account.");
                return;
            }

            if (amountField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter an amount.");
                return;
            }

            // FIXED: Make signatory optional - auto-fill if empty
            String signatory = signatoryField.getText().trim();
            if (signatory.isEmpty()) {
                // Auto-fill with account owner if no signatory provided
                String accountSelection = accountComboBox.getValue();
                String accountNumber = accountSelection.split(" - ")[0];
                String customerId = accountDAO.getCustomerIdForAccount(accountNumber);

                if (customerId != null) {
                    CustomerDAO customerDAO = new CustomerDAO();
                    Customer customer = customerDAO.getCustomerById(customerId);
                    signatory = customer != null ? customer.getDisplayName() : "Account Owner";
                    System.out.println("✅ Auto-filled signatory: " + signatory);
                } else {
                    signatory = "Account Owner";
                }
            }

            // Parse amount
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Amount must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid amount.");
                return;
            }

            // Extract account number from selection
            String accountSelection = accountComboBox.getValue();
            String accountNumber = accountSelection.split(" - ")[0];
            String description = descriptionArea.getText().trim();

            System.out.println("🔍 TRANSACTION DETAILS:");
            System.out.println("Account: " + accountNumber);
            System.out.println("Type: " + transactionType);
            System.out.println("Amount: " + amount);
            System.out.println("Signatory: " + signatory);
            System.out.println("Description: " + description);

            boolean success = false;
            String message = "";

            // FIXED: Use case-insensitive comparison
            if (transactionType.equalsIgnoreCase("Deposit")) {
                System.out.println("💰 PROCESSING DEPOSIT...");
                success = depositToAccount(accountNumber, amount, signatory, description);
                message = success ? "Deposit successful!" : "Deposit failed!";
            } else if (transactionType.equalsIgnoreCase("Withdraw") || transactionType.equalsIgnoreCase("Withdrawal")) {
                System.out.println("💸 PROCESSING WITHDRAWAL...");
                success = withdrawFromAccount(accountNumber, amount, signatory, description);
                message = success ? "Withdrawal successful!" : "Withdrawal failed!";
            } else {
                System.out.println("❌ Unknown transaction type: " + transactionType);
                showAlert(Alert.AlertType.ERROR, "Error", "Unknown transaction type: " + transactionType);
                return;
            }

            if (success) {
                resultLabel.setStyle("-fx-text-fill: green;");
                resultLabel.setText(message);

                // Clear form for next transaction
                amountField.clear();
                descriptionArea.clear();
                signatoryField.clear(); // Clear the signatory field too

                // Reload accounts to show updated balances
                loadAccounts();
            } else {
                resultLabel.setStyle("-fx-text-fill: red;");
                resultLabel.setText(message);
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to process transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Account> getCurrentCustomerAccounts() {
        Customer currentCustomer = bankingSystem.getCurrentCustomer();
        if (currentCustomer != null) {
            return accountDAO.getAccountsByCustomer(currentCustomer.getCustomerId());
        }
        return List.of();
    }

    private List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    private boolean depositToAccount(String accountNumber, double amount, String signatory, String description) {
        try {
            Account account = accountDAO.getAccountByNumber(accountNumber);
            if (account == null) {
                System.out.println("❌ Account not found: " + accountNumber);
                return false;
            }

            System.out.println("Current balance: " + account.getBalance());
            System.out.println("Deposit amount: " + amount);

            // Update balance
            double newBalance = account.getBalance() + amount;
            boolean balanceUpdated = accountDAO.updateAccountBalance(accountNumber, newBalance);

            System.out.println("Balance update result: " + balanceUpdated);
            System.out.println("New balance: " + newBalance);

            if (balanceUpdated) {
                // Record transaction
                Transaction transaction = new Transaction(
                        accountNumber,
                        "DEPOSIT",
                        amount,
                        newBalance,
                        description != null && !description.isEmpty() ? description : "Deposit authorized by: " + signatory
                );
                boolean transactionRecorded = transactionDAO.recordTransaction(transaction);
                System.out.println("Transaction recorded: " + transactionRecorded);
                return transactionRecorded;
            }
        } catch (Exception e) {
            System.out.println("❌ Error processing deposit: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private boolean withdrawFromAccount(String accountNumber, double amount, String signatory, String description) {
        try {
            Account account = accountDAO.getAccountByNumber(accountNumber);
            if (account == null) {
                System.out.println("❌ Account not found: " + accountNumber);
                return false;
            }

            System.out.println("Current balance: " + account.getBalance());
            System.out.println("Withdrawal amount: " + amount);

            // Check sufficient funds
            if (account.getBalance() < amount) {
                System.out.println("❌ Insufficient funds! Balance: " + account.getBalance() + ", Required: " + amount);
                showAlert(Alert.AlertType.ERROR, "Insufficient Funds",
                        "Account balance: " + account.getFormattedBalance() +
                                "\nWithdrawal amount: P" + String.format("%.2f", amount));
                return false;
            }

            System.out.println("✅ Sufficient funds available");

            // Update balance
            double newBalance = account.getBalance() - amount;
            boolean balanceUpdated = accountDAO.updateAccountBalance(accountNumber, newBalance);

            System.out.println("Balance update result: " + balanceUpdated);
            System.out.println("New balance: " + newBalance);

            if (balanceUpdated) {
                // Record transaction
                Transaction transaction = new Transaction(
                        accountNumber,
                        "WITHDRAWAL",
                        amount,
                        newBalance,
                        description != null && !description.isEmpty() ? description : "Withdrawal authorized by: " + signatory
                );
                boolean transactionRecorded = transactionDAO.recordTransaction(transaction);

                if (transactionRecorded) {
                    System.out.println("✅ Withdrawal successful: " + amount + " from " + accountNumber);
                    return true;
                } else {
                    // If transaction recording failed, revert the balance
                    System.out.println("❌ Failed to record transaction - reverting balance");
                    accountDAO.updateAccountBalance(accountNumber, account.getBalance());
                    return false;
                }
            } else {
                System.out.println("❌ Balance update failed");
            }
        } catch (Exception e) {
            System.out.println("❌ Error processing withdrawal: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
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