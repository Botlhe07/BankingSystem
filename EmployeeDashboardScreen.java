// EmployeeDashboardScreen.java
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EmployeeDashboardScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private VBox formFields; // Moved to instance variable

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

        // Add dashboard cards
        dashboardGrid.add(createDashboardCard("Total Customers", "150", "primary-color"), 0, 0);
        dashboardGrid.add(createDashboardCard("Active Accounts", "245", "success-color"), 1, 0);
        dashboardGrid.add(createDashboardCard("Pending Actions", "12", "warning-color"), 2, 0);
        dashboardGrid.add(createDashboardCard("Total Balance", "$1.2M", "secondary-color"), 3, 0);

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
        String[] styles = {"btn-primary", "btn-success", "btn-secondary", "btn-warning"};

        for (int i = 0; i < actions.length; i++) {
            final String action = actions[i]; // Create final variable for lambda
            final String style = styles[i];   // Create final variable for lambda

            Button actionBtn = new Button(action);
            actionBtn.getStyleClass().addAll("btn", style);
            actionBtn.setOnAction(e -> handleQuickAction(action));
            actionsBox.getChildren().add(actionBtn);
        }

        return actionsBox;
    }

    private void handleMenuAction(String menuItem) {
        switch (menuItem) {
            case "Create Customer":
                navigationController.showCustomerRegistration();
                break;
            case "Open Account":
                navigationController.showAccountCreation();
                break;
            case "View Customers":
                // Show customers list
                showAlert(Alert.AlertType.INFORMATION, "View Customers", "Customer list feature coming soon!");
                break;
            case "Register Employee":
                navigationController.showMainMenu();
                break;
            case "Process Interest":
                showAlert(Alert.AlertType.INFORMATION, "Process Interest", "Interest processing feature coming soon!");
                break;
            case "Generate Statements":
                showAlert(Alert.AlertType.INFORMATION, "Generate Statements", "Statement generation feature coming soon!");
                break;
            default:
                showAlert(Alert.AlertType.INFORMATION, menuItem, "This feature is under development!");
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
                showAlert(Alert.AlertType.INFORMATION, "View Reports", "Reports feature coming soon!");
                break;
            case "Process Interest":
                showAlert(Alert.AlertType.INFORMATION, "Process Interest", "Interest processing feature coming soon!");
                break;
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