package college;

import javax.swing.*;

public class MainDashboard extends JFrame {
    public MainDashboard() {
        setTitle("College Lost & Found + Instruments System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JButton btnLostFound = new JButton("Lost & Found");
        btnLostFound.setBounds(100, 50, 200, 40);
        btnLostFound.addActionListener(e -> new lostandfound.LostItemForm().setVisible(true));
        add(btnLostFound);

        JButton btnInstruments = new JButton("Instrument Borrow");
        btnInstruments.setBounds(100, 110, 200, 40);
        btnInstruments.addActionListener(e -> new instruments.RequestInstrument().setVisible(true));
        add(btnInstruments);

        JButton btnAdmin = new JButton("Admin Panel");
        btnAdmin.setBounds(100, 170, 200, 40);
        btnAdmin.addActionListener(e -> new admin.AdminInstrumentRequests().setVisible(true));
        add(btnAdmin);
    }

    public static void main(String[] args) {
        new MainDashboard().setVisible(true);
    }
}
