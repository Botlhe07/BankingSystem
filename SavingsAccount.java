public class SavingsAccount extends Account implements InterestBearing {
    public SavingsAccount(String accountNumber, double balance, String branch) {
        super(accountNumber, balance, branch);
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public boolean withdraw(double amount) {
        System.out.println("Withdrawals are not allowed from Savings accounts.");
        return false;
    }

    @Override
    public double calculateInterest() {
        // 0.05% monthly interest
        return balance * 0.0005;
    }
}
