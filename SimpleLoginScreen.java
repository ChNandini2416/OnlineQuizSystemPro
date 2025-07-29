package ui;

import db.DBConnection;
import javax.swing.*;
import java.sql.*;

public class SimpleLoginScreen {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        frame.setSize(300, 250);
        frame.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(40, 30, 100, 25);
        frame.add(userLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(40, 60, 200, 25);
        frame.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(40, 95, 100, 25);
        frame.add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(40, 125, 200, 25);
        frame.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(90, 165, 100, 30);
        frame.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "❌ Username and password required.");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                // Check if user exists with matching password
                String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                int userId;
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                    System.out.println("✅ Logged in: " + username);
                } else {
                    // User doesn't exist — insert new
                    String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                    insertStmt.setString(1, username);
                    insertStmt.setString(2, password);
                    insertStmt.executeUpdate();

                    ResultSet keyRs = insertStmt.getGeneratedKeys();
                    keyRs.next();
                    userId = keyRs.getInt(1);
                    System.out.println("🆕 Registered new user: " + username);
                }

                QuizRunner.UserId = userId;
                frame.dispose();
                QuizSelector.main(userId); // Proceed to quiz selection
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "❌ Login error: " + ex.getMessage());
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}