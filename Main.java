import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private BankingSystem bankingSystem;
    private NavigationController navigationController;

    @Override
    public void start(Stage primaryStage) {
        bankingSystem = new BankingSystem();
        bankingSystem.initializeSampleData();

        navigationController = new NavigationController(primaryStage, bankingSystem);

        // Start with Main Menu (where user can register first employee)
        navigationController.showMainMenu();

        primaryStage.setTitle("Banking System");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Save data when application closes
        primaryStage.setOnCloseRequest(e -> {
            bankingSystem.saveAllData();
            System.out.println("Application closing - data saved.");
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}