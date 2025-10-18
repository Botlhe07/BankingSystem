// MainMenuScreen.java
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainMenuScreen {
    private Scene scene;
    private NavigationController navigationController;
    private BankingSystem bankingSystem;

    public MainMenuScreen(NavigationController navigationController, BankingSystem bankingSystem) {
        this.navigationController = navigationController;
        this.bankingSystem = bankingSystem;
        createUI();
    }

    private void createUI() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("root");

        // Header
        HBox header = createHeader("Banking System", "Main Menu");

        // Main content
        VBox content = new VBox(30);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to Banking System");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        welcomeLabel.getStyleClass().add("header-title");

        Label descriptionLabel = new Label("Choose an option to continue");
        descriptionLabel.getStyleClass().add("header-subtitle");

        // Button container
        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setMaxWidth(400);

        Button loginButton = new Button("Login");
        loginButton.getStyleClass().addAll("btn", "btn-primary");
        loginButton.setPrefHeight(50);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> navigationController.showLoginScreen());

        Button registerEmployeeButton = new Button("Register New Employee");
        registerEmployeeButton.getStyleClass().addAll("btn", "btn-secondary");
        registerEmployeeButton.setPrefHeight(50);
        registerEmployeeButton.setMaxWidth(Double.MAX_VALUE);
        registerEmployeeButton.setOnAction(e -> showEmployeeRegistration());

        Button exitButton = new Button("Exit System");
        exitButton.getStyleClass().addAll("btn", "btn-outline");
        exitButton.setPrefHeight(50);
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setOnAction(e -> System.exit(0));

        buttonContainer.getChildren().addAll(loginButton, registerEmployeeButton, exitButton);
        content.getChildren().addAll(welcomeLabel, descriptionLabel, buttonContainer);

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

    private void showEmployeeRegistration() {
        // For now, navigate to login screen - employee registration can be added later
        navigationController.showLoginScreen();
    }

    public Scene getScene() {
        return scene;
    }
}