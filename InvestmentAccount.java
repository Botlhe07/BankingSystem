public class InvestmentAccount extends Account implements InterestBearing {
    public InvestmentAccount(String accountNumber, double balance, String branch) {
        super(accountNumber, balance, branch);
    }

    @Override
    public String getAccountType() {
        return "Investment Account";
    }

    @Override
    public double calculateInterest() {
        // 5% monthly interest
        return balance * 0.05;
    }
}