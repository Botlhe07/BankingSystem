import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.Connection; // ← ADD THIS IMPORT
import java.util.List;      // ← ADD THIS IMPORT TOO


public class EmployeeRegistrationScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private BankEmployeeDAO employeeDAO;


    private void createUI() {
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setAlignment(Pos.TOP_CENTER);

        // Title
        Label titleLabel = new Label("Register New Employee");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Form fields
        TextField employeeIdField = new TextField();
        employeeIdField.setPromptText("Employee ID");
        employeeIdField.getStyleClass().add("form-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("form-field");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.getStyleClass().add("form-field");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        firstNameField.getStyleClass().add("form-field");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        lastNameField.getStyleClass().add("form-field");

        TextField positionField = new TextField();
        positionField.setPromptText("Position");
        positionField.getStyleClass().add("form-field");

        TextField departmentField = new TextField();
        departmentField.setPromptText("Department");
        departmentField.getStyleClass().add("form-field");

        // Buttons
        Button registerButton = new Button("Register Employee");
        registerButton.setStyle("-fx-background-color: #2e8b57; -fx-text-fill: white;");
        registerButton.setOnAction(e -> registerEmployee(
                employeeIdField.getText(),
                passwordField.getText(),
                confirmPasswordField.getText(),
                firstNameField.getText(),
                lastNameField.getText(),
                positionField.getText(),
                departmentField.getText()
        ));

        Button backButton = new Button("Back to Login");
        backButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        backButton.setOnAction(e -> navigationController.showLoginScreen());

        HBox buttonBox = new HBox(15, registerButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add components to main container
        mainContainer.getChildren().addAll(
                titleLabel,
                createLabel("Employee ID"), employeeIdField,
                createLabel("Password"), passwordField,
                createLabel("Confirm Password"), confirmPasswordField,
                createLabel("First Name"), firstNameField,
                createLabel("Last Name"), lastNameField,
                createLabel("Position"), positionField,
                createLabel("Department"), departmentField,
                buttonBox
        );

        // Wrap the main container in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPadding(new Insets(10));

        // Set preferred viewport size
        scrollPane.setPrefViewportWidth(600);
        scrollPane.setPrefViewportHeight(600);

        // Ensure the main container can expand
        mainContainer.setMinHeight(Region.USE_PREF_SIZE);
        mainContainer.setMinWidth(Region.USE_PREF_SIZE);

        scene = new Scene(scrollPane, 600, 600);
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void registerEmployee(String employeeId, String password, String confirmPassword,
                                  String firstName, String lastName, String position, String department) {
        // Validation
        if (employeeId.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                position.isEmpty() || department.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long.");
            return;
        }

        // Create and save employee
        BankEmployee employee = new BankEmployee(employeeId, password, firstName, lastName, position, department);

        if (employeeDAO.createEmployee(employee)) {
            showAlert("Success", "Employee registered successfully!");
            navigationController.showLoginScreen();
        } else {
            showAlert("Error", "Failed to register employee. Employee ID might already exist.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
    public EmployeeRegistrationScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.employeeDAO = new BankEmployeeDAO();

        // ADD THIS DEBUG:
        debugDatabaseContents();
        createUI();
    }

    // ADD THIS METHOD:
    private void debugDatabaseContents() {
        try {
            System.out.println("=== DATABASE DEBUG ===");

            // Check current database URL
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Database URL: " + conn.getMetaData().getURL());

            // Check employees table
            List<BankEmployee> employees = employeeDAO.getAllEmployees();
            System.out.println("Employees count: " + employees.size());

            for (BankEmployee emp : employees) {
                System.out.println("FOUND EMPLOYEE: " + emp.getEmployeeId());
            }

            conn.close();
            System.out.println("=== END DEBUG ===");

        } catch (Exception e) {
            System.out.println("Debug error: " + e.getMessage());
        }
    }

}