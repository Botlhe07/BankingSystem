import java.io.*;
import java.util.Base64;

public class DatabaseManager {
    public void saveData(BankingSystem bankingSystem) {
        // In a real application, this would save to a database
        // For this example, we'll simulate encryption by encoding to Base64
        try {
            // Simulate data serialization and encryption
            String data = "BankingSystemData:" + System.currentTimeMillis();
            String encryptedData = Base64.getEncoder().encodeToString(data.getBytes());

            // Save to file (simulating database persistence)
            try (PrintWriter writer = new PrintWriter("bank_data.enc")) {
                writer.println(encryptedData);
            }

            System.out.println("Data saved and encrypted successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public void loadData(BankingSystem bankingSystem) {
        // In a real application, this would load from a database
        try {
            // Load from file (simulating database persistence)
            File file = new File("bank_data.enc");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String encryptedData = reader.readLine();
                    // Simulate decryption
                    String data = new String(Base64.getDecoder().decode(encryptedData));
                    System.out.println("Data loaded and decrypted successfully: " + data);
                }
            } else {
                System.out.println("No saved data found. Starting with sample data.");
            }
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}
