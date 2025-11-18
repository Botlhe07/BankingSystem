import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // FIXED: Customer login with password
    public boolean customerLogin(String username, String password) {
        String sql = "SELECT * FROM customers WHERE customer_id = ? AND password = ?";

        System.out.println("🔐 CustomerDAO Login Attempt:");
        System.out.println("Customer ID: " + username);
        System.out.println("Password: " + password);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            boolean loginSuccess = rs.next();

            System.out.println("Login result: " + loginSuccess);
            if (loginSuccess) {
                System.out.println("Found customer: " + rs.getString("first_name") + " " + rs.getString("last_name"));
            }

            return loginSuccess;

        } catch (SQLException e) {
            System.out.println("Error during customer login: " + e.getMessage());
            return false;
        }
    }

    // FIXED: Create new customer with password - corrected parameter indices
    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, first_name, last_name, address, phone_number, email, customer_type, company_name, company_address, employment_info, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getPhoneNumber());
            pstmt.setString(6, customer.getEmail());
            pstmt.setString(7, customer.getCustomerType());

            // Handle different customer types - CORRECTED INDICES
            if (customer instanceof CompanyCustomer) {
                CompanyCustomer cc = (CompanyCustomer) customer;
                pstmt.setString(8, cc.getCompanyName());
                pstmt.setString(9, cc.getCompanyAddress());
                pstmt.setString(10, null); // employment_info
            } else {
                pstmt.setString(8, null); // company_name
                pstmt.setString(9, null); // company_address
                pstmt.setString(10, customer.getEmploymentInfo());
            }

            pstmt.setString(11, customer.getPassword()); // CORRECTED: Now index 11

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error creating customer: " + e.getMessage());
            e.printStackTrace(); // Added for better debugging
            return false;
        }
    }

    // UPDATED: Get customer by ID - includes password and handles both customer types correctly
    public Customer getCustomerById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String customerType = rs.getString("customer_type");
                if ("COMPANY".equalsIgnoreCase(customerType)) {
                    CompanyCustomer customer = new CompanyCustomer(
                            rs.getString("customer_id"),           // username
                            rs.getString("password"),              // password
                            rs.getString("company_name"),         // companyName
                            rs.getString("address"),              // address
                            rs.getString("phone_number"),         // phoneNumber
                            rs.getString("registration_number"),  // registrationNumber
                            rs.getString("first_name") + " " + rs.getString("last_name"), // contactName
                            rs.getString("company_address")       // companyAddress
                    );
                    customer.setEmail(rs.getString("email"));
                    return customer;
                } else {
                    // Individual customer
                    IndividualCustomer customer = new IndividualCustomer(
                            rs.getString("customer_id"),          // username
                            rs.getString("password"),             // password
                            rs.getString("first_name"),           // firstName
                            rs.getString("last_name"),            // lastName
                            rs.getString("address"),              // address
                            rs.getString("phone_number"),         // phoneNumber
                            rs.getString("email")                 // email
                    );
                    customer.setEmploymentInfo(rs.getString("employment_info"));
                    return customer;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting customer: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // UPDATED: Get all customers - includes password
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String customerType = rs.getString("customer_type");
                if ("COMPANY".equalsIgnoreCase(customerType)) {
                    CompanyCustomer customer = new CompanyCustomer(
                            rs.getString("customer_id"),           // username
                            rs.getString("password"),              // password
                            rs.getString("company_name"),         // companyName
                            rs.getString("address"),              // address
                            rs.getString("phone_number"),         // phoneNumber
                            rs.getString("registration_number"),  // registrationNumber
                            rs.getString("first_name") + " " + rs.getString("last_name"), // contactName
                            rs.getString("company_address")       // companyAddress
                    );
                    customer.setEmail(rs.getString("email"));
                    customers.add(customer);
                } else {
                    // Individual customer
                    IndividualCustomer customer = new IndividualCustomer(
                            rs.getString("customer_id"),          // username
                            rs.getString("password"),             // password
                            rs.getString("first_name"),           // firstName
                            rs.getString("last_name"),            // lastName
                            rs.getString("address"),              // address
                            rs.getString("phone_number"),         // phoneNumber
                            rs.getString("email")                 // email
                    );
                    customer.setEmploymentInfo(rs.getString("employment_info"));
                    customers.add(customer);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting all customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    // FIXED: Update customer information - corrected parameter indices
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, address = ?, phone_number = ?, email = ?, company_name = ?, company_address = ?, employment_info = ?, password = ? WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getPhoneNumber());
            pstmt.setString(5, customer.getEmail());

            // Handle different customer types - CORRECTED INDICES
            if (customer instanceof CompanyCustomer) {
                CompanyCustomer cc = (CompanyCustomer) customer;
                pstmt.setString(6, cc.getCompanyName());
                pstmt.setString(7, cc.getCompanyAddress());
                pstmt.setString(8, null); // employment_info
            } else {
                pstmt.setString(6, null); // company_name
                pstmt.setString(7, null); // company_address
                pstmt.setString(8, customer.getEmploymentInfo());
            }

            pstmt.setString(9, customer.getPassword()); // CORRECTED: Now index 9
            pstmt.setString(10, customer.getCustomerId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete customer
    public boolean deleteCustomer(String customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Check if customer exists
    public boolean customerExists(String customerId) {
        String sql = "SELECT 1 FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking customer existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // UPDATED: Get customers by type - includes password
    public List<Customer> getCustomersByType(String customerType) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_type = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerType.toUpperCase()); // Ensure case consistency
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                if ("COMPANY".equalsIgnoreCase(customerType)) {
                    CompanyCustomer customer = new CompanyCustomer(
                            rs.getString("customer_id"),           // username
                            rs.getString("password"),              // password
                            rs.getString("company_name"),         // companyName
                            rs.getString("address"),              // address
                            rs.getString("phone_number"),         // phoneNumber
                            rs.getString("registration_number"),  // registrationNumber
                            rs.getString("first_name") + " " + rs.getString("last_name"), // contactName
                            rs.getString("company_address")       // companyAddress
                    );
                    customer.setEmail(rs.getString("email"));
                    customers.add(customer);
                } else {
                    // Individual customer
                    IndividualCustomer customer = new IndividualCustomer(
                            rs.getString("customer_id"),          // username
                            rs.getString("password"),             // password
                            rs.getString("first_name"),           // firstName
                            rs.getString("last_name"),            // lastName
                            rs.getString("address"),              // address
                            rs.getString("phone_number"),         // phoneNumber
                            rs.getString("email")                 // email
                    );
                    customer.setEmploymentInfo(rs.getString("employment_info"));
                    customers.add(customer);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error getting customers by type: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    // NEW: Change customer password
    public boolean changePassword(String customerId, String newPassword) {
        String sql = "UPDATE customers SET password = ? WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, customerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // NEW: Debug method to check database state
    public void debugDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("=== DEBUG DATABASE CONTENTS ===");

            // Check customers table structure
            ResultSet rs = stmt.executeQuery("PRAGMA table_info(customers)");
            System.out.println("CUSTOMERS TABLE COLUMNS:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("name") + " (" + rs.getString("type") + ")");
            }

            // Check customers count
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers");
            rs.next();
            System.out.println("Total customers: " + rs.getInt("count"));

            // List all customers
            rs = stmt.executeQuery("SELECT customer_id, first_name, last_name, customer_type FROM customers");
            while (rs.next()) {
                System.out.println("Customer: " + rs.getString("customer_id") +
                        " - " + rs.getString("first_name") + " " + rs.getString("last_name") +
                        " (" + rs.getString("customer_type") + ")");
            }

            System.out.println("=== END DEBUG ===");

        } catch (SQLException e) {
            System.out.println("Debug error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}