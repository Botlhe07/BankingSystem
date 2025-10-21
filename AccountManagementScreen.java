import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountManagementScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public AccountManagementScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Account Management", "View and manage your accounts");

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> {
            if (bankingSystem.getCurrentCustomer() != null) {
                navigationController.showCustomerDashboard();
            } else {
                navigationController.showEmployeeDashboard();
            }
        });

        // Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        // Accounts table
        Label tableTitle = new Label("Your Accounts");
        tableTitle.getStyleClass().add("card-title");

        TableView<Account> accountsTable = createAccountsTable();

        // Action buttons
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button viewDetailsButton = new Button("View Account Details");
        viewDetailsButton.getStyleClass().addAll("btn", "btn-primary");
        viewDetailsButton.setOnAction(e -> viewAccountDetails(accountsTable));

        Button depositButton = new Button("Deposit");
        depositButton.getStyleClass().addAll("btn", "btn-success");
        depositButton.setOnAction(e -> navigationController.showTransactionScreen("Deposit"));

        Button withdrawButton = new Button("Withdraw");
        withdrawButton.getStyleClass().addAll("btn", "btn-warning");
        withdrawButton.setOnAction(e -> navigationController.showTransactionScreen("Withdraw"));

        Button transactionHistoryButton = new Button("Transaction History");
        transactionHistoryButton.getStyleClass().addAll("btn", "btn-secondary");
        transactionHistoryButton.setOnAction(e -> showTransactionHistory(accountsTable));

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().addAll("btn", "btn-outline");
        refreshButton.setOnAction(e -> refreshTable(accountsTable));

        actionButtons.getChildren().addAll(viewDetailsButton, depositButton, withdrawButton, transactionHistoryButton, refreshButton);

        content.getChildren().addAll(backButton, tableTitle, accountsTable, actionButtons);
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

    private TableView<Account> createAccountsTable() {
        TableView<Account> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        // Create columns
        TableColumn<Account, String> accountNumberCol = new TableColumn<>("Account Number");
        accountNumberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        TableColumn<Account, String> accountTypeCol = new TableColumn<>("Account Type");
        accountTypeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));

        TableColumn<Account, String> balanceCol = new TableColumn<>("Balance (BWP)");
        balanceCol.setCellValueFactory(cellData -> {
            Account account = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(account.getFormattedBalance());
        });

        TableColumn<Account, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));

        TableColumn<Account, String> signatoriesCol = new TableColumn<>("Signatories");
        signatoriesCol.setCellValueFactory(cellData -> {
            Account account = cellData.getValue();
            int signatoryCount = account.getSignatories().size();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(signatoryCount));
        });

        TableColumn<Account, String> transactionsCol = new TableColumn<>("Transactions");
        transactionsCol.setCellValueFactory(cellData -> {
            Account account = cellData.getValue();
            int transactionCount = account.getTransactionHistory().size();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(transactionCount));
        });

        table.getColumns().addAll(accountNumberCol, accountTypeCol, balanceCol, branchCol, signatoriesCol, transactionsCol);

        // Load data
        refreshTable(table);

        return table;
    }

    private void refreshTable(TableView<Account> table) {
        List<Account> accounts;
        if (bankingSystem.getCurrentCustomer() != null) {
            accounts = bankingSystem.getCurrentCustomerAccounts();
        } else {
            accounts = bankingSystem.getAllAccounts();
        }

        ObservableList<Account> accountData = FXCollections.observableArrayList(accounts);
        table.setItems(accountData);

        // Show message if no accounts
        if (accounts.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Accounts",
                    bankingSystem.getCurrentCustomer() != null ?
                            "You don't have any accounts yet." :
                            "No accounts found in the system.");
        }
    }

    private void viewAccountDetails(TableView<Account> table) {
        Account selectedAccount = table.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an account first.");
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("=== ACCOUNT DETAILS ===\n\n");
        details.append("Account Number: ").append(selectedAccount.getAccountNumber()).append("\n");
        details.append("Account Type: ").append(selectedAccount.getAccountType()).append("\n");
        details.append("Balance: ").append(selectedAccount.getFormattedBalance()).append("\n");
        details.append("Branch: ").append(selectedAccount.getBranch()).append("\n");
        details.append("Number of Signatories: ").append(selectedAccount.getSignatories().size()).append("\n");

        List<String> signatories = selectedAccount.getSignatories();
        if (!signatories.isEmpty()) {
            details.append("\nAuthorized Signatories:\n");
            for (String signatory : signatories) {
                details.append("  • ").append(signatory).append("\n");
            }
        } else {
            details.append("\nNo signatories assigned to this account.\n");
        }

        details.append("\nNumber of Transactions: ").append(selectedAccount.getTransactionHistory().size());

        showTextAlert("Account Details - " + selectedAccount.getAccountNumber(), details.toString());
    }

    private void showTransactionHistory(TableView<Account> table) {
        Account selectedAccount = table.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an account first.");
            return;
        }

        List<Transaction> transactions = selectedAccount.getTransactionHistory();
        StringBuilder history = new StringBuilder();
        history.append("=== TRANSACTION HISTORY ===\n\n");
        history.append("Account: ").append(selectedAccount.getAccountNumber()).append("\n");
        history.append("Account Type: ").append(selectedAccount.getAccountType()).append("\n");
        history.append("Current Balance: ").append(selectedAccount.getFormattedBalance()).append("\n\n");

        if (transactions.isEmpty()) {
            history.append("No transactions found for this account.");
        } else {
            history.append("Recent Transactions (Newest First):\n");
            history.append("----------------------------------------\n");

            // Show transactions in reverse order (newest first)
            List<Transaction> reversedTransactions = new ArrayList<>(transactions);
            Collections.reverse(reversedTransactions);

            for (Transaction transaction : reversedTransactions) {
                history.append(transaction.toString()).append("\n\n");
            }
        }

        showTextAlert("Transaction History - " + selectedAccount.getAccountNumber(), history.toString());
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