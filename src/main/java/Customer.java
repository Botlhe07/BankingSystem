import java.io.Serializable;
import java.util.*;

public abstract class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String username;  // This will be the simple customer ID (like "C001")
    protected String password;
    protected String address;
    protected String phoneNumber;
    protected String email;
    protected List<Account> accounts;
    protected String employmentInfo;

    public Customer(String username, String password, String address, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.accounts = new ArrayList<>();
    }

    public Customer(String username, String password, String address, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.accounts = new ArrayList<>();
    }

    // ENHANCED: Better authentication method
    public boolean authenticate(String customerId, String password) {
        return this.username.equals(customerId) && this.password.equals(password);
    }

    // NEW: Get password for database operations
    public String getPassword() {
        return this.password;
    }

    // NEW: Set password for updates
    public void setPassword(String password) {
        this.password = password;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public abstract String getDisplayName();
    public abstract String getCustomerType();

    // CHANGED: customer_id is now the username (simple ID)
    public String getCustomerId() {
        return this.username;
    }

    // NEW: Set customer ID
    public void setCustomerId(String customerId) {
        this.username = customerId;
    }

    public String getFirstName() {
        return getDisplayName().split(" ")[0];
    }

    public String getLastName() {
        String[] nameParts = getDisplayName().split(" ");
        return nameParts.length > 1 ? nameParts[1] : "";
    }

    public String getEmail() {
        return this.email != null ? this.email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmploymentInfo() {
        return this.employmentInfo != null ? this.employmentInfo : "";
    }

    public void setEmploymentInfo(String employmentInfo) {
        this.employmentInfo = employmentInfo;
    }

    public String getUsername() { return username; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }

    // NEW: Helper method to check if this is a valid customer
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
                password != null && !password.trim().isEmpty();
    }
}