package ui;

import db.DBConnection;
import javax.swing.*;
import java.sql.*;

import ui.UserStats;
import ui.Leaderboard;
import ui.ScoreViewer;

public class QuizSelector {

    public static void main(int userId) {
        JFrame frame = new JFrame("Select Quiz");
        frame.setSize(420, 360);  // 🖼️ Adequate space for all buttons
        frame.setLayout(null);

        JLabel label = new JLabel("Choose a Quiz:");
        label.setBounds(50, 30, 150, 30);
        frame.add(label);

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBounds(180, 30, 180, 30);
        frame.add(comboBox);

        // 🔁 Load quizzes from DB
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT quiz_id, quiz_name FROM quizzes";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                comboBox.addItem(rs.getInt("quiz_id") + " - " + rs.getString("quiz_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error loading quizzes: " + e.getMessage());
        }

        // 🚀 Start Quiz button
        JButton startButton = new JButton("Start Quiz");
        startButton.setBounds(145, 90, 120, 30);
        frame.add(startButton);

        // 📊 View History button
        JButton historyButton = new JButton("📊 View History");
        historyButton.setBounds(145, 130, 120, 30);
        frame.add(historyButton);

        // 📈 My Stats button
        JButton statsButton = new JButton("📈 My Stats");
        statsButton.setBounds(145, 170, 120, 30);
        frame.add(statsButton);

        // 🏆 Leaderboard button
        JButton lbButton = new JButton("🏆 Leaderboard");
        lbButton.setBounds(145, 210, 120, 30);
        frame.add(lbButton);

        // 🧠 Action: Start Quiz (you'll link this to your quiz runner)
        startButton.addActionListener(e -> {
            String selected = (String) comboBox.getSelectedItem();
            if (selected != null && selected.contains(" - ")) {
                int quizId = Integer.parseInt(selected.split(" - ")[0]);
                QuizRunner.UserId = userId;
                QuizRunner.startQuiz(quizId);
            } else {
                JOptionPane.showMessageDialog(null, "⚠️ Please select a quiz first.");
            }
        });
        // 📈 Action: View Stats
        statsButton.addActionListener(e -> {
            UserStats.showStats(userId);
        });
        // 📊 Action: View History
        historyButton.addActionListener(e -> {
            QuizRunner.UserId = userId;
            ScoreViewer.showHistory(userId);
        });

        // 🏆 Action: Show Leaderboard
        lbButton.addActionListener(e -> {
            Leaderboard.showLeaderboard();
        });
        frame.setVisible(true);
    }
}
