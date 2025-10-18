// BankingApp.java
import javafx.application.Application;
import javafx.stage.Stage;

public class BankingApp extends Application {
    private BankingSystem bankingSystem;
    private NavigationController navigationController;

    @Override
    public void start(Stage primaryStage) {
        bankingSystem = new BankingSystem();
        bankingSystem.initializeSampleData();

        navigationController = new NavigationController(primaryStage, bankingSystem);
        navigationController.showLoginScreen();

        primaryStage.setTitle("Banking System");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
