3. Load All Instrument Requests into Admin Table

// Load all instrument requests from all users and display in the admin table
private void loadAllRequests() {
    // Get the table model to manipulate rows
    DefaultTableModel model = (DefaultTableModel) tblAllRequests.getModel();
    
    // Clear existing rows before loading new data
    model.setRowCount(0);

    try {
        // Prepare SQL query to join requests with user names and instrument names
        PreparedStatement pst = con.prepareStatement(
            "SELECT ir.request_id, u.name, i.name AS instrument, ir.request_date, ir.status " +
            "FROM instrument_requests ir " +
            "JOIN users u ON ir.user_id = u.user_id " +
            "JOIN instruments i ON ir.instrument_id = i.instrument_id"
        );

        // Execute query and get results
        ResultSet rs = pst.executeQuery();

        // Loop through all requests
        while (rs.next()) {
            // Add each request as a row in the table
            model.addRow(new Object[]{
                rs.getInt("request_id"),           // Request ID
                rs.getString("name"),               // User's name
                rs.getString("instrument"),         // Instrument name
                rs.getDate("request_date"),         // Date of request
                rs.getString("status")              // Request status (pending/approved/denied)
            });
        }
    } catch (SQLException e) {
        // Show error message if query fails
        lblStatus.setText("Load failed: " + e.getMessage());
    }
}
4. Approve Button Logic

// Approve the selected instrument request and update inventory
private void btnApproveActionPerformed(java.awt.event.ActionEvent evt) {
    // Get selected row index in the table
    int selectedRow = tblAllRequests.getSelectedRow();
    
    // If no row is selected, show message and return
    if (selectedRow == -1) {
        lblStatus.setText("Select a request first.");
        return;
    }

    // Get the request ID from the selected row (column 0)
    int requestId = (int) tblAllRequests.getValueAt(selectedRow, 0);

    try {
        // Step 1: Get instrument_id for the selected request
        PreparedStatement pst = con.prepareStatement(
            "SELECT instrument_id FROM instrument_requests WHERE request_id = ?"
        );
        pst.setInt(1, requestId);
        ResultSet rs = pst.executeQuery();

        // If request not found, return early
        if (!rs.next()) return;

        int instrumentId = rs.getInt("instrument_id");

        // Step 2: Decrement available_quantity for that instrument if available (> 0)
        pst = con.prepareStatement(
            "UPDATE instruments SET available_quantity = available_quantity - 1 WHERE instrument_id = ? AND available_quantity > 0"
        );
        pst.setInt(1, instrumentId);
        int rowsAffected = pst.executeUpdate();

        // If no rows updated, instrument not available
        if (rowsAffected == 0) {
            lblStatus.setText("Instrument not available.");
            return;
        }

        // Step 3: Update the request status to 'approved'
        pst = con.prepareStatement(
            "UPDATE instrument_requests SET status = 'approved' WHERE request_id = ?"
        );
        pst.setInt(1, requestId);
        pst.executeUpdate();

        // Success message
        lblStatus.setText("Request approved.");

        // Reload all requests to update table UI
        loadAllRequests();

    } catch (SQLException e) {
        // Show error message on failure
        lblStatus.setText("Error approving: " + e.getMessage());
    }
}
5. Deny Button Logic

// Deny the selected instrument request
private void btnDenyActionPerformed(java.awt.event.ActionEvent evt) {
    // Get selected row index
    int selectedRow = tblAllRequests.getSelectedRow();

    // If no row is selected, prompt the user
    if (selectedRow == -1) {
        lblStatus.setText("Select a request first.");
        return;
    }

    // Get the request ID from selected row
    int requestId = (int) tblAllRequests.getValueAt(selectedRow, 0);

    try {
        // Update the request status to 'denied'
        PreparedStatement pst = con.prepareStatement(
            "UPDATE instrument_requests SET status = 'denied' WHERE request_id = ?"
        );
        pst.setInt(1, requestId);
        pst.executeUpdate();

        // Inform success
        lblStatus.setText("Request denied.");

        // Reload requests to refresh table display
        loadAllRequests();

    } catch (SQLException e) {
        // Show error message if query fails
        lblStatus.setText("Error denying: " + e.getMessage());
    }
}
