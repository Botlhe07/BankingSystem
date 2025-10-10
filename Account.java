import java.util.*;

public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected List<Transaction> transactionHistory;

    public Account(String accountNumber, double balance, String branch) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.transactionHistory = new ArrayList<>();
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add(new Transaction("DEPOSIT", amount, "Deposit to account"));
            System.out.println("Deposited: $" + amount);
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            transactionHistory.add(new Transaction("WITHDRAWAL", amount, "Withdrawal from account"));
            System.out.println("Withdrawn: $" + amount);
            return true;
        }
        return false;
    }

    public abstract String getAccountType();

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }
}
