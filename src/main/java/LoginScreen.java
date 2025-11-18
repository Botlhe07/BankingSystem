import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private CustomerDAO customerDAO;
    private BankEmployeeDAO employeeDAO;

    public LoginScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.customerDAO = new CustomerDAO();
        this.employeeDAO = new BankEmployeeDAO();

        // ADD DEBUG HERE - at the start of constructor
        checkDatabaseState();

        createUI();
    }

    // FIXED: Debug method - uses customer_id instead of username
    private void checkDatabaseState() {
        System.out.println("=== DATABASE STATE CHECK ===");
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Check customers
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers");
            rs.next();
            System.out.println("Customers in database: " + rs.getInt("count"));

            // Check employees
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM employees");
            rs.next();
            System.out.println("Employees in database: " + rs.getInt("count"));

            // FIXED: Use customer_id instead of username
            rs = stmt.executeQuery("SELECT customer_id FROM customers");
            System.out.println("Customer IDs:");
            boolean hasCustomers = false;
            while (rs.next()) {
                hasCustomers = true;
                System.out.println("  - " + rs.getString("customer_id"));
            }
            if (!hasCustomers) {
                System.out.println("  (none)");
            }

            // List employee IDs if any exist
            rs = stmt.executeQuery("SELECT employee_id FROM employees");
            System.out.println("Employee IDs:");
            boolean hasEmployees = false;
            while (rs.next()) {
                hasEmployees = true;
                System.out.println("  - " + rs.getString("employee_id"));
            }
            if (!hasEmployees) {
                System.out.println("  (none)");
            }

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("Debug error: " + e.getMessage());
        }
        System.out.println("=== END CHECK ===");
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
        usernameField.setPromptText("Customer ID / Employee ID");
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

        Button backButton = new Button("← Back to Main Menu");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setPrefHeight(45);
        backButton.setMaxWidth(Double.MAX_VALUE);

        // Form layout
        VBox formFields = new VBox(15);
        formFields.getChildren().addAll(
                formTitle, radioBox, createLabel("Customer ID / Employee ID"), usernameField,
                createLabel("Password"), passwordField, loginButton, registerButton, backButton
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

        registerButton.setOnAction(e -> navigationController.showEmployeeRegistration());
        backButton.setOnAction(e -> navigationController.showMainMenu());

        // Wrap in ScrollPane for scrolling
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");

        scene = new Scene(scrollPane, 900, 700);
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
                // Use BankingSystem's customerLogin method which handles setting current customer
                if (bankingSystem.customerLogin(username, password)) {
                    Customer currentCustomer = bankingSystem.getCurrentCustomer();
                    showAlert(Alert.AlertType.INFORMATION, "Login Successful",
                            "Welcome back, " + currentCustomer.getDisplayName() + "!");
                    navigationController.showCustomerDashboard();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid customer credentials.");
                }
            } else {
                // Use BankingSystem's employeeLogin method which handles setting current employee
                if (bankingSystem.employeeLogin(username, password)) {
                    BankEmployee currentEmployee = bankingSystem.getCurrentEmployee();
                    showAlert(Alert.AlertType.INFORMATION, "Login Successful",
                            "Welcome, " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + "!");
                    navigationController.showEmployeeDashboard();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid employee credentials.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "System error during login: " + e.getMessage());
            e.printStackTrace();
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