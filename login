// Import required classes for database and GUI operations
import java.sql.*;              // For JDBC operations: Connection, PreparedStatement, ResultSet
import javax.swing.*;           // For GUI components like JFrame, JOptionPane, etc.

// Define the Login class which extends JFrame (for GUI window)
public class Login extends javax.swing.JFrame {
    
    // Declare variables to manage database connection and query execution
    Connection con;             // Used to establish connection to MySQL
    PreparedStatement pst;     // Used to prepare SQL queries
    ResultSet rs;              // Used to hold the result of SQL queries

    // Constructor - automatically called when an object of Login is created
    public Login() {
        initComponents();      // Auto-generated method that initializes GUI components
        connectDB();           // Call method to connect to the database
    }

    // Method to connect to MySQL database
    public void connectDB() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database with URL, username, and password
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/college_lost_found", // DB URL
                "root",                                           // DB username
                "your_password"                                   // DB password (replace with your real password)
            );
        } catch (Exception e) {
            // Show error dialog if connection fails
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage());
        }
    }

    // This method is called when the "Login" button is clicked
    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        
        // Get text entered by user in email and password fields
        String email = txtEmail.getText();                          // Get email input
        String password = String.valueOf(txtPassword.getPassword()); // Get password input (converted from char[])

        try {
            // SQL query to check if user exists with the entered email and password
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

            // Prepare the SQL statement
            pst = con.prepareStatement(sql);

            // Set the values of placeholders (?) in the SQL query
            pst.setString(1, email);      // Replace first ? with user's email
            pst.setString(2, password);   // Replace second ? with user's password

            // Execute the query and store the result
            rs = pst.executeQuery();

            // Check if a matching user is found
            if (rs.next()) {
                // If user exists, get their role and name from the database
                String role = rs.getString("role");   // Get role: student/faculty/admin
                String name = rs.getString("name");   // Get user's name

                // Show a welcome message
                JOptionPane.showMessageDialog(this, "Welcome " + name + " (" + role + ")");

                // Redirect the user to the correct dashboard based on their role
                switch (role) {
                    case "student":
                        new StudentDashboard().setVisible(true);  // Open student dashboard
                        break;
                    case "faculty":
                        new FacultyDashboard().setVisible(true);  // Open faculty dashboard
                        break;
                    case "admin":
                        new AdminDashboard().setVisible(true);    // Open admin dashboard
                        break;
                }

                // Close the login window
                this.dispose();
            } else {
                // If user doesn't exist, show error message on screen
                lblMessage.setText("Invalid email or password.");
            }

        } catch (SQLException ex) {
            // If SQL error occurs, show an error message
            JOptionPane.showMessageDialog(this, "Login Failed: " + ex.getMessage());
        }
    }
}
