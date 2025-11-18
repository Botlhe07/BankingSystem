import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomerRegistrationScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private ToggleGroup customerTypeGroup;
    private VBox formFields;
    private CustomerDAO customerDAO;

    // Field references for easier data collection
    private TextField customerIdField, addressField, phoneField, emailField;
    private PasswordField passwordField, confirmPasswordField;
    private TextField firstNameField, lastNameField, dobField, govIdField, incomeField;
    private TextField companyNameField, regNumberField, contactNameField, companyAddressField;

    public CustomerRegistrationScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.customerDAO = new CustomerDAO();
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Register New Customer", "Create customer accounts");

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
    }

    private ScrollPane createScrollableForm() {
        VBox formContent = createRegistrationForm();

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

    private VBox createRegistrationForm() {
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(600);
        formContainer.getStyleClass().add("form-container");
        formContainer.setPadding(new Insets(25));

        Label formTitle = new Label("Customer Registration");
        formTitle.getStyleClass().add("form-title");

        // Customer type selection
        VBox typeSelection = createCustomerTypeSelection();

        // Form fields will be dynamically updated based on customer type
        formFields = new VBox(15);
        formFields.setId("formFields");

        // Initial form setup for individual customer
        updateFormFields("individual");

        Button submitButton = new Button("Create Customer");
        submitButton.getStyleClass().addAll("btn", "btn-success");
        submitButton.setPrefHeight(45);
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(e -> createCustomer());

        formContainer.getChildren().addAll(formTitle, typeSelection, formFields, submitButton);
        return formContainer;
    }

    private VBox createCustomerTypeSelection() {
        VBox typeBox = new VBox(10);

        Label typeLabel = new Label("Customer Type:");
        typeLabel.getStyleClass().add("form-label");

        customerTypeGroup = new ToggleGroup();
        RadioButton individualRadio = new RadioButton("Individual Customer");
        RadioButton companyRadio = new RadioButton("Company Customer");

        individualRadio.setToggleGroup(customerTypeGroup);
        companyRadio.setToggleGroup(customerTypeGroup);
        individualRadio.setSelected(true);

        individualRadio.setOnAction(e -> updateFormFields("individual"));
        companyRadio.setOnAction(e -> updateFormFields("company"));

        HBox radioBox = new HBox(20, individualRadio, companyRadio);
        typeBox.getChildren().addAll(typeLabel, radioBox);

        return typeBox;
    }

    private void updateFormFields(String customerType) {
        formFields.getChildren().clear();

        // Customer ID field
        customerIdField = new TextField();
        customerIdField.setPromptText("Customer ID (e.g., C001)");
        customerIdField.getStyleClass().add("form-field");

        // Password fields
        passwordField = new PasswordField();
        passwordField.setPromptText("Password (min 6 characters)");
        passwordField.getStyleClass().add("form-field");

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.getStyleClass().add("form-field");

        addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.getStyleClass().add("form-field");

        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.getStyleClass().add("form-field");

        emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.getStyleClass().add("form-field");

        // Add common fields first
        formFields.getChildren().addAll(
                createLabel("Customer ID *"), customerIdField,
                createLabel("Password *"), passwordField,
                createLabel("Confirm Password *"), confirmPasswordField,
                createLabel("Address *"), addressField,
                createLabel("Phone Number *"), phoneField,
                createLabel("Email"), emailField
        );

        if (customerType.equals("individual")) {
            // Individual-specific fields
            firstNameField = new TextField();
            firstNameField.setPromptText("First Name");
            firstNameField.getStyleClass().add("form-field");

            lastNameField = new TextField();
            lastNameField.setPromptText("Last Name");
            lastNameField.getStyleClass().add("form-field");

            dobField = new TextField();
            dobField.setPromptText("Date of Birth (YYYY-MM-DD)");
            dobField.getStyleClass().add("form-field");

            govIdField = new TextField();
            govIdField.setPromptText("Government ID");
            govIdField.getStyleClass().add("form-field");

            incomeField = new TextField();
            incomeField.setPromptText("Source of Income");
            incomeField.getStyleClass().add("form-field");

            // Add individual-specific fields
            formFields.getChildren().addAll(
                    createLabel("First Name *"), firstNameField,
                    createLabel("Last Name *"), lastNameField,
                    createLabel("Date of Birth"), dobField,
                    createLabel("Government ID"), govIdField,
                    createLabel("Source of Income"), incomeField
            );
        } else {
            // Company-specific fields
            companyNameField = new TextField();
            companyNameField.setPromptText("Company Name");
            companyNameField.getStyleClass().add("form-field");

            regNumberField = new TextField();
            regNumberField.setPromptText("Registration Number");
            regNumberField.getStyleClass().add("form-field");

            contactNameField = new TextField();
            contactNameField.setPromptText("Contact Name");
            contactNameField.getStyleClass().add("form-field");

            companyAddressField = new TextField();
            companyAddressField.setPromptText("Company Address");
            companyAddressField.getStyleClass().add("form-field");

            // Add company-specific fields
            formFields.getChildren().addAll(
                    createLabel("Company Name *"), companyNameField,
                    createLabel("Registration Number"), regNumberField,
                    createLabel("Contact Name *"), contactNameField,
                    createLabel("Company Address"), companyAddressField
            );
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void createCustomer() {
        try {
            String customerType = ((RadioButton) customerTypeGroup.getSelectedToggle()).getText().contains("Individual") ? "individual" : "company";

            // Validate required fields
            if (customerIdField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                    addressField.getText().isEmpty() || phoneField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields (*).");
                return;
            }

            // Validate password
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (password.length() < 6) {
                showAlert(Alert.AlertType.ERROR, "Error", "Password must be at least 6 characters long.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match. Please confirm your password.");
                return;
            }

            // Validate simple customer ID format
            String customerId = customerIdField.getText().trim().toUpperCase();
            if (!customerId.matches("^C\\d{3}$")) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Customer ID must be in format: C001, C002, etc.\nExample: C001");
                return;
            }

            // Check if customer ID already exists
            if (customerDAO.customerExists(customerId)) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Customer ID already exists. Please use a different ID.");
                return;
            }

            // Validate type-specific required fields
            if (customerType.equals("individual")) {
                if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "First name and last name are required for individual customers.");
                    return;
                }
            } else {
                if (companyNameField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Company name is required for company customers.");
                    return;
                }
                if (contactNameField.getText().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Contact name is required for company customers.");
                    return;
                }
            }

            // Create customer using direct DAO
            boolean success = createCustomerInDatabase(customerType, customerId);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Customer created successfully!\n\n" +
                                "Customer ID: " + customerId + "\n" +
                                "Password: " + password + "\n\n" +
                                "The customer can now login with this Customer ID and Password.");

                // Clear form for next registration
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer in database.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean createCustomerInDatabase(String customerType, String customerId) {
        try {
            Customer customer;
            String password = passwordField.getText();

            if (customerType.equals("individual")) {
                customer = new IndividualCustomer(
                        customerId,                    // customerId
                        password,                     // password
                        firstNameField.getText(),     // firstName
                        lastNameField.getText(),      // lastName
                        addressField.getText(),       // address
                        phoneField.getText(),         // phoneNumber
                        emailField.getText()          // email
                );
            } else {
                customer = new CompanyCustomer(
                        customerId,                    // customerId
                        password,                     // password
                        companyNameField.getText(),   // companyName
                        addressField.getText(),       // address
                        phoneField.getText(),         // phoneNumber
                        regNumberField.getText(),     // registrationNumber
                        contactNameField.getText(),   // contactName
                        companyAddressField.getText() // companyAddress
                );
            }

            // Save to database
            boolean success = customerDAO.createCustomer(customer);

            if (success) {
                System.out.println("Customer created successfully: " + customerId);
                return true;
            }

        } catch (Exception e) {
            System.out.println("Error creating customer in database: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void clearForm() {
        // Clear all form fields
        customerIdField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        addressField.clear();
        phoneField.clear();
        emailField.clear();

        if (firstNameField != null) firstNameField.clear();
        if (lastNameField != null) lastNameField.clear();
        if (dobField != null) dobField.clear();
        if (govIdField != null) govIdField.clear();
        if (incomeField != null) incomeField.clear();
        if (companyNameField != null) companyNameField.clear();
        if (regNumberField != null) regNumberField.clear();
        if (contactNameField != null) contactNameField.clear();
        if (companyAddressField != null) companyAddressField.clear();
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