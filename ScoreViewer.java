package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ScoreViewer {

    public static void showHistory(int userId) {
        JFrame frame = new JFrame("All Quiz Attempts");
        frame.setSize(600, 400);
        frame.setLayout(null);

        String[] columns = { "Username", "Quiz Name", "Score", "Date Taken" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 20, 540, 300);
        frame.add(scroll);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.username, q.quiz_name, r.score, r.date_taken " +
                         "FROM result r " +
                         "JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                         "JOIN users u ON r.user_id = u.user_id " +
                         "ORDER BY r.date_taken DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String username = rs.getString("username");
                String quizName = rs.getString("quiz_name");
                int score = rs.getInt("score");
                Timestamp date = rs.getTimestamp("date_taken");

                model.addRow(new Object[] { username, quizName, score, date });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error loading history: " + e.getMessage());
        }

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}