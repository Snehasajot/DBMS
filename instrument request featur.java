🧠 3. Java Code to Request Instruments
🔹 a) Load Instruments into ComboBox
private void loadAvailableInstruments() {
    cmbInstruments.removeAllItems();
    try {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT instrument_id, name FROM instruments WHERE available_quantity > 0");

        while (rs.next()) {
            cmbInstruments.addItem(new ComboItem(rs.getInt("instrument_id"), rs.getString("name")));
        }
    } catch (SQLException e) {
        lblStatus.setText("Error loading instruments: " + e.getMessage());
    }
}
Use a helper class ComboItem:
public class ComboItem {
    int id;
    String name;

    public ComboItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }

    @Override
    public String toString() { return name; }
}
🔹 b) Request Button Code
private void btnRequestActionPerformed(java.awt.event.ActionEvent evt) {
    ComboItem selected = (ComboItem) cmbInstruments.getSelectedItem();
    if (selected == null) {
        lblStatus.setText("Select a valid instrument.");
        return;
    }

    try {
        PreparedStatement pst = con.prepareStatement("INSERT INTO instrument_requests (user_id, instrument_id, request_date) VALUES (?, ?, ?)");
        pst.setInt(1, currentUserId);  // from login session
        pst.setInt(2, selected.getId());
        pst.setDate(3, new java.sql.Date(System.currentTimeMillis()));
        pst.executeUpdate();

        lblStatus.setText("Request submitted successfully.");
        loadMyRequests();
    } catch (SQLException e) {
        lblStatus.setText("Request error: " + e.getMessage());
    }
}
🔹 c) Show Student's Requests in a Table
private void loadMyRequests() {
    DefaultTableModel model = (DefaultTableModel) tblMyRequests.getModel();
    model.setRowCount(0);

    try {
        PreparedStatement pst = con.prepareStatement(
            "SELECT ir.request_id, i.name, ir.request_date, ir.status FROM instrument_requests ir JOIN instruments i ON ir.instrument_id = i.instrument_id WHERE ir.user_id = ?");
        pst.setInt(1, currentUserId);1. Load Instruments into ComboBox
java
Copy
Edit
// Load all available instruments into the combo box for user selection
private void loadAvailableInstruments() {
    cmbInstruments.removeAllItems();  // Clear existing items first
    
    try {
        // Create a Statement object to execute the query
        Statement stmt = con.createStatement();
        
        // Execute query to get instruments with available quantity > 0
        ResultSet rs = stmt.executeQuery("SELECT instrument_id, name FROM instruments WHERE available_quantity > 0");

        // Loop through the result set
        while (rs.next()) {
            // Add each instrument as a ComboItem (id + name) to the combo box
            cmbInstruments.addItem(new ComboItem(rs.getInt("instrument_id"), rs.getString("name")));
        }
    } catch (SQLException e) {
        // Show error message if loading fails
        lblStatus.setText("Error loading instruments: " + e.getMessage());
    }
}
Helper class to represent each instrument in ComboBox:

java
Copy
Edit
public class ComboItem {
    int id;         // Store instrument ID
    String name;    // Store instrument name

    // Constructor to set id and name
    public ComboItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter for ID (used when submitting request)
    public int getId() {
        return id;
    }

    // This controls how the item appears in the ComboBox list (shows name)
    @Override
    public String toString() {
        return name;
    }
}
2. Button Action to Request an Instrument
java
Copy
Edit
// Action performed when user clicks Request button
private void btnRequestActionPerformed(java.awt.event.ActionEvent evt) {
    // Get selected instrument from combo box
    ComboItem selected = (ComboItem) cmbInstruments.getSelectedItem();

    // If no instrument selected, show error and return
    if (selected == null) {
        lblStatus.setText("Select a valid instrument.");
        return;
    }

    try {
        // Prepare SQL insert to add request to instrument_requests table
        PreparedStatement pst = con.prepareStatement(
            "INSERT INTO instrument_requests (user_id, instrument_id, request_date) VALUES (?, ?, ?)"
        );

        // Set parameters: current user ID, selected instrument ID, and today's date
        pst.setInt(1, currentUserId);                 // Assume currentUserId is set from login session
        pst.setInt(2, selected.getId());              // Instrument ID from ComboItem
        pst.setDate(3, new java.sql.Date(System.currentTimeMillis())); // Current date

        // Execute insert
        pst.executeUpdate();

        // Show success message
        lblStatus.setText("Request submitted successfully.");

        // Reload the user's requests to update table display
        loadMyRequests();

    } catch (SQLException e) {
        // Show error message if insert fails
        lblStatus.setText("Request error: " + e.getMessage());
    }
}
3. Load Student's Instrument Requests into Table
java
Copy
Edit
// Load current user's instrument requests and show in table
private void loadMyRequests() {
    // Get the table model for the requests table
    DefaultTableModel model = (DefaultTableModel) tblMyRequests.getModel();

    // Clear existing rows before loading fresh data
    model.setRowCount(0);

    try {
        // Prepare SQL query joining requests and instrument names for this user
        PreparedStatement pst = con.prepareStatement(
            "SELECT ir.request_id, i.name, ir.request_date, ir.status " +
            "FROM instrument_requests ir " +
            "JOIN instruments i ON ir.instrument_id = i.instrument_id " +
            "WHERE ir.user_id = ?"
        );

        // Set user ID parameter
        pst.setInt(1, currentUserId);

        // Execute query and get results
        ResultSet rs = pst.executeQuery();

        // Loop through each request and add to table model as a new row
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("request_id"),        // Request ID
                rs.getString("name"),            // Instrument Name
                rs.getDate("request_date"),      // Date of request
                rs.getString("status")           // Request status (e.g. pending, approved)
            });
        }

    } catch (SQLException e) {
        // Show error message if loading requests fails
        lblStatus.setText("Load error: " + e.getMessage());
    }
}
