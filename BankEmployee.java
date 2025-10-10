public class BankEmployee {
    private String employeeId;
    private String password;
    private String firstName;
    private String lastName;

    public BankEmployee(String employeeId, String password, String firstName, String lastName) {
        this.employeeId = employeeId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean authenticate(String employeeId, String password) {
        return this.employeeId.equals(employeeId) && this.password.equals(password);
    }

    public String getEmployeeId() { return employeeId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}