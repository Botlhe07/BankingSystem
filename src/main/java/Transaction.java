import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    private String transactionType;
    private double amount;
    private double balanceAfter;
    private String description;
    private LocalDateTime timestamp;

    public Transaction(String accountNumber, String transactionType, double amount,
                       double balanceAfter, String description) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getTransactionType() { return transactionType; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %s: P%.2f | Balance: P%.2f | %s",
                timestamp.format(formatter),
                transactionType, amount, balanceAfter, description);
    }



}