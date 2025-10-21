import java.io.Serializable;

public class InvestmentAccount extends Account implements InterestBearing, Serializable {
    private static final long serialVersionUID = 1L;

    public InvestmentAccount(String accountNumber, double balance, String branch) {
        super(accountNumber, balance, branch);
    }

    @Override
    public String getAccountType() {
        return "Investment Account";
    }

    @Override
    public double calculateInterest() {
        return balance * 0.05; // 5% annual interest
    }
}