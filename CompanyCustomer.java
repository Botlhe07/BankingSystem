public class CompanyCustomer extends Customer {
    private String companyName;
    private String registrationNumber;
    private String contactName;
    private String companyAddress;

    public CompanyCustomer(String username, String password, String companyName, String address,
                           String phoneNumber, String registrationNumber, String contactName,
                           String companyAddress) {
        super(username, password, address, phoneNumber);
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
        this.contactName = contactName;
        this.companyAddress = companyAddress;
    }

    @Override
    public String getDisplayName() {
        return companyName;
    }

    @Override
    public String getCustomerType() {
        return "Company";
    }

    public String getCompanyName() { return companyName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getContactName() { return contactName; }
    public String getCompanyAddress() { return companyAddress; }
}