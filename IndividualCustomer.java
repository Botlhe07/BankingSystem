public class IndividualCustomer extends Customer {
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String governmentId;
    private String sourceOfIncome;

    // Constructor with all parameters including sourceOfIncome
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

    // Constructor without sourceOfIncome (for backward compatibility)
    public IndividualCustomer(String username, String password, String firstName, String lastName,
                              String address, String phoneNumber, String dateOfBirth,
                              String governmentId) {
        this(username, password, firstName, lastName, address, phoneNumber, dateOfBirth, governmentId, "Not Specified");
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