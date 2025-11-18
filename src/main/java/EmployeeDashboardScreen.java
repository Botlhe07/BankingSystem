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
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public EmployeeDashboardScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        createUI();
    }

    private void createUI() {
        // Main container
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader();

        // Main content area
        VBox content = createContent();

        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        // Wrap in ScrollPane for scrolling
        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");

        scene = new Scene(scrollPane, 1000, 700);
    }

    private HBox createHeader() {
        Label titleLabel = new Label("Employee Dashboard");
        titleLabel.getStyleClass().add("header-title");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));

        Label welcomeLabel = new Label();
        if (bankingSystem.getCurrentEmployee() != null) {
            welcomeLabel.setText("Welcome, " + bankingSystem.getCurrentEmployee().getFirstName() + " " +
                    bankingSystem.getCurrentEmployee().getLastName() + "!");
        } else {
            welcomeLabel.setText("Welcome, Employee!");
        }
        welcomeLabel.getStyleClass().add("header-subtitle");

        VBox titleBox = new VBox(5, titleLabel, welcomeLabel);

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
        logoutButton.setAlignment(Pos.CENTER_RIGHT);

        return header;
    }

    private VBox createContent() {
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.TOP_CENTER);

        // Quick Stats Section
        VBox statsSection = createStatsSection();

        // Actions Section
        VBox actionsSection = createActionsSection();

        content.getChildren().addAll(statsSection, actionsSection);
        return content;
    }

    private VBox createStatsSection() {
        VBox statsSection = new VBox(15);
        statsSection.setAlignment(Pos.CENTER);
        statsSection.setMaxWidth(800);

        Label statsTitle = new Label("Quick Overview");
        statsTitle.getStyleClass().add("section-title");

        HBox statsGrid = new HBox(20);
        statsGrid.setAlignment(Pos.CENTER);

        int totalCustomers = customerDAO.getAllCustomers().size();
        int totalAccounts = accountDAO.getAllAccounts().size();
        double totalBalance = calculateTotalBalance();

        VBox totalCustomersBox = createStatBox("Total Customers", String.valueOf(totalCustomers));
        VBox totalAccountsBox = createStatBox("Total Accounts", String.valueOf(totalAccounts));
        VBox totalBalanceBox = createStatBox("Total Balance", "P" + String.format("%.2f", totalBalance));

        statsGrid.getChildren().addAll(totalCustomersBox, totalAccountsBox, totalBalanceBox);
        statsSection.getChildren().addAll(statsTitle, statsGrid);

        return statsSection;
    }

    private double calculateTotalBalance() {
        double total = 0.0;
        try {
            List<Account> accounts = accountDAO.getAllAccounts();
            for (Account account : accounts) {
                total += account.getBalance();
            }
        } catch (Exception e) {
            System.out.println("Error calculating total balance: " + e.getMessage());
        }
        return total;
    }

    private VBox createStatBox(String title, String value) {
        VBox statBox = new VBox(10);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(20));
        statBox.getStyleClass().add("stat-box");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        statBox.getChildren().addAll(valueLabel, titleLabel);
        return statBox;
    }

    private VBox createActionsSection() {
        VBox actionsSection = new VBox(20);
        actionsSection.setAlignment(Pos.CENTER);
        actionsSection.setMaxWidth(800);

        Label actionsTitle = new Label("Employee Actions");
        actionsTitle.getStyleClass().add("section-title");

        GridPane actionsGrid = new GridPane();
        actionsGrid.setAlignment(Pos.CENTER);
        actionsGrid.setHgap(20);
        actionsGrid.setVgap(20);
        actionsGrid.setPadding(new Insets(20));

        // FIXED: Removed "Process Transactions" button and reorganized layout
        Button registerCustomerBtn = createActionButton("Register Customer", "Add new customers to the system");
        Button createAccountBtn = createActionButton("Create Account", "Open new accounts for customers");
        Button customerListBtn = createActionButton("Customer List", "View all customers");
        Button accountManagementBtn = createActionButton("Account Management", "Manage customer accounts");
        Button statementBtn = createActionButton("Generate Statement", "Create account statements");
        Button registerEmployeeBtn = createActionButton("Register Employee", "Add new employees");
        Button processInterestBtn = createActionButton("Process Interest", "Calculate monthly interest");

        // FIXED: Better grid layout without the transactions button
        actionsGrid.add(registerCustomerBtn, 0, 0);
        actionsGrid.add(createAccountBtn, 1, 0);
        actionsGrid.add(customerListBtn, 2, 0);
        actionsGrid.add(accountManagementBtn, 0, 1);
        actionsGrid.add(statementBtn, 1, 1);
        actionsGrid.add(registerEmployeeBtn, 2, 1);
        actionsGrid.add(processInterestBtn, 1, 2);

        // FIXED: Set actions - removed transaction button action
        registerCustomerBtn.setOnAction(e -> navigationController.showCustomerRegistration());
        createAccountBtn.setOnAction(e -> navigationController.showAccountCreation());
        customerListBtn.setOnAction(e -> navigationController.showCustomerList());
        accountManagementBtn.setOnAction(e -> navigationController.showAccountManagement());
        statementBtn.setOnAction(e -> navigationController.showStatementScreen());
        registerEmployeeBtn.setOnAction(e -> navigationController.showEmployeeRegistration());
        processInterestBtn.setOnAction(e -> processMonthlyInterest());

        actionsSection.getChildren().addAll(actionsTitle, actionsGrid);
        return actionsSection;
    }

    private Button createActionButton(String title, String description) {
        VBox buttonContent = new VBox(5);
        buttonContent.setAlignment(Pos.CENTER);
        buttonContent.setPadding(new Insets(15));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("action-title");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("action-description");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(150);

        buttonContent.getChildren().addAll(titleLabel, descLabel);

        Button button = new Button();
        button.setGraphic(buttonContent);
        button.getStyleClass().addAll("btn", "btn-action");
        button.setPrefSize(180, 80);

        return button;
    }

    private void processMonthlyInterest() {
        try {
            int processedCount = 0;
            List<Customer> customers = customerDAO.getAllCustomers();
            for (Customer customer : customers) {
                List<Account> accounts = accountDAO.getAccountsByCustomer(customer.getCustomerId());
                for (Account account : accounts) {
                    if (account instanceof SavingsAccount || account instanceof InvestmentAccount) {
                        double interest = calculateInterestForAccount(account);
                        if (interest > 0) {
                            double newBalance = account.getBalance() + interest;
                            if (accountDAO.updateAccountBalance(account.getAccountNumber(), newBalance)) {
                                Transaction interestTransaction = new Transaction(
                                        account.getAccountNumber(), "INTEREST", interest, newBalance, "Monthly interest payment"
                                );
                                if (transactionDAO.recordTransaction(interestTransaction)) {
                                    processedCount++;
                                }
                            }
                        }
                    }
                }
            }
            showAlert("Interest Processed", "Processed interest for " + processedCount + " accounts!");
        } catch (Exception e) {
            showAlert("Error", "Failed to process interest: " + e.getMessage());
        }
    }

    private double calculateInterestForAccount(Account account) {
        if (account instanceof SavingsAccount) {
            return account.getBalance() * 0.0005;
        } else if (account instanceof InvestmentAccount) {
            return account.getBalance() * 0.05;
        }
        return 0.0;
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
}