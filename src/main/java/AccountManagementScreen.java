import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class AccountManagementScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;

    // UI components
    private ComboBox<String> accountComboBox;
    private TextArea accountDetailsArea;
    private ListView<String> signatoriesListView;
    private TextField newSignatoryField;
    private TextField removeSignatoryField;

    public AccountManagementScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.accountDAO = new AccountDAO();
        this.customerDAO = new CustomerDAO();
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader();

        // FIXED: Back button - Check if employee or customer
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> {
            if (bankingSystem.getCurrentEmployee() != null) {
                navigationController.showEmployeeDashboard(); // Employee goes to employee dashboard
            } else {
                navigationController.showCustomerDashboard(); // Customer goes to customer dashboard
            }
        });

        // Main content
        VBox content = createContent();

        VBox mainContent = new VBox(20, backButton, content);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);

        mainLayout.setTop(header);
        mainLayout.setCenter(mainContent);

        scene = new Scene(mainLayout, 1000, 700);
    }

    private HBox createHeader() {
        Label titleLabel = new Label("Account Management");
        titleLabel.getStyleClass().add("header-title");

        Label subtitleLabel = new Label("Manage customer accounts and signatories");
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
        VBox content = new VBox(30);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        // Account selection section
        VBox accountSelectionSection = createAccountSelectionSection();

        // Account details section
        VBox accountDetailsSection = createAccountDetailsSection();

        // Signatory management section
        VBox signatorySection = createSignatorySection();

        content.getChildren().addAll(accountSelectionSection, accountDetailsSection, signatorySection);
        return content;
    }

    private VBox createAccountSelectionSection() {
        VBox section = new VBox(15);
        section.setMaxWidth(600);

        Label sectionTitle = new Label("Select Account");
        sectionTitle.getStyleClass().add("section-title");

        HBox selectionBox = new HBox(15);
        selectionBox.setAlignment(Pos.CENTER_LEFT);

        Label accountLabel = new Label("Account:");
        accountLabel.getStyleClass().add("form-label");

        accountComboBox = new ComboBox<>();
        accountComboBox.getStyleClass().add("form-field");
        accountComboBox.setPrefWidth(300);
        loadAccounts();

        accountComboBox.setOnAction(e -> displayAccountDetails());

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().addAll("btn", "btn-outline");
        refreshButton.setOnAction(e -> loadAccounts());

        selectionBox.getChildren().addAll(accountLabel, accountComboBox, refreshButton);

        section.getChildren().addAll(sectionTitle, selectionBox);
        return section;
    }

    private VBox createAccountDetailsSection() {
        VBox section = new VBox(15);
        section.setMaxWidth(600);

        Label sectionTitle = new Label("Account Details");
        sectionTitle.getStyleClass().add("section-title");

        accountDetailsArea = new TextArea();
        accountDetailsArea.setEditable(false);
        accountDetailsArea.setPrefHeight(150);
        accountDetailsArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12px;");
        accountDetailsArea.setText("Select an account to view details...");

        section.getChildren().addAll(sectionTitle, accountDetailsArea);
        return section;
    }

    private VBox createSignatorySection() {
        VBox section = new VBox(15);
        section.setMaxWidth(600);

        Label sectionTitle = new Label("Signatory Management");
        sectionTitle.getStyleClass().add("section-title");

        // Current signatories
        VBox currentSignatories = createCurrentSignatories();

        // Add signatory
        VBox addSignatory = createAddSignatory();

        // Remove signatory
        VBox removeSignatory = createRemoveSignatory();

        section.getChildren().addAll(sectionTitle, currentSignatories, addSignatory, removeSignatory);
        return section;
    }

    private VBox createCurrentSignatories() {
        VBox box = new VBox(10);

        Label title = new Label("Current Signatories:");
        title.getStyleClass().add("form-label");

        signatoriesListView = new ListView<>();
        signatoriesListView.setPrefHeight(120);

        box.getChildren().addAll(title, signatoriesListView);
        return box;
    }

    private VBox createAddSignatory() {
        VBox box = new VBox(10);

        Label title = new Label("Add New Signatory:");
        title.getStyleClass().add("form-label");

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        newSignatoryField = new TextField();
        newSignatoryField.setPromptText("Enter full name");
        newSignatoryField.getStyleClass().add("form-field");
        newSignatoryField.setPrefWidth(200);

        Button addButton = new Button("Add Signatory");
        addButton.getStyleClass().addAll("btn", "btn-success");
        addButton.setOnAction(e -> addSignatory());

        inputBox.getChildren().addAll(newSignatoryField, addButton);
        box.getChildren().addAll(title, inputBox);
        return box;
    }

    private VBox createRemoveSignatory() {
        VBox box = new VBox(10);

        Label title = new Label("Remove Signatory:");
        title.getStyleClass().add("form-label");

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        removeSignatoryField = new TextField();
        removeSignatoryField.setPromptText("Enter full name to remove");
        removeSignatoryField.getStyleClass().add("form-field");
        removeSignatoryField.setPrefWidth(200);

        Button removeButton = new Button("Remove Signatory");
        removeButton.getStyleClass().addAll("btn", "btn-danger");
        removeButton.setOnAction(e -> removeSignatory());

        inputBox.getChildren().addAll(removeSignatoryField, removeButton);
        box.getChildren().addAll(title, inputBox);
        return box;
    }

    private void loadAccounts() {
        try {
            List<Account> accounts = accountDAO.getAllAccounts();
            accountComboBox.getItems().clear();

            for (Account account : accounts) {
                String customerId = accountDAO.getCustomerIdForAccount(account.getAccountNumber());
                Customer customer = customerDAO.getCustomerById(customerId);
                String customerName = customer != null ? customer.getDisplayName() : "Unknown Customer";

                String displayText = account.getAccountNumber() + " - " +
                        customerName + " - " +
                        account.getAccountType() + " - " +
                        account.getFormattedBalance();
                accountComboBox.getItems().add(displayText);
            }

            if (!accounts.isEmpty()) {
                accountComboBox.setValue(accountComboBox.getItems().get(0));
                displayAccountDetails();
            } else {
                accountDetailsArea.setText("No accounts found in the system.");
                signatoriesListView.getItems().clear();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load accounts: " + e.getMessage());
        }
    }

    private void displayAccountDetails() {
        if (accountComboBox.getValue() == null) return;

        try {
            String accountSelection = accountComboBox.getValue();
            String accountNumber = accountSelection.split(" - ")[0];

            Account account = accountDAO.getAccountByNumber(accountNumber);
            if (account == null) {
                accountDetailsArea.setText("Account not found: " + accountNumber);
                return;
            }

            String customerId = accountDAO.getCustomerIdForAccount(accountNumber);
            Customer customer = customerDAO.getCustomerById(customerId);

            StringBuilder details = new StringBuilder();
            details.append("Account Number: ").append(account.getAccountNumber()).append("\n");
            details.append("Account Type: ").append(account.getAccountType()).append("\n");
            details.append("Balance: ").append(account.getFormattedBalance()).append("\n");
            details.append("Branch: ").append(account.getBranch()).append("\n");

            if (customer != null) {
                details.append("Customer: ").append(customer.getDisplayName()).append("\n");
                details.append("Customer ID: ").append(customer.getCustomerId()).append("\n");
            }

            if (account instanceof InvestmentAccount) {
                details.append("Account Features: High-interest investment account\n");
            } else if (account instanceof ChequeAccount) {
                ChequeAccount chequeAccount = (ChequeAccount) account;
                details.append("Employer: ").append(chequeAccount.getEmployerName()).append("\n");
                details.append("Employer Address: ").append(chequeAccount.getEmployerAddress()).append("\n");
            } else if (account instanceof SavingsAccount) {
                details.append("Account Features: Basic savings with interest\n");
            }

            accountDetailsArea.setText(details.toString());

            // Load signatories
            loadSignatories(accountNumber);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load account details: " + e.getMessage());
        }
    }

    private void loadSignatories(String accountNumber) {
        try {
            List<String> signatories = accountDAO.getSignatories(accountNumber);
            signatoriesListView.getItems().clear();
            signatoriesListView.getItems().addAll(signatories);
        } catch (Exception e) {
            System.out.println("Error loading signatories: " + e.getMessage());
        }
    }

    private void addSignatory() {
        if (accountComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an account first.");
            return;
        }

        String signatoryName = newSignatoryField.getText().trim();
        if (signatoryName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a signatory name.");
            return;
        }

        try {
            String accountSelection = accountComboBox.getValue();
            String accountNumber = accountSelection.split(" - ")[0];

            boolean success = accountDAO.addSignatory(accountNumber, signatoryName);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Signatory added successfully!");
                newSignatoryField.clear();
                loadSignatories(accountNumber);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add signatory.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add signatory: " + e.getMessage());
        }
    }

    private void removeSignatory() {
        if (accountComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an account first.");
            return;
        }

        String signatoryName = removeSignatoryField.getText().trim();
        if (signatoryName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a signatory name to remove.");
            return;
        }

        try {
            String accountSelection = accountComboBox.getValue();
            String accountNumber = accountSelection.split(" - ")[0];

            boolean success = accountDAO.removeSignatory(accountNumber, signatoryName);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Signatory removed successfully!");
                removeSignatoryField.clear();
                loadSignatories(accountNumber);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove signatory. Name may not exist.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove signatory: " + e.getMessage());
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