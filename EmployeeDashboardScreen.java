import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class EmployeeDashboardScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public EmployeeDashboardScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Employee Dashboard", "Bank Management System");

        // Sidebar
        VBox sidebar = createEmployeeSidebar();

        // Content area
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        // Dashboard cards
        GridPane dashboardGrid = new GridPane();
        dashboardGrid.setHgap(20);
        dashboardGrid.setVgap(20);
        dashboardGrid.setPadding(new Insets(20));

        // Get actual statistics
        int totalCustomers = bankingSystem.getAllCustomers().size();
        int totalAccounts = bankingSystem.getAllAccounts().size();
        double totalBalance = calculateTotalBalance();

        // Add dashboard cards with real data
        dashboardGrid.add(createDashboardCard("Total Customers", String.valueOf(totalCustomers), "primary-color"), 0, 0);
        dashboardGrid.add(createDashboardCard("Total Accounts", String.valueOf(totalAccounts), "success-color"), 1, 0);
        dashboardGrid.add(createDashboardCard("Total Balance", "P" + String.format("%.2f", totalBalance), "warning-color"), 2, 0);

        content.getChildren().addAll(
                createSectionTitle("Bank Overview"),
                dashboardGrid,
                createSectionTitle("Quick Actions"),
                createQuickActions()
        );

        mainLayout.setTop(header);
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 1200, 800);
        scene.getStylesheets().add("banking-styles.css");
    }

    private double calculateTotalBalance() {
        double total = 0.0;
        for (Account account : bankingSystem.getAllAccounts()) {
            total += account.getBalance();
        }
        return total;
    }

    private HBox createHeader(String title, String subtitle) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("header-title");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("header-subtitle");

        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().addAll("btn", "btn-outline");
        logoutButton.setOnAction(e -> {
            bankingSystem.logout();
            navigationController.showLoginScreen();
        });

        HBox header = new HBox(20, titleBox, logoutButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(15, 30, 15, 30));
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        return header;
    }

    private VBox createEmployeeSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("menu-sidebar");
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20, 10, 20, 10));

        String[] menuItems = {
                "Dashboard", "Create Customer", "Open Account",
                "View Customers", "View Accounts", "Transaction History",
                "Process Interest", "Generate Statements", "Register Employee"
        };

        for (String item : menuItems) {
            Button menuButton = new Button(item);
            menuButton.getStyleClass().add("menu-item");
            menuButton.setMaxWidth(Double.MAX_VALUE);
            menuButton.setOnAction(e -> handleMenuAction(item));
            sidebar.getChildren().add(menuButton);
        }

        return sidebar;
    }

    private VBox createDashboardCard(String title, String value, String colorClass) {
        VBox card = new VBox(15);
        card.getStyleClass().addAll("card", colorClass);
        card.setPadding(new Insets(20));
        card.setPrefSize(200, 120);
        card.setAlignment(Pos.CENTER);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }

    private Label createSectionTitle(String text) {
        Label title = new Label(text);
        title.getStyleClass().add("card-title");
        return title;
    }

    private HBox createQuickActions() {
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER_LEFT);

        String[] actions = {"New Customer", "Open Account", "View Reports", "Process Interest"};

        for (String action : actions) {
            Button actionBtn = new Button(action);
            actionBtn.getStyleClass().addAll("btn", "btn-primary");
            actionBtn.setOnAction(e -> handleQuickAction(action));
            actionsBox.getChildren().add(actionBtn);
        }

        return actionsBox;
    }

    private void handleMenuAction(String menuItem) {
        switch (menuItem) {
            case "Dashboard":
                // Already on dashboard, do nothing
                break;
            case "Create Customer":
                navigationController.showCustomerRegistration();
                break;
            case "Open Account":
                navigationController.showAccountCreation();
                break;
            case "View Customers":
                navigationController.showCustomerList();
                break;
            case "View Accounts":
                showAllAccounts();
                break;
            case "Transaction History":
                showTransactionHistory();
                break;
            case "Process Interest":
                processMonthlyInterest();
                break;
            case "Generate Statements":
                navigationController.showStatementScreen();
                break;
            case "Register Employee":
                showEmployeeRegistration();
                break;
        }
    }

    private void handleQuickAction(String action) {
        switch (action) {
            case "New Customer":
                navigationController.showCustomerRegistration();
                break;
            case "Open Account":
                navigationController.showAccountCreation();
                break;
            case "View Reports":
                showReports();
                break;
            case "Process Interest":
                processMonthlyInterest();
                break;
        }
    }

    private void showAllAccounts() {
        List<Account> accounts = bankingSystem.getAllAccounts();
        StringBuilder message = new StringBuilder("All Bank Accounts:\n\n");

        if (accounts.isEmpty()) {
            message.append("No accounts found.");
        } else {
            for (Account account : accounts) {
                message.append("Account: ").append(account.getAccountNumber())
                        .append(" | Type: ").append(account.getAccountType())
                        .append(" | Balance: ").append(account.getFormattedBalance())
                        .append("\n");
            }
        }

        showAlert(Alert.AlertType.INFORMATION, "All Accounts", message.toString());
    }

    private void showTransactionHistory() {
        List<Account> accounts = bankingSystem.getAllAccounts();
        StringBuilder message = new StringBuilder("Transaction History:\n\n");

        if (accounts.isEmpty()) {
            message.append("No accounts found.");
        } else {
            boolean hasTransactions = false;
            for (Account account : accounts) {
                List<Transaction> transactions = account.getTransactionHistory();
                if (!transactions.isEmpty()) {
                    hasTransactions = true;
                    message.append("Account: ").append(account.getAccountNumber()).append("\n");
                    for (Transaction transaction : transactions) {
                        message.append("  ").append(transaction.toString()).append("\n");
                    }
                    message.append("\n");
                }
            }
            if (!hasTransactions) {
                message.append("No transactions found.");
            }
        }

        TextArea textArea = new TextArea(message.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction History");
        alert.setHeaderText("All Transactions");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void processMonthlyInterest() {
        bankingSystem.processMonthlyInterest();
        showAlert(Alert.AlertType.INFORMATION, "Interest Processed",
                "Monthly interest has been processed for all eligible accounts.");
    }

    private void showReports() {
        List<Customer> customers = bankingSystem.getAllCustomers();
        List<Account> accounts = bankingSystem.getAllAccounts();

        StringBuilder report = new StringBuilder();
        report.append("=== BANKING SYSTEM REPORT ===\n\n");
        report.append("Total Customers: ").append(customers.size()).append("\n");
        report.append("Total Accounts: ").append(accounts.size()).append("\n");
        report.append("Total Balance: P").append(String.format("%.2f", calculateTotalBalance())).append("\n\n");

        report.append("=== CUSTOMER DETAILS ===\n");
        for (Customer customer : customers) {
            report.append("• ").append(customer.getDisplayName())
                    .append(" (").append(customer.getCustomerType()).append(")\n");
            report.append("  Username: ").append(customer.getUsername()).append("\n");
            report.append("  Accounts: ").append(customer.getAccounts().size()).append("\n\n");
        }

        TextArea textArea = new TextArea(report.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bank Reports");
        alert.setHeaderText("Comprehensive System Report");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void showEmployeeRegistration() {
        EmployeeRegistrationScreen registrationScreen = new EmployeeRegistrationScreen(navigationController, bankingSystem);
        navigationController.showEmployeeRegistrationScreen(registrationScreen.getScene());
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