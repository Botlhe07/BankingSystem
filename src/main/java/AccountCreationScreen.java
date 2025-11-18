import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;


public class AccountCreationScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private List<TextField> signatoryFields;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;

    // Form fields
    private ComboBox<String> customerComboBox;
    private ComboBox<String> accountTypeComboBox;
    private TextField branchField;
    private TextField initialDepositField;
    private TextField employerNameField;
    private TextField employerAddressField;
    private VBox signatoryContainer;
    private VBox investmentFields;
    private VBox chequeFields;

    public AccountCreationScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.signatoryFields = new ArrayList<>();
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Create New Account", "Open accounts for customers");

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> navigationController.showEmployeeDashboard());

        // Form content
        ScrollPane formScrollPane = createScrollableForm();

        VBox content = new VBox(20, backButton, formScrollPane);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        // Wrap entire layout in ScrollPane for scrolling
        ScrollPane mainScrollPane = new ScrollPane(mainLayout);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setFitToHeight(true);
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScrollPane.getStyleClass().add("scroll-pane");

        scene = new Scene(mainScrollPane, 900, 700);
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

    private ScrollPane createScrollableForm() {
        VBox formContent = createAccountForm();

        ScrollPane scrollPane = new ScrollPane(formContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setPadding(new Insets(10));

        return scrollPane;
    }

    private VBox createAccountForm() {
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(600);
        formContainer.getStyleClass().add("form-container");
        formContainer.setPadding(new Insets(25));

        Label formTitle = new Label("Account Creation");
        formTitle.getStyleClass().add("form-title");

        // Customer selection
        VBox customerSelection = createCustomerSelection();

        // Account type selection
        VBox accountTypeSelection = createAccountTypeSelection();

        // Common fields
        VBox commonFields = createCommonFields();

        // Account-specific fields
        investmentFields = createInvestmentFields();
        chequeFields = createChequeFields();

        // Signatory management
        VBox signatoryManagement = createSignatoryManagement();

        // Submit button
        Button submitButton = new Button("Create Account");
        submitButton.getStyleClass().addAll("btn", "btn-success");
        submitButton.setPrefHeight(45);
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(e -> createAccount());

        formContainer.getChildren().addAll(
                formTitle, customerSelection, accountTypeSelection, commonFields,
                investmentFields, chequeFields, signatoryManagement, submitButton
        );

        // Hide account-specific fields initially
        investmentFields.setVisible(false);
        chequeFields.setVisible(false);

        return formContainer;
    }

    private VBox createCustomerSelection() {
        VBox customerBox = new VBox(10);

        Label customerLabel = new Label("Select Customer:");
        customerLabel.getStyleClass().add("form-label");

        customerComboBox = new ComboBox<>();
        customerComboBox.getStyleClass().add("form-field");
        loadCustomers();

        customerBox.getChildren().addAll(customerLabel, customerComboBox);
        return customerBox;
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            customerComboBox.getItems().clear();
            for (Customer customer : customers) {
                customerComboBox.getItems().add(customer.getCustomerId() + " - " + customer.getDisplayName());
            }
        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private VBox createAccountTypeSelection() {
        VBox typeBox = new VBox(10);

        Label typeLabel = new Label("Account Type:");
        typeLabel.getStyleClass().add("form-label");

        accountTypeComboBox = new ComboBox<>();
        accountTypeComboBox.getItems().addAll("Savings Account", "Investment Account", "Cheque Account");
        accountTypeComboBox.setValue("Savings Account");
        accountTypeComboBox.getStyleClass().add("form-field");

        accountTypeComboBox.setOnAction(e -> updateAccountSpecificFields());

        typeBox.getChildren().addAll(typeLabel, accountTypeComboBox);
        return typeBox;
    }

    private VBox createCommonFields() {
        VBox commonBox = new VBox(10);

        Label branchLabel = new Label("Branch:");
        branchLabel.getStyleClass().add("form-label");

        branchField = new TextField();
        branchField.setPromptText("Enter branch name");
        branchField.getStyleClass().add("form-field");

        Label depositLabel = new Label("Initial Deposit:");
        depositLabel.getStyleClass().add("form-label");

        initialDepositField = new TextField();
        initialDepositField.setPromptText("0.00");
        initialDepositField.getStyleClass().add("form-field");

        commonBox.getChildren().addAll(branchLabel, branchField, depositLabel, initialDepositField);
        return commonBox;
    }

    private VBox createInvestmentFields() {
        VBox investmentBox = new VBox(10);
        investmentBox.getStyleClass().add("account-specific-fields");

        Label investmentLabel = new Label("Investment Account Requirements:");
        investmentLabel.getStyleClass().add("form-label");

        Label requirementLabel = new Label("• Minimum initial deposit: P500.00\n• Higher interest rates");
        requirementLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        investmentBox.getChildren().addAll(investmentLabel, requirementLabel);
        return investmentBox;
    }

    private VBox createChequeFields() {
        VBox chequeBox = new VBox(10);
        chequeBox.getStyleClass().add("account-specific-fields");

        Label chequeLabel = new Label("Cheque Account Information:");
        chequeLabel.getStyleClass().add("form-label");

        Label employerNameLabel = new Label("Employer Name:");
        employerNameLabel.getStyleClass().add("form-label");

        employerNameField = new TextField();
        employerNameField.setPromptText("Enter employer name");
        employerNameField.getStyleClass().add("form-field");

        Label employerAddressLabel = new Label("Employer Address:");
        employerAddressLabel.getStyleClass().add("form-label");

        employerAddressField = new TextField();
        employerAddressField.setPromptText("Enter employer address");
        employerAddressField.getStyleClass().add("form-field");

        chequeBox.getChildren().addAll(chequeLabel, employerNameLabel, employerNameField,
                employerAddressLabel, employerAddressField);
        return chequeBox;
    }

    private VBox createSignatoryManagement() {
        VBox signatoryBox = new VBox(15);

        Label signatoryLabel = new Label("Account Signatories:");
        signatoryLabel.getStyleClass().add("form-label");

        signatoryContainer = new VBox(10);

        Button addSignatoryButton = new Button("+ Add Signatory");
        addSignatoryButton.getStyleClass().addAll("btn", "btn-outline");
        addSignatoryButton.setOnAction(e -> addSignatoryField());

        // Add one initial signatory field
        addSignatoryField();

        signatoryBox.getChildren().addAll(signatoryLabel, signatoryContainer, addSignatoryButton);
        return signatoryBox;
    }

    private void addSignatoryField() {
        HBox signatoryRow = new HBox(10);
        signatoryRow.setAlignment(Pos.CENTER_LEFT);

        TextField signatoryField = new TextField();
        signatoryField.setPromptText("Signatory full name");
        signatoryField.getStyleClass().add("form-field");
        HBox.setHgrow(signatoryField, Priority.ALWAYS);

        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().addAll("btn", "btn-danger");
        removeButton.setOnAction(e -> {
            signatoryContainer.getChildren().remove(signatoryRow);
            signatoryFields.remove(signatoryField);
        });

        signatoryRow.getChildren().addAll(signatoryField, removeButton);
        signatoryContainer.getChildren().add(signatoryRow);
        signatoryFields.add(signatoryField);
    }

    private void updateAccountSpecificFields() {
        String selectedType = accountTypeComboBox.getValue();

        investmentFields.setVisible(selectedType.equals("Investment Account"));
        chequeFields.setVisible(selectedType.equals("Cheque Account"));
    }

    private void createAccount() {
        try {
            // Validate customer selection
            if (customerComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a customer.");
                return;
            }

            // Extract customer ID from selection
            String customerSelection = customerComboBox.getValue();
            String customerId = customerSelection.split(" - ")[0];

            // Get customer from database
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Selected customer not found.");
                return;
            }

            // Validate required fields
            if (branchField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter branch name.");
                return;
            }

            // Get signatories
            List<String> signatories = new ArrayList<>();
            for (TextField signatoryField : signatoryFields) {
                String signatory = signatoryField.getText().trim();
                if (!signatory.isEmpty()) {
                    signatories.add(signatory);
                }
            }

            // Create account in database
            boolean success = createAccountInDatabase(
                    customer,
                    accountTypeComboBox.getValue(),
                    branchField.getText(),
                    initialDepositField.getText(),
                    employerNameField.getText(),
                    employerAddressField.getText(),
                    signatories
            );

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Account created successfully for " + customer.getDisplayName() + "!");
                navigationController.showEmployeeDashboard();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean createAccountInDatabase(Customer customer, String accountType, String branch,
                                            String initialDepositStr, String employerName,
                                            String employerAddress, List<String> signatories) {
        try {
            // Generate account number
            String accountNumber = generateAccountNumber();
            double initialDeposit = 0.0;

            // Parse initial deposit
            if (!initialDepositStr.isEmpty()) {
                initialDeposit = Double.parseDouble(initialDepositStr);
                if (initialDeposit < 0) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Initial deposit cannot be negative.");
                    return false;
                }
            }

            // Create appropriate account type
            Account account;
            String cleanAccountType = accountType.split(" ")[0].toUpperCase();

            switch (cleanAccountType) {
                case "INVESTMENT":
                    // Validate investment account requirements
                    if (initialDeposit < 500.00) {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Investment accounts require minimum initial deposit of P500.00");
                        return false;
                    }
                    account = new InvestmentAccount(accountNumber, initialDeposit, branch);
                    break;

                case "CHEQUE":
                    account = new ChequeAccount(accountNumber, initialDeposit, branch, employerName, employerAddress);
                    break;

                case "SAVINGS":
                default:
                    account = new SavingsAccount(accountNumber, initialDeposit, branch);
                    break;
            }

            // Add signatories to account
            for (String signatory : signatories) {
                account.addSignatory(signatory);
            }

            // Save to database
            boolean success = accountDAO.createAccount(account, customer.getCustomerId());

            if (success && initialDeposit > 0) {
                // Record initial deposit transaction
                TransactionDAO transactionDAO = new TransactionDAO();
                Transaction initialTransaction = new Transaction(
                        accountNumber,
                        "DEPOSIT",
                        initialDeposit,
                        initialDeposit,
                        "Initial deposit - Account opening"
                );
                transactionDAO.recordTransaction(initialTransaction);
            }

            return success;

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid initial deposit amount. Please enter numbers only.");
            return false;
        } catch (Exception e) {
            System.out.println("Error creating account in database: " + e.getMessage());
            return false;
        }
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
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