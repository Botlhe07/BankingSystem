import java.io.Serializable;

public class IndividualCustomer extends Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String governmentId;
    private String sourceOfIncome;

    public IndividualCustomer(String username, String password, String firstName, String lastName,
                              String address, String phoneNumber, String dateOfBirth,
                              String governmentId, String sourceOfIncome) {
        super(username, password, address, phoneNumber);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.governmentId = governmentId;
        this.sourceOfIncome = sourceOfIncome;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getCustomerType() {
        return "Individual";
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGovernmentId() { return governmentId; }
    public String getSourceOfIncome() { return sourceOfIncome; }
}