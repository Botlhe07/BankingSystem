// CustomerDashboardScreen.java
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CustomerDashboardScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public CustomerDashboardScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
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
        StackPane content = new StackPane();
        content.setPadding(new Insets(20));

        // Welcome card
        VBox welcomeCard = createWelcomeCard();
        content.getChildren().add(welcomeCard);

        mainLayout.setTop(header);
        mainLayout.setLeft(sidebar);
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

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().addAll("btn", "btn-outline");
        logoutButton.setOnAction(e -> navigationController.showLoginScreen());

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

    private VBox createWelcomeCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setMaxWidth(600);
        card.setPadding(new Insets(30));

        Label welcomeTitle = new Label("Welcome to Your Dashboard");
        welcomeTitle.getStyleClass().add("card-title");

        Label welcomeText = new Label("Use the menu on the left to navigate through your banking options. " +
                "You can view your accounts, make transactions, and check your transaction history.");
        welcomeText.setWrapText(true);
        welcomeText.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");

        // Quick stats
        HBox statsBox = new HBox(20);
        statsBox.getChildren().addAll(
                createStatCard("Total Accounts", "3"),
                createStatCard("Total Balance", "$12,450.75"),
                createStatCard("Recent Transactions", "15")
        );

        card.getChildren().addAll(welcomeTitle, welcomeText, statsBox);
        return card;
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
            case "View Accounts":
                navigationController.showAccountManagement();
                break;
            case "Deposit Funds":
                navigationController.showTransactionScreen("Deposit");
                break;
            case "Withdraw Funds":
                navigationController.showTransactionScreen("Withdraw");
                break;
            case "Transaction History":
                // Show transaction history
                break;
            case "Account Statement":
                // Show account statement
                break;
        }
    }

    public Scene getScene() {
        return scene;
    }
}
