import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.HashMap;
import java.util.Map;

public class CustomerRegistrationScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private ToggleGroup customerTypeGroup;
    private VBox formFields;

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

    private void createCustomer() {
        // Get all form data
        Map<String, String> formData = collectFormData();

        if (formData.get("username").isEmpty() || formData.get("password").isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        }

        String customerType = ((RadioButton) customerTypeGroup.getSelectedToggle()).getText().contains("Individual") ? "individual" : "company";

        try {
            bankingSystem.createNewCustomerAndSave(customerType, formData.get("username"),
                    formData.get("password"), formData.get("address"), formData.get("phoneNumber"), formData);

            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Customer created successfully!\nUsername: " + formData.get("username") + "\nPassword: " + formData.get("password"));
            navigationController.showEmployeeDashboard();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create customer: " + e.getMessage());
        }
    }

    private Map<String, String> collectFormData() {
        Map<String, String> data = new HashMap<>();

        for (javafx.scene.Node node : formFields.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                String fieldName = label.getText().replace(":", "").toLowerCase().replace(" ", "");
                int nodeIndex = formFields.getChildren().indexOf(node);
                if (nodeIndex + 1 < formFields.getChildren().size()) {
                    javafx.scene.Node nextNode = formFields.getChildren().get(nodeIndex + 1);
                    if (nextNode instanceof TextField) {
                        data.put(fieldName, ((TextField) nextNode).getText());
                    } else if (nextNode instanceof PasswordField) {
                        data.put(fieldName, ((PasswordField) nextNode).getText());
                    }
                }
            }
        }

        return data;
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