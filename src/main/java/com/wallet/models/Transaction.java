package com.wallet.models;

import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private int userId;
    private String type; // DEPOSIT, WITHDRAW
    private double amount;
    private LocalDateTime timestamp;
    private String description;

    public Transaction() {}

    public Transaction(int userId, String type, double amount, String description) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return type + " | $" + amount + " | " + timestamp + " | " + description;
    }
}
