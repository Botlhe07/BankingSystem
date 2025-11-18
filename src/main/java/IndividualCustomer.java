import java.io.Serializable;

public class IndividualCustomer extends Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String firstName;
    private String lastName;

    public IndividualCustomer(String username, String password, String firstName, String lastName,
                              String address, String phoneNumber, String email) {
        super(username, password, address, phoneNumber, email);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getCustomerType() {
        return "INDIVIDUAL"; // MUST BE UPPERCASE
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public String getCustomerId() {
        return this.getUsername();
    }
}