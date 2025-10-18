// CustomerRegistrationScreen.java
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

    public CustomerRegistrationScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Register New Customer", "Create customer accounts");

        // Back button
        Button backButton = new Button("← Back");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> navigationController.showEmployeeDashboard());

        // Form content
        VBox formContent = createRegistrationForm();

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

    private VBox createRegistrationForm() {
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(600);
        formContainer.getStyleClass().add("form-container");

        Label formTitle = new Label("Customer Registration");
        formTitle.getStyleClass().add("form-title");

        // Customer type selection
        VBox typeSelection = createCustomerTypeSelection();

        // Form fields will be dynamically updated based on customer type
        VBox formFields = new VBox(15);
        formFields.setId("formFields");

        // Initial form setup for individual customer
        updateFormFields(formFields, "individual");

        Button submitButton = new Button("Create Customer");
        submitButton.getStyleClass().addAll("btn", "btn-success");
        submitButton.setPrefHeight(45);
        submitButton.setMaxWidth(Double.MAX_VALUE);

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

        VBox formFields = new VBox();
        individualRadio.setOnAction(e -> updateFormFields(formFields, "individual"));
        companyRadio.setOnAction(e -> updateFormFields(formFields, "company"));

        HBox radioBox = new HBox(20, individualRadio, companyRadio);
        typeBox.getChildren().addAll(typeLabel, radioBox);

        return typeBox;
    }

    private void updateFormFields(VBox formFields, String customerType) {
        formFields.getChildren().clear();

        // Common fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("form-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("form-field");

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.getStyleClass().add("form-field");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.getStyleClass().add("form-field");

        // Add common fields first
        formFields.getChildren().addAll(
                createLabel("Username"), usernameField,
                createLabel("Password"), passwordField,
                createLabel("Address"), addressField,
                createLabel("Phone Number"), phoneField
        );

        if (customerType.equals("individual")) {
            // Individual-specific fields
            TextField firstNameField = new TextField();
            firstNameField.setPromptText("First Name");
            firstNameField.getStyleClass().add("form-field");

            TextField lastNameField = new TextField();
            lastNameField.setPromptText("Last Name");
            lastNameField.getStyleClass().add("form-field");

            TextField dobField = new TextField();
            dobField.setPromptText("Date of Birth (YYYY-MM-DD)");
            dobField.getStyleClass().add("form-field");

            TextField govIdField = new TextField();
            govIdField.setPromptText("Government ID");
            govIdField.getStyleClass().add("form-field");

            TextField incomeField = new TextField();
            incomeField.setPromptText("Source of Income");
            incomeField.getStyleClass().add("form-field");

            // Add individual-specific fields
            formFields.getChildren().addAll(
                    createLabel("First Name"), firstNameField,
                    createLabel("Last Name"), lastNameField,
                    createLabel("Date of Birth"), dobField,
                    createLabel("Government ID"), govIdField,
                    createLabel("Source of Income"), incomeField
            );
        } else {
            // Company-specific fields
            TextField companyNameField = new TextField();
            companyNameField.setPromptText("Company Name");
            companyNameField.getStyleClass().add("form-field");

            TextField regNumberField = new TextField();
            regNumberField.setPromptText("Registration Number");
            regNumberField.getStyleClass().add("form-field");

            TextField contactNameField = new TextField();
            contactNameField.setPromptText("Contact Name");
            contactNameField.getStyleClass().add("form-field");

            TextField companyAddressField = new TextField();
            companyAddressField.setPromptText("Company Address");
            companyAddressField.getStyleClass().add("form-field");

            // Add company-specific fields
            formFields.getChildren().addAll(
                    createLabel("Company Name"), companyNameField,
                    createLabel("Registration Number"), regNumberField,
                    createLabel("Contact Name"), contactNameField,
                    createLabel("Company Address"), companyAddressField
            );
        }
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    public Scene getScene() {
        return scene;
    }
}