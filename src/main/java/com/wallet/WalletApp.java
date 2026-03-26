package com.wallet;

import com.wallet.database.DatabaseConnection;
import com.wallet.gui.LoginFrame;
import javax.swing.*;
import java.sql.SQLException;

public class WalletApp {
    public static void main(String[] args) {
        try {
            // Initialize database
            DatabaseConnection.initializeDatabase();
            System.out.println("Application started successfully");

            // Launch GUI
            SwingUtilities.invokeLater(() -> new LoginFrame());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database initialization failed: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
