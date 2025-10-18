// LoginScreen.java
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public LoginScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.getStyleClass().add("root");

        // Header
        Label headerLabel = new Label("Banking System");
        headerLabel.getStyleClass().add("header-title");
        headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));

        Label subHeaderLabel = new Label("Secure Banking Platform");
        subHeaderLabel.getStyleClass().add("header-subtitle");

        VBox headerBox = new VBox(10, headerLabel, subHeaderLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getStyleClass().add("header");
        headerBox.setPadding(new Insets(30));

        // Login Form
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setMaxWidth(400);
        formContainer.getStyleClass().add("form-container");

        Label formTitle = new Label("Login to Your Account");
        formTitle.getStyleClass().add("form-title");

        // Login Type Selection
        ToggleGroup loginTypeGroup = new ToggleGroup();
        RadioButton customerRadio = new RadioButton("Customer Login");
        RadioButton employeeRadio = new RadioButton("Employee Login");
        customerRadio.setToggleGroup(loginTypeGroup);
        employeeRadio.setToggleGroup(loginTypeGroup);
        customerRadio.setSelected(true);

        HBox radioBox = new HBox(20, customerRadio, employeeRadio);
        radioBox.setAlignment(Pos.CENTER);

        // Form fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username / Employee ID");
        usernameField.getStyleClass().add("form-field");
        usernameField.setPrefHeight(40);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("form-field");
        passwordField.setPrefHeight(40);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().addAll("btn", "btn-primary");
        loginButton.setPrefHeight(45);
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Button registerButton = new Button("Register New Employee");
        registerButton.getStyleClass().addAll("btn", "btn-outline");
        registerButton.setPrefHeight(45);
        registerButton.setMaxWidth(Double.MAX_VALUE);

        // Form layout
        VBox formFields = new VBox(15);
        formFields.getChildren().addAll(
                formTitle, radioBox, createLabel("Username / Employee ID"), usernameField,
                createLabel("Password"), passwordField, loginButton, registerButton
        );

        formContainer.getChildren().add(formFields);

        // Add everything to main container
        mainContainer.getChildren().addAll(headerBox, formContainer);

        // Event handlers
        loginButton.setOnAction(e -> handleLogin(
                customerRadio.isSelected() ? "customer" : "employee",
                usernameField.getText(),
                passwordField.getText()
        ));

        registerButton.setOnAction(e -> navigationController.showMainMenu());

        scene = new Scene(mainContainer, 900, 700);
        scene.getStylesheets().add("banking-styles.css");
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void handleLogin(String userType, String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both username and password.");
            return;
        }

        try {
            if (userType.equals("customer")) {
                // Handle customer login
                navigationController.showCustomerDashboard();
            } else {
                // Handle employee login
                navigationController.showEmployeeDashboard();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid credentials or system error.");
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
