import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        BankingSystem bankingSystem = new BankingSystem();

        // Create NavigationController and start with Main Menu
        NavigationController navigationController = new NavigationController(primaryStage, bankingSystem);
        navigationController.showMainMenu();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}