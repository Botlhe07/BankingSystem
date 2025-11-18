import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class CustomerDashboardScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public CustomerDashboardScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        createUI();
    }

    private void createUI() {
        // Main layout with sidebar and content
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Customer Dashboard", "Welcome back!");

        // Sidebar
        VBox sidebar = createCustomerSidebar();

        // Content area
        ScrollPane content = createContentArea();

        mainLayout.setTop(header);
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 1200, 800);
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

    private VBox createCustomerSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("menu-sidebar");
        sidebar.setPrefWidth(250);
        sidebar.setPadding(new Insets(20, 10, 20, 10));

        String[] menuItems = {
                "Dashboard", "View Accounts", "Deposit Funds",
                "Withdraw Funds", "Transaction History", "Account Statement"
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

    private ScrollPane createContentArea() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        // Welcome card
        VBox welcomeCard = createWelcomeCard();

        // Accounts summary
        VBox accountsSummary = createAccountsSummary();

        content.getChildren().addAll(welcomeCard, accountsSummary);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("scroll-pane");

        return scrollPane;
    }

    private VBox createWelcomeCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setMaxWidth(600);
        card.setPadding(new Insets(30));

        Customer currentCustomer = bankingSystem.getCurrentCustomer();

        Label welcomeTitle = new Label("Welcome to Your Dashboard");
        welcomeTitle.getStyleClass().add("card-title");

        Label welcomeText = new Label("Hello " + currentCustomer.getDisplayName() + "! " +
                "Use the menu on the left to navigate through your banking options. " +
                "You can view your accounts, make transactions, and check your transaction history.");
        welcomeText.setWrapText(true);
        welcomeText.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");

        // Quick stats
        HBox statsBox = new HBox(20);
        List<Account> accounts = getCurrentCustomerAccounts();
        int accountCount = accounts.size();
        double totalBalance = accounts.stream().mapToDouble(Account::getBalance).sum();
        int transactionCount = getTotalTransactionCount(accounts);

        statsBox.getChildren().addAll(
                createStatCard("Total Accounts", String.valueOf(accountCount)),
                createStatCard("Total Balance", "P" + String.format("%.2f", totalBalance)),
                createStatCard("Total Transactions", String.valueOf(transactionCount))
        );

        card.getChildren().addAll(welcomeTitle, welcomeText, statsBox);
        return card;
    }

    private VBox createAccountsSummary() {
        VBox summaryBox = new VBox(15);
        summaryBox.getStyleClass().add("card");
        summaryBox.setPadding(new Insets(20));

        Label summaryTitle = new Label("Your Accounts Summary");
        summaryTitle.getStyleClass().add("card-title");

        List<Account> accounts = getCurrentCustomerAccounts();

        if (accounts.isEmpty()) {
            Label noAccountsLabel = new Label("You don't have any accounts yet.");
            noAccountsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            summaryBox.getChildren().addAll(summaryTitle, noAccountsLabel);
        } else {
            VBox accountsList = new VBox(10);
            for (Account account : accounts) {
                HBox accountItem = createAccountItem(account);
                accountsList.getChildren().add(accountItem);
            }
            summaryBox.getChildren().addAll(summaryTitle, accountsList);
        }

        return summaryBox;
    }

    private HBox createAccountItem(Account account) {
        HBox accountItem = new HBox(15);
        accountItem.setPadding(new Insets(10));
        accountItem.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5px;");

        VBox accountInfo = new VBox(5);

        Label accountNumber = new Label("Account: " + account.getAccountNumber());
        accountNumber.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label accountDetails = new Label(account.getAccountType() + " | " + account.getFormattedBalance());
        accountDetails.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        accountInfo.getChildren().addAll(accountNumber, accountDetails);
        HBox.setHgrow(accountInfo, Priority.ALWAYS);

        Label signatories = new Label("Signatories: " + account.getSignatories().size());
        signatories.setStyle("-fx-text-fill: #3498db; -fx-font-size: 12px;");

        accountItem.getChildren().addAll(accountInfo, signatories);
        return accountItem;
    }

    private VBox createStatCard(String label, String value) {
        VBox statCard = new VBox(10);
        statCard.getStyleClass().add("stat-card");
        statCard.setPadding(new Insets(20));
        statCard.setPrefSize(150, 100);

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("stat-label");

        statCard.getChildren().addAll(valueLabel, nameLabel);
        return statCard;
    }

    private void handleMenuAction(String menuItem) {
        switch (menuItem) {
            case "Dashboard":
                // Already on dashboard, refresh
                navigationController.showCustomerDashboard();
                break;
            case "View Accounts":
                // FIXED: Use customer-specific account view
                showCustomerAccountView();
                break;
            case "Deposit Funds":
                navigationController.showTransactionScreen("Deposit");
                break;
            case "Withdraw Funds":
                navigationController.showTransactionScreen("Withdraw");
                break;
            case "Transaction History":
                showCustomerTransactionHistory();
                break;
            case "Account Statement":
                generateCustomerStatement();
                break;
        }
    }

    // ADD THIS METHOD: Customer-specific account view
    private void showCustomerAccountView() {
        Customer customer = bankingSystem.getCurrentCustomer();
        List<Account> accounts = getCurrentCustomerAccounts();

        StringBuilder accountInfo = new StringBuilder();
        accountInfo.append("=== YOUR ACCOUNTS ===\n\n");

        if (accounts.isEmpty()) {
            accountInfo.append("You don't have any accounts yet.\n");
        } else {
            for (Account account : accounts) {
                accountInfo.append("Account: ").append(account.getAccountNumber()).append("\n");
                accountInfo.append("Type: ").append(account.getAccountType()).append("\n");
                accountInfo.append("Balance: ").append(account.getFormattedBalance()).append("\n");
                accountInfo.append("Branch: ").append(account.getBranch()).append("\n");

                // Show signatories
                List<String> signatories = account.getSignatories();
                accountInfo.append("Signatories: ");
                if (signatories.isEmpty()) {
                    accountInfo.append("None");
                } else {
                    accountInfo.append(String.join(", ", signatories));
                }
                accountInfo.append("\n\n");
            }
        }

        TextArea textArea = new TextArea(accountInfo.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Your Accounts");
        alert.setHeaderText("Account Summary for " + customer.getDisplayName());
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void showCustomerTransactionHistory() {
        List<Account> accounts = getCurrentCustomerAccounts();
        StringBuilder message = new StringBuilder("Your Transaction History:\n\n");

        if (accounts.isEmpty()) {
            message.append("You don't have any accounts yet.");
        } else {
            boolean hasTransactions = false;
            for (Account account : accounts) {
                List<Transaction> transactions = transactionDAO.getTransactionsByAccount(account.getAccountNumber());
                if (!transactions.isEmpty()) {
                    hasTransactions = true;
                    message.append("=== ").append(account.getAccountNumber()).append(" ===\n");
                    for (Transaction transaction : transactions) {
                        message.append(transaction.toString()).append("\n");
                    }
                    message.append("\n");
                }
            }
            if (!hasTransactions) {
                message.append("No transactions found for your accounts.");
            }
        }

        TextArea textArea = new TextArea(message.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Your Transaction History");
        alert.setHeaderText("All Your Transactions");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private void generateCustomerStatement() {
        Customer customer = bankingSystem.getCurrentCustomer();
        List<Account> accounts = getCurrentCustomerAccounts();

        StringBuilder statement = new StringBuilder();
        statement.append("=== ACCOUNT STATEMENT ===\n\n");
        statement.append("Customer: ").append(customer.getDisplayName()).append("\n");
        statement.append("Customer Type: ").append(customer.getCustomerType()).append("\n");
        statement.append("Statement Date: ").append(java.time.LocalDate.now()).append("\n\n");

        if (accounts.isEmpty()) {
            statement.append("No accounts found.\n");
        } else {
            double totalBalance = 0;
            int totalTransactions = 0;

            for (Account account : accounts) {
                statement.append("Account: ").append(account.getAccountNumber()).append("\n");
                statement.append("Type: ").append(account.getAccountType()).append("\n");
                statement.append("Balance: ").append(account.getFormattedBalance()).append("\n");
                statement.append("Branch: ").append(account.getBranch()).append("\n");

                List<Transaction> transactions = transactionDAO.getTransactionsByAccount(account.getAccountNumber());
                totalTransactions += transactions.size();
                totalBalance += account.getBalance();

                if (transactions.isEmpty()) {
                    statement.append("No transactions.\n");
                } else {
                    statement.append("Recent Transactions:\n");
                    for (Transaction transaction : transactions) {
                        statement.append("  ").append(transaction.toString()).append("\n");
                    }
                }
                statement.append("\n");
            }

            statement.append("=== SUMMARY ===\n");
            statement.append("Total Accounts: ").append(accounts.size()).append("\n");
            statement.append("Total Balance: P").append(String.format("%.2f", totalBalance)).append("\n");
            statement.append("Total Transactions: ").append(totalTransactions).append("\n");
        }

        TextArea textArea = new TextArea(statement.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(600, 400);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Account Statement");
        alert.setHeaderText("Your Personal Account Statement");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    private List<Account> getCurrentCustomerAccounts() {
        Customer currentCustomer = bankingSystem.getCurrentCustomer();
        if (currentCustomer != null) {
            return accountDAO.getAccountsByCustomer(currentCustomer.getCustomerId());
        }
        return List.of();
    }

    private int getTotalTransactionCount(List<Account> accounts) {
        int total = 0;
        for (Account account : accounts) {
            List<Transaction> transactions = transactionDAO.getTransactionsByAccount(account.getAccountNumber());
            total += transactions.size();
        }
        return total;
    }

    public Scene getScene() {
        return scene;
    }
}