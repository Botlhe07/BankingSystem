import java.io.Serializable;

public class SavingsAccount extends Account implements InterestBearing, Serializable {
    private static final long serialVersionUID = 1L;

    public SavingsAccount(String accountNumber, double balance, String branch) {
        super(accountNumber, balance, branch);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public double calculateInterest() {
        return balance * 0.02; // 2% annual interest
    }
}