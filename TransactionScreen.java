// TransactionScreen.java
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TransactionScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;
    private String transactionType;

    public TransactionScreen(NavigationController navigationController, BankingSystem bankingSystem, String transactionType) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        this.transactionType = transactionType;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        String title = transactionType + " Funds";
        HBox header = createHeader(title, "Process financial transactions");

        // Back button
        Button backButton = new Button("← Back to Dashboard");
        backButton.getStyleClass().addAll("btn", "btn-outline");
        backButton.setOnAction(e -> goBack());

        // Form content
        VBox formContent = createTransactionForm();

        VBox content = new VBox(20, backButton, formContent);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);

        mainLayout.setTop(header);
        mainLayout.setCenter(content);

        scene = new Scene(mainLayout, 900, 700);
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

    private VBox createTransactionForm() {
        VBox formContainer = new VBox(20);
        formContainer.setMaxWidth(500);
        formContainer.getStyleClass().add("form-container");

        Label formTitle = new Label(transactionType + " Funds");
        formTitle.getStyleClass().add("form-title");

        // Account selection
        ComboBox<String> accountComboBox = new ComboBox<>();
        accountComboBox.getItems().addAll("ACC1001 - Savings ($1,500.00)", "ACC1002 - Cheque ($2,500.00)");
        accountComboBox.setPromptText("Select Account");
        accountComboBox.getStyleClass().add("form-field");
        accountComboBox.setPrefHeight(40);

        // Amount field
        TextField amountField = new TextField();
        amountField.setPromptText("Enter Amount");
        amountField.getStyleClass().add("form-field");
        amountField.setPrefHeight(40);

        // Description field
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Transaction Description (Optional)");
        descriptionField.getStyleClass().add("form-field");
        descriptionField.setPrefHeight(40);

        Button submitButton = new Button("Process " + transactionType);
        submitButton.getStyleClass().addAll("btn",
                transactionType.equals("Deposit") ? "btn-success" : "btn-warning");
        submitButton.setPrefHeight(45);
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(e -> processTransaction());

        formContainer.getChildren().addAll(
                formTitle,
                createLabel("Select Account"), accountComboBox,
                createLabel("Amount"), amountField,
                createLabel("Description"), descriptionField,
                submitButton
        );

        return formContainer;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void processTransaction() {
        // Implementation for transaction processing
        showAlert(Alert.AlertType.INFORMATION, "Success",
                transactionType + " processed successfully!");
        goBack();
    }

    private void goBack() {
        if (bankingSystem.getCurrentCustomer() != null) {
            navigationController.showCustomerDashboard();
        } else {
            navigationController.showEmployeeDashboard();
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