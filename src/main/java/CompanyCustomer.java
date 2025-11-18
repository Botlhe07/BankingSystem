import java.io.Serializable;

public class CompanyCustomer extends Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String companyName;
    private String registrationNumber;
    private String contactName;
    private String companyAddress;

    public CompanyCustomer(String username, String password, String companyName,
                           String address, String phoneNumber, String registrationNumber,
                           String contactName, String companyAddress) {
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
        return "COMPANY"; // MUST BE UPPERCASE
    }

    @Override
    public String getFirstName() {
        if (this.contactName != null && !this.contactName.trim().isEmpty()) {
            String[] nameParts = this.contactName.split(" ", 2);
            return nameParts[0];
        }
        return "Company";
    }

    @Override
    public String getLastName() {
        if (this.contactName != null && !this.contactName.trim().isEmpty()) {
            String[] nameParts = this.contactName.split(" ", 2);
            return nameParts.length > 1 ? nameParts[1] : "";
        }
        return "";
    }

    public String getCompanyName() { return companyName; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getContactName() { return contactName; }
    public String getCompanyAddress() { return companyAddress; }
}