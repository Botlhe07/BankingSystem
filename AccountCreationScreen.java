// AccountCreationScreen.java
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AccountCreationScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public AccountCreationScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Open New Account", "Create banking accounts for customers");

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> navigationController.showEmployeeDashboard());

        // Form content
        VBox formContent = createAccountForm();

        VBox content = new VBox(20, backButton, formContent);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 1000, 800);
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

    private VBox createAccountForm() {
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(600);
        formContainer.getStyleClass().add("form-container");

        Label formTitle = new Label("Account Creation");
        formTitle.getStyleClass().add("form-title");

        // Customer selection
        ComboBox<String> customerComboBox = new ComboBox<>();
        customerComboBox.getItems().addAll("Customer 1", "Customer 2", "Customer 3"); // Sample data
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
        initialDepositField.setPromptText("Initial Deposit Amount");
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

        Button createAccountButton = new Button("Create Account");
        createAccountButton.getStyleClass().addAll("btn", "btn-success");
        createAccountButton.setPrefHeight(45);
        createAccountButton.setMaxWidth(Double.MAX_VALUE);
        createAccountButton.setOnAction(e -> createAccount());

        formContainer.getChildren().addAll(
                formTitle,
                createLabel("Select Customer"), customerComboBox,
                createLabel("Account Type"), accountTypeComboBox,
                createLabel("Branch"), branchField,
                createLabel("Initial Deposit"), initialDepositField,
                createLabel("Employer Name"), employerNameField,
                createLabel("Employer Address"), employerAddressField,
                createAccountButton
        );

        return formContainer;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void createAccount() {
        // Implementation for account creation
        showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully!");
        navigationController.showEmployeeDashboard();
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
