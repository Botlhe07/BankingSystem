// StatementScreen.java
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class StatementScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public StatementScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Generate Customer Statement", "Create account statements");

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> navigationController.showEmployeeDashboard());

        // Form content
        VBox formContent = createStatementForm();

        VBox content = new VBox(20, backButton, formContent);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 1000, 800);
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

    private VBox createStatementForm() {
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(600);
        formContainer.getStyleClass().add("form-container");

        Label formTitle = new Label("Generate Customer Statement");
        formTitle.getStyleClass().add("form-title");

        // Customer selection
        ComboBox<String> customerComboBox = new ComboBox<>();
        for (Customer customer : bankingSystem.getAllCustomers()) {
            customerComboBox.getItems().add(customer.getDisplayName() + " (" + customer.getUsername() + ")");
        }
        customerComboBox.setPromptText("Select Customer");
        customerComboBox.getStyleClass().add("form-field");
        customerComboBox.setPrefHeight(40);

        // Date range
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");

        HBox dateRangeBox = new HBox(15, startDatePicker, endDatePicker);

        // Statement type
        ComboBox<String> statementTypeComboBox = new ComboBox<>();
        statementTypeComboBox.getItems().addAll("Account Summary", "Detailed Transactions", "Tax Statement");
        statementTypeComboBox.setPromptText("Statement Type");
        statementTypeComboBox.getStyleClass().add("form-field");
        statementTypeComboBox.setPrefHeight(40);

        Button generateButton = new Button("Generate Statement");
        generateButton.getStyleClass().addAll("btn", "btn-success");
        generateButton.setPrefHeight(45);
        generateButton.setMaxWidth(Double.MAX_VALUE);
        generateButton.setOnAction(e -> generateStatement());

        formContainer.getChildren().addAll(
                formTitle,
                createLabel("Select Customer"), customerComboBox,
                createLabel("Date Range"), dateRangeBox,
                createLabel("Statement Type"), statementTypeComboBox,
                generateButton
        );

        return formContainer;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void generateStatement() {
        showAlert(Alert.AlertType.INFORMATION, "Statement Generated",
                "Customer statement has been generated successfully!");
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