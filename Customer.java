import java.util.*;

public abstract class Customer {
    protected String username;
    protected String password;
    protected String address;
    protected String phoneNumber;
    protected List<Account> accounts;

    public Customer(String username, String password, String address, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.accounts = new ArrayList<>();
    }

    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public abstract String getDisplayName();
    public abstract String getCustomerType();

    public String getUsername() { return username; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
}