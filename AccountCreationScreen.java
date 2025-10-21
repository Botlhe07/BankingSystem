import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountCreationScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private List<TextField> signatoryFields;

    public AccountCreationScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.signatoryFields = new ArrayList<>();
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Open New Account", "Create banking accounts with signatories");

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> navigationController.showEmployeeDashboard());

        // Form content wrapped in ScrollPane
        ScrollPane scrollPane = createScrollableForm();

        VBox content = new VBox(20, backButton, scrollPane);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 1000, 800);
        scene.getStylesheets().add("banking-styles.css");
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

    private VBox createAccountForm() {
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(600);
        formContainer.getStyleClass().add("form-container");
        formContainer.setPadding(new Insets(25));

        Label formTitle = new Label("Account Creation");
        formTitle.getStyleClass().add("form-title");

        // Customer selection
        ComboBox<String> customerComboBox = new ComboBox<>();
        for (Customer customer : bankingSystem.getAllCustomers()) {
            customerComboBox.getItems().add(customer.getDisplayName() + " (" + customer.getUsername() + ")");
        }
        customerComboBox.setPromptText("Select Customer");
        customerComboBox.getStyleClass().add("form-field");
        customerComboBox.setPrefHeight(40);

        // Account type selection
        ComboBox<String> accountTypeComboBox = new ComboBox<>();
        accountTypeComboBox.getItems().addAll("Savings Account", "Investment Account", "Cheque Account");
        accountTypeComboBox.setPromptText("Select Account Type");
        accountTypeComboBox.getStyleClass().add("form-field");
        accountTypeComboBox.setPrefHeight(40);

        // Branch field
        TextField branchField = new TextField();
        branchField.setPromptText("Branch Name");
        branchField.getStyleClass().add("form-field");
        branchField.setPrefHeight(40);

        // Initial deposit (for investment accounts)
        TextField initialDepositField = new TextField();
        initialDepositField.setPromptText("Initial Deposit Amount (P)");
        initialDepositField.getStyleClass().add("form-field");
        initialDepositField.setPrefHeight(40);

        // Additional fields for cheque accounts
        TextField employerNameField = new TextField();
        employerNameField.setPromptText("Employer Name");
        employerNameField.getStyleClass().add("form-field");
        employerNameField.setPrefHeight(40);

        TextField employerAddressField = new TextField();
        employerAddressField.setPromptText("Employer Address");
        employerAddressField.getStyleClass().add("form-field");
        employerAddressField.setPrefHeight(40);

        // Signatories section
        Label signatoryTitle = new Label("Account Signatories");
        signatoryTitle.getStyleClass().add("form-label");

        VBox signatoryContainer = new VBox(10);
        signatoryFields.clear();

        // Add first signatory field
        addSignatoryField(signatoryContainer);

        Button addSignatoryButton = new Button("+ Add Another Signatory");
        addSignatoryButton.getStyleClass().addAll("btn", "btn-outline");
        addSignatoryButton.setOnAction(e -> addSignatoryField(signatoryContainer));

        Button createAccountButton = new Button("Create Account");
        createAccountButton.getStyleClass().addAll("btn", "btn-success");
        createAccountButton.setPrefHeight(45);
        createAccountButton.setMaxWidth(Double.MAX_VALUE);
        createAccountButton.setOnAction(e -> createAccount(customerComboBox, accountTypeComboBox, branchField,
                initialDepositField, employerNameField, employerAddressField));

        formContainer.getChildren().addAll(
                formTitle,
                createLabel("Select Customer"), customerComboBox,
                createLabel("Account Type"), accountTypeComboBox,
                createLabel("Branch"), branchField,
                createLabel("Initial Deposit (P)"), initialDepositField,
                createLabel("Employer Name"), employerNameField,
                createLabel("Employer Address"), employerAddressField,
                signatoryTitle, signatoryContainer, addSignatoryButton,
                createAccountButton
        );

        return formContainer;
    }

    private void addSignatoryField(VBox container) {
        TextField signatoryField = new TextField();
        signatoryField.setPromptText("Signatory Full Name");
        signatoryField.getStyleClass().add("form-field");
        signatoryField.setPrefHeight(40);

        signatoryFields.add(signatoryField);
        container.getChildren().add(signatoryField);
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void createAccount(ComboBox<String> customerComboBox, ComboBox<String> accountTypeComboBox,
                               TextField branchField, TextField initialDepositField,
                               TextField employerNameField, TextField employerAddressField) {
        try {
            String selectedCustomer = customerComboBox.getValue();
            String accountType = accountTypeComboBox.getValue();
            String branch = branchField.getText();

            if (selectedCustomer == null || accountType == null || branch.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
                return;
            }

            // Extract username from selection (format: "Display Name (username)")
            String username = selectedCustomer.substring(selectedCustomer.lastIndexOf("(") + 1, selectedCustomer.lastIndexOf(")"));

            // Collect signatories
            List<String> signatories = new ArrayList<>();
            for (TextField signatoryField : signatoryFields) {
                if (!signatoryField.getText().isEmpty()) {
                    signatories.add(signatoryField.getText());
                }
            }

            if (signatories.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "At least one signatory is required.");
                return;
            }

            // Prepare additional info
            Map<String, String> additionalInfo = new HashMap<>();
            additionalInfo.put("initialDeposit", initialDepositField.getText().isEmpty() ? "0" : initialDepositField.getText());
            additionalInfo.put("employerName", employerNameField.getText());
            additionalInfo.put("employerAddress", employerAddressField.getText());

            // Create account
            bankingSystem.createAccountAndSave(username, accountType.split(" ")[0], branch, additionalInfo, signatories);

            // Show success message with account details
            StringBuilder message = new StringBuilder();
            message.append("Account created successfully!\n\n");
            message.append("Account Type: ").append(accountType).append("\n");
            message.append("Branch: ").append(branch).append("\n");
            message.append("Signatories:\n");
            for (String signatory : signatories) {
                message.append("  - ").append(signatory).append("\n");
            }
            message.append("\nAll transactions require authorization from one of the signatories.");

            showAlert(Alert.AlertType.INFORMATION, "Success", message.toString());
            navigationController.showEmployeeDashboard();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account: " + e.getMessage());
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