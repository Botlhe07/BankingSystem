public class ChequeAccount extends Account {
    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, double balance, String branch,
                         String employerName, String employerAddress) {
        super(accountNumber, balance, branch);
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public String getAccountType() {
        return "Cheque Account";
    }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }
}
