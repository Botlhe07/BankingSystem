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

    public TransactionScreen(NavigationController navigationController, BankingSystem bankingSystem, String transactionType) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.transactionType = transactionType;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        String title = transactionType + " Funds";
        HBox header = createHeader(title, "Process financial transactions with authorization");

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> goBack());

        // Form content wrapped in ScrollPane
        ScrollPane scrollPane = createScrollableForm();

        VBox content = new VBox(20, backButton, scrollPane);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 900, 700);
        scene.getStylesheets().add("banking-styles.css");
    }

    private ScrollPane createScrollableForm() {
        VBox formContent = createTransactionForm();

        ScrollPane scrollPane = new ScrollPane(formContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setPadding(new Insets(10));

        return scrollPane;
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

    private VBox createTransactionForm() {
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(500);
        formContainer.getStyleClass().add("form-container");
        formContainer.setPadding(new Insets(25));

        Label formTitle = new Label(transactionType + " Funds");
        formTitle.getStyleClass().add("form-title");

        // Account selection
        ComboBox<String> accountComboBox = new ComboBox<>();
        updateAccountComboBox(accountComboBox);
        accountComboBox.setPromptText("Select Account");
        accountComboBox.getStyleClass().add("form-field");
        accountComboBox.setPrefHeight(40);

        // Amount field
        TextField amountField = new TextField();
        amountField.setPromptText("Enter Amount (P)");
        amountField.getStyleClass().add("form-field");
        amountField.setPrefHeight(40);

        // Signatory selection
        ComboBox<String> signatoryComboBox = new ComboBox<>();
        signatoryComboBox.setPromptText("Select Authorizing Signatory");
        signatoryComboBox.getStyleClass().add("form-field");
        signatoryComboBox.setPrefHeight(40);

        // Update signatories when account selection changes
        accountComboBox.setOnAction(e -> {
            signatoryComboBox.getItems().clear();
            String selectedAccount = accountComboBox.getValue();
            if (selectedAccount != null && !selectedAccount.isEmpty() && !selectedAccount.equals("No accounts available")) {
                try {
                    String accountNumber = selectedAccount.split(" - ")[0];
                    List<String> signatories = bankingSystem.getAccountSignatories(accountNumber);
                    if (signatories.isEmpty()) {
                        signatoryComboBox.getItems().add("No signatories found");
                    } else {
                        signatoryComboBox.getItems().addAll(signatories);
                    }
                } catch (Exception ex) {
                    System.out.println("Error loading signatories: " + ex.getMessage());
                }
            }
        });

        // Description field
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Transaction Description (Optional)");
        descriptionField.getStyleClass().add("form-field");
        descriptionField.setPrefHeight(40);

        Button submitButton = new Button("Process " + transactionType);
        submitButton.getStyleClass().addAll("btn",
                transactionType.equals("Deposit") ? "btn-success" : "btn-warning");
        submitButton.setPrefHeight(45);
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(e -> processTransaction(accountComboBox, amountField, signatoryComboBox, descriptionField));

        // Refresh button
        Button refreshButton = new Button("Refresh Accounts");
        refreshButton.getStyleClass().addAll("btn", "btn-outline");
        refreshButton.setOnAction(e -> updateAccountComboBox(accountComboBox));

        formContainer.getChildren().addAll(
                formTitle,
                createLabel("Select Account"), accountComboBox,
                createLabel("Amount (P)"), amountField,
                createLabel("Authorizing Signatory"), signatoryComboBox,
                createLabel("Description"), descriptionField,
                submitButton, refreshButton
        );

        return formContainer;
    }

    private void updateAccountComboBox(ComboBox<String> accountComboBox) {
        accountComboBox.getItems().clear();
        List<Account> accounts;

        if (bankingSystem.getCurrentCustomer() != null) {
            // Customer view
            accounts = bankingSystem.getCurrentCustomerAccounts();
        } else {
            // Employee view - show all accounts
            accounts = bankingSystem.getAllAccounts();
        }

        if (accounts.isEmpty()) {
            accountComboBox.getItems().add("No accounts available");
        } else {
            for (Account account : accounts) {
                String displayText = account.getAccountNumber() + " - " +
                        account.getAccountType() + " - Balance: " + account.getFormattedBalance();
                accountComboBox.getItems().add(displayText);
            }
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void processTransaction(ComboBox<String> accountComboBox, TextField amountField,
                                    ComboBox<String> signatoryComboBox, TextField descriptionField) {
        try {
            String selectedAccount = accountComboBox.getValue();
            String amountText = amountField.getText();
            String signatory = signatoryComboBox.getValue();

            // Validation
            if (selectedAccount == null || selectedAccount.isEmpty() || selectedAccount.equals("No accounts available")) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a valid account.");
                return;
            }

            if (amountText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter an amount.");
                return;
            }

            if (signatory == null || signatory.isEmpty() || signatory.equals("No signatories found")) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a valid signatory.");
                return;
            }

            String accountNumber = selectedAccount.split(" - ")[0];
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Amount must be greater than zero.");
                return;
            }

            // Process transaction
            boolean success = false;
            if (transactionType.equals("Deposit")) {
                success = bankingSystem.depositToAccount(accountNumber, amount, signatory);
                if (success) {
                    bankingSystem.saveAllData(); // Save after successful transaction
                }
            } else {
                success = bankingSystem.withdrawFromAccount(accountNumber, amount, signatory);
                if (success) {
                    bankingSystem.saveAllData(); // Save after successful transaction
                }
            }

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        transactionType + " of P" + String.format("%.2f", amount) +
                                " processed successfully!\nAuthorized by: " + signatory +
                                "\nAccount: " + accountNumber);
                goBack();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Transaction failed. Please check:\n" +
                                "- Account balance (for withdrawals)\n" +
                                "- Signatory authorization\n" +
                                "- Account validity");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid amount (numbers only).");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Transaction failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void goBack() {
        if (bankingSystem.getCurrentCustomer() != null) {
            navigationController.showCustomerDashboard();
        } else {
            navigationController.showEmployeeDashboard();
        }
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