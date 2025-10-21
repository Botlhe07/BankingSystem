import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private double amount;
    private String description;
    private LocalDateTime timestamp;
    private String signatory;

    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.signatory = "System";
    }

    public Transaction(String type, double amount, String description, String signatory) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.signatory = signatory;
    }

    @Override
    public String toString() {
        return String.format("%s | %s: P%.2f | %s | Authorized by: %s",
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                type, amount, description, signatory);
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getSignatory() { return signatory; }

    public String getFormattedAmount() {
        return String.format("P%.2f", amount);
    }
}