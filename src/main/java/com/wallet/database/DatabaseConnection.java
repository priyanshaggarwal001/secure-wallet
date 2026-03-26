package com.wallet.database;

import java.sql.*;

public class DatabaseConnection {
    // MySQL Configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/wallet_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1285"; // Change this to your MySQL password
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found!");
                e.printStackTrace();
            }
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to MySQL database successfully");
        }
        return connection;
    }

    public static void initializeDatabase() throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "password VARCHAR(255) NOT NULL," +
                "email VARCHAR(100) NOT NULL," +
                "balance DOUBLE DEFAULT 0" +
                ")";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "type VARCHAR(50) NOT NULL," +
                "amount DOUBLE NOT NULL," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "description TEXT," +
                "FOREIGN KEY(user_id) REFERENCES users(id)" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createTransactionsTable);
            System.out.println("Database tables initialized successfully");
        }
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed");
        }
    }
}
