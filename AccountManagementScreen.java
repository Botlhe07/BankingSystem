// AccountManagementScreen.java
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AccountManagementScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public AccountManagementScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Account Management", "View and manage your accounts");

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> navigationController.showCustomerDashboard());

        // Content
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        // Accounts table
        Label tableTitle = new Label("Your Accounts");
        tableTitle.getStyleClass().add("card-title");

        TableView<Account> accountsTable = createAccountsTable();

        // Action buttons
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.getStyleClass().addAll("btn", "btn-primary");

        Button depositButton = new Button("Deposit");
        depositButton.getStyleClass().addAll("btn", "btn-success");
        depositButton.setOnAction(e -> navigationController.showTransactionScreen("Deposit"));

        Button withdrawButton = new Button("Withdraw");
        withdrawButton.getStyleClass().addAll("btn", "btn-warning");
        withdrawButton.setOnAction(e -> navigationController.showTransactionScreen("Withdraw"));

        Button transactionHistoryButton = new Button("Transaction History");
        transactionHistoryButton.getStyleClass().addAll("btn", "btn-secondary");

        actionButtons.getChildren().addAll(viewDetailsButton, depositButton, withdrawButton, transactionHistoryButton);

        content.getChildren().addAll(backButton, tableTitle, accountsTable, actionButtons);
        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 1200, 800);
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

    private TableView<Account> createAccountsTable() {
        TableView<Account> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Sample data - you would replace this with real data from your BankingSystem
        /*
        table.getItems().addAll(
            new Account("ACC1001", "Savings", 1500.00),
            new Account("ACC1002", "Cheque", 2500.00),
            new Account("ACC1003", "Investment", 5000.00)
        );
        */

        // Create columns
        TableColumn<Account, String> accountNumberCol = new TableColumn<>("Account Number");
        accountNumberCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));

        TableColumn<Account, String> accountTypeCol = new TableColumn<>("Account Type");
        accountTypeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));

        TableColumn<Account, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setCellFactory(col -> new TableCell<Account, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", amount));
                }
            }
        });

        TableColumn<Account, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(accountNumberCol, accountTypeCol, balanceCol, statusCol);

        return table;
    }

    public Scene getScene() {
        return scene;
    }
}