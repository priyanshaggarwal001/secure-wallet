package com.wallet.gui;

import com.wallet.database.UserDAO;
import com.wallet.database.TransactionDAO;
import com.wallet.models.User;
import com.wallet.models.Transaction;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardFrame extends JFrame {
    private User currentUser;
    private UserDAO userDAO;
    private TransactionDAO transactionDAO;
    private JLabel balanceLabel;
    private JTextArea transactionArea;
    private DefaultListModel<String> transactionListModel;

    public DashboardFrame(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();

        setTitle("Secure Digital Wallet - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - User Info
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(new Color(200, 220, 240));
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getUsername());
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(userLabel);

        balanceLabel = new JLabel("Balance: $" + String.format("%.2f", currentUser.getBalance()));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(new Color(0, 128, 0));
        topPanel.add(balanceLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel - Transactions and Buttons
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Transaction Display
        JLabel transLabel = new JLabel("Recent Transactions");
        transLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        transactionListModel = new DefaultListModel<>();
        loadTransactions();
        
        JList<String> transactionList = new JList<>(transactionListModel);
        transactionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(transactionList);
        
        JPanel transactionPanel = new JPanel(new BorderLayout());
        transactionPanel.add(transLabel, BorderLayout.NORTH);
        transactionPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerPanel.add(transactionPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton depositButton = new JButton("Deposit");
        depositButton.setPreferredSize(new Dimension(100, 40));
        depositButton.addActionListener(e -> handleDeposit());
        buttonPanel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setPreferredSize(new Dimension(100, 40));
        withdrawButton.addActionListener(e -> handleWithdraw());
        buttonPanel.add(withdrawButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 40));
        refreshButton.addActionListener(e -> refreshDashboard());
        buttonPanel.add(refreshButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> logout());
        buttonPanel.add(logoutButton);

        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void handleDeposit() {
        String input = JOptionPane.showInputDialog(this, "Enter deposit amount:", "Deposit");
        if (input != null && !input.isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                currentUser.setBalance(currentUser.getBalance() + amount);
                userDAO.updateBalance(currentUser.getUserId(), currentUser.getBalance());
                
                Transaction transaction = new Transaction(currentUser.getUserId(), "DEPOSIT", amount, "Deposit from user");
                transactionDAO.saveTransaction(transaction);
                
                JOptionPane.showMessageDialog(this, "Deposit successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDashboard();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleWithdraw() {
        String input = JOptionPane.showInputDialog(this, "Enter withdrawal amount:", "Withdraw");
        if (input != null && !input.isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (currentUser.getBalance() < amount) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                currentUser.setBalance(currentUser.getBalance() - amount);
                userDAO.updateBalance(currentUser.getUserId(), currentUser.getBalance());
                
                Transaction transaction = new Transaction(currentUser.getUserId(), "WITHDRAW", amount, "Withdrawal by user");
                transactionDAO.saveTransaction(transaction);
                
                JOptionPane.showMessageDialog(this, "Withdrawal successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshDashboard();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTransactions() {
        try {
            List<Transaction> transactions = transactionDAO.getTransactionsByUserId(currentUser.getUserId());
            transactionListModel.clear();
            for (Transaction t : transactions) {
                transactionListModel.addElement(t.toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshDashboard() {
        try {
            var user = userDAO.getUserById(currentUser.getUserId());
            if (user.isPresent()) {
                currentUser = user.get();
                balanceLabel.setText("Balance: $" + String.format("%.2f", currentUser.getBalance()));
                loadTransactions();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing dashboard", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        dispose();
        new LoginFrame();
    }
}
