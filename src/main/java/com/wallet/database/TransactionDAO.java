package com.wallet.database;

import com.wallet.models.Transaction;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void saveTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions(user_id, type, amount, description) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transaction.getUserId());
            pstmt.setString(2, transaction.getType());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setString(4, transaction.getDescription());
            pstmt.executeUpdate();
        }
    }

    public List<Transaction> getTransactionsByUserId(int userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY timestamp DESC LIMIT 20";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction trans = new Transaction();
                trans.setTransactionId(rs.getInt("id"));
                trans.setUserId(rs.getInt("user_id"));
                trans.setType(rs.getString("type"));
                trans.setAmount(rs.getDouble("amount"));
                trans.setDescription(rs.getString("description"));
                transactions.add(trans);
            }
        }
        return transactions;
    }
}
