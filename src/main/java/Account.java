import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected List<Transaction> transactions;
    protected List<String> signatories;

    public Account(String accountNumber, double balance, String branch) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.transactions = new ArrayList<>();
        this.signatories = new ArrayList<>();
    }

    public void deposit(double amount) {
        if (amount > 0) {
            double oldBalance = balance;
            balance += amount;
            // FIXED: Use 5-argument Transaction constructor
            transactions.add(new Transaction(accountNumber, "DEPOSIT", amount, balance, "Deposit to account"));
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            double oldBalance = balance;
            balance -= amount;
            // FIXED: Use 5-argument Transaction constructor
            transactions.add(new Transaction(accountNumber, "WITHDRAWAL", amount, balance, "Withdrawal from account"));
            return true;
        }
        return false;
    }

    public void addSignatory(String signatoryName) {
        if (!signatories.contains(signatoryName)) {
            signatories.add(signatoryName);
        }
    }

    public void removeSignatory(String signatoryName) {
        signatories.remove(signatoryName);
    }

    public List<String> getSignatories() {
        return new ArrayList<>(signatories);
    }

    public boolean hasSignatory(String signatoryName) {
        return signatories.contains(signatoryName);
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public List<Transaction> getTransactionHistory() { return transactions; }

    public abstract String getAccountType();

    public String getFormattedBalance() {
        return String.format("P%.2f", balance);
    }
}