import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class EmployeeRegistrationScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public EmployeeRegistrationScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Register New Employee", "Create bank employee account");

        // Back button
        Button backButton = new Button("← Back to Main Menu");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> navigationController.showMainMenu());

        // Form content wrapped in ScrollPane
        ScrollPane scrollPane = createScrollableForm();

        VBox content = new VBox(20, backButton, scrollPane);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 800, 600);
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
        formContainer.setMaxWidth(500);
        formContainer.getStyleClass().add("form-container");
        formContainer.setPadding(new Insets(25));

        Label formTitle = new Label("Employee Registration");
        formTitle.getStyleClass().add("form-title");

        // Form fields
        TextField employeeIdField = new TextField();
        employeeIdField.setPromptText("Employee ID");
        employeeIdField.getStyleClass().add("form-field");
        employeeIdField.setPrefHeight(40);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("form-field");
        passwordField.setPrefHeight(40);

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.getStyleClass().add("form-field");
        firstNameField.setPrefHeight(40);

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.getStyleClass().add("form-field");
        lastNameField.setPrefHeight(40);

        Button registerButton = new Button("Register Employee");
        registerButton.getStyleClass().addAll("btn", "btn-success");
        registerButton.setPrefHeight(45);
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setOnAction(e -> registerEmployee(employeeIdField, passwordField, firstNameField, lastNameField));

        // Password requirements label
        Label passwordRequirements = new Label("Password must be at least 8 characters with both letters and numbers");
        passwordRequirements.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-wrap-text: true;");

        formContainer.getChildren().addAll(
                formTitle,
                createLabel("Employee ID"), employeeIdField,
                createLabel("Password"), passwordField,
                passwordRequirements,
                createLabel("First Name"), firstNameField,
                createLabel("Last Name"), lastNameField,
                registerButton
        );

        return formContainer;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void registerEmployee(TextField employeeIdField, PasswordField passwordField,
                                  TextField firstNameField, TextField lastNameField) {
        String employeeId = employeeIdField.getText();
        String password = passwordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        // Validation
        if (employeeId.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        // Check if employee ID already exists
        if (bankingSystem.employeeIdExists(employeeId)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Employee ID already exists. Please choose a different one.");
            return;
        }

        // Password validation
        if (password.length() < 8 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Password must be at least 8 characters with both letters and numbers.");
            return;
        }

        try {
            // Create new employee
            bankingSystem.createNewEmployeeAndSave(employeeId, password, firstName, lastName);

            // Show success message with credentials
            String successMessage = String.format(
                    "Employee registered successfully!\n\n" +
                            "Employee ID: %s\n" +
                            "Password: %s\n" +
                            "Name: %s %s\n\n" +
                            "You can now login with these credentials.",
                    employeeId, password, firstName, lastName
            );

            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", successMessage);

            // Return to main menu
            navigationController.showMainMenu();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to register employee: " + e.getMessage());
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