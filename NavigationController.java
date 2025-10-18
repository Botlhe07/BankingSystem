// NavigationController.java
import javafx.stage.Stage;

public class NavigationController {
    private Stage primaryStage;
    private BankingSystem bankingSystem;

    public NavigationController(Stage primaryStage, BankingSystem bankingSystem) {
        this.primaryStage = primaryStage;
        this.bankingSystem = bankingSystem;
    }

    public void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this, bankingSystem);
        primaryStage.setScene(loginScreen.getScene());
        primaryStage.setTitle("Banking System - Login");
    }

    public void showMainMenu() {
        MainMenuScreen mainMenu = new MainMenuScreen(this, bankingSystem);
        primaryStage.setScene(mainMenu.getScene());
        primaryStage.setTitle("Banking System - Main Menu");
    }

    public void showCustomerDashboard() {
        CustomerDashboardScreen customerDashboard = new CustomerDashboardScreen(this, bankingSystem);
        primaryStage.setScene(customerDashboard.getScene());
        primaryStage.setTitle("Banking System - Customer Dashboard");
    }

    public void showEmployeeDashboard() {
        EmployeeDashboardScreen employeeDashboard = new EmployeeDashboardScreen(this, bankingSystem);
        primaryStage.setScene(employeeDashboard.getScene());
        primaryStage.setTitle("Banking System - Employee Dashboard");
    }

    public void showCustomerRegistration() {
        CustomerRegistrationScreen registrationScreen = new CustomerRegistrationScreen(this, bankingSystem);
        primaryStage.setScene(registrationScreen.getScene());
        primaryStage.setTitle("Banking System - Register Customer");
    }

    public void showAccountCreation() {
        AccountCreationScreen accountScreen = new AccountCreationScreen(this, bankingSystem);
        primaryStage.setScene(accountScreen.getScene());
        primaryStage.setTitle("Banking System - Create Account");
    }

    public void showTransactionScreen(String type) {
        TransactionScreen transactionScreen = new TransactionScreen(this, bankingSystem, type);
        primaryStage.setScene(transactionScreen.getScene());
        primaryStage.setTitle("Banking System - " + type);
    }

    public void showAccountManagement() {
        AccountManagementScreen accountManagement = new AccountManagementScreen(this, bankingSystem);
        primaryStage.setScene(accountManagement.getScene());
        primaryStage.setTitle("Banking System - Account Management");
    }
}