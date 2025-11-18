import java.io.Serializable;


public class BankEmployee implements Serializable {
    private static final long serialVersionUID = 1L;
    private String employeeId;
    private String password;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    private boolean isActive;

    // Updated constructor with 6 parameters
    public BankEmployee(String employeeId, String password, String firstName, String lastName,
                        String position, String department) {
        this.employeeId = employeeId;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.department = department;
        this.isActive = true;
    }

    // UPDATED: Now accepts both employeeId and password for authentication
    public boolean authenticate(String inputEmployeeId, String inputPassword) {
        return this.employeeId.equals(inputEmployeeId) &&
                this.password.equals(inputPassword) &&
                this.isActive;
    }

    // Getters
    public String getEmployeeId() { return employeeId; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPosition() { return position; }
    public String getDepartment() { return department; }
    public boolean isActive() { return isActive; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}