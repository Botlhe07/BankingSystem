import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankEmployeeDAO {

    // ADD THIS SIMPLE LOGIN METHOD
    public boolean employeeLogin(String employeeId, String password) {
        String sql = "SELECT * FROM employees WHERE employee_id = ? AND password = ?";

        System.out.println("🔐 EmployeeDAO Login Attempt:");
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Password: " + password);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeId);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            boolean loginSuccess = rs.next();

            System.out.println("Login result: " + loginSuccess);
            if (loginSuccess) {
                System.out.println("Found employee: " + rs.getString("first_name") + " " + rs.getString("last_name"));
            }

            return loginSuccess;

        } catch (SQLException e) {
            System.out.println("Error during employee login: " + e.getMessage());
            return false;
        }
    }

    // Get employee by employee ID
    public BankEmployee getEmployeeById(String employeeId) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new BankEmployee(
                        rs.getString("employee_id"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("position"),
                        rs.getString("department")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error getting employee by ID: " + e.getMessage());
        }
        return null;
    }

    // Get all active employees
    public List<BankEmployee> getAllEmployees() {
        List<BankEmployee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BankEmployee employee = new BankEmployee(
                        rs.getString("employee_id"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("position"),
                        rs.getString("department")
                );
                employees.add(employee);
            }

        } catch (SQLException e) {
            System.out.println("Error getting all employees: " + e.getMessage());
        }
        return employees;
    }

    // Verify employee credentials
    public BankEmployee verifyEmployee(String employeeId, String password) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                BankEmployee employee = new BankEmployee(
                        rs.getString("employee_id"),
                        rs.getString("password"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("position"),
                        rs.getString("department")
                );

                // Verify password using the authenticate method
                if (employee.authenticate(employeeId, password)) {
                    return employee;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error verifying employee: " + e.getMessage());
        }
        return null;
    }

    // Check if any employees exist (for first-time setup)
    public boolean hasEmployees() {
        String sql = "SELECT COUNT(*) as count FROM employees";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error checking if employees exist: " + e.getMessage());
        }
        return false;
    }

    // Update employee
    public boolean updateEmployee(BankEmployee employee) {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, position = ?, department = ? WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getPosition());
            pstmt.setString(4, employee.getDepartment());
            pstmt.setString(5, employee.getEmployeeId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    // Delete employee (soft delete)
    public boolean deleteEmployee(String employeeId) {
        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    public boolean createEmployee(BankEmployee employee) {
        String sql = "INSERT INTO employees (employee_id, password, first_name, last_name, position, department) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employee.getEmployeeId());
            pstmt.setString(2, employee.getPassword());
            pstmt.setString(3, employee.getFirstName());
            pstmt.setString(4, employee.getLastName());
            pstmt.setString(5, employee.getPosition());
            pstmt.setString(6, employee.getDepartment());

            int rowsAffected = pstmt.executeUpdate();

            // ADD DEBUG:
            System.out.println("DEBUG createEmployee: " + employee.getEmployeeId() +
                    " | Rows affected: " + rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            // ADD DEBUG:
            System.out.println("DEBUG SQL Error: " + e.getMessage());
            System.out.println("DEBUG SQL State: " + e.getSQLState());
            System.out.println("DEBUG Error Code: " + e.getErrorCode());
            return false;
        }
    }
}