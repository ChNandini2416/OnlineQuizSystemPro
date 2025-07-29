package ui;

import db.DBConnection;
import javax.swing.*;
import java.sql.*;

public class UserStats {
    public static void showStats(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT 
                    COUNT(*) AS total_attempts,
                    COALESCE(AVG(score), 0) AS avg_score,
                    COALESCE(MAX(score), 0) AS max_score,
                    COALESCE(MAX(date_taken), 'N/A') AS last_date
                FROM result
                WHERE user_id = ?
            """;
            
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int attempts = rs.getInt("total_attempts");

                if (attempts > 0) {
                    double average = rs.getDouble("avg_score");
                    int highest = rs.getInt("max_score");
                    String lastDate = rs.getString("last_date");

                    String message = String.format(
                        "📋 Your Performance:%n" +
                        "- Total Quizzes Attempted: %d%n" +
                        "- Average Score: %.2f%n" +
                        "- Highest Score: %d%n" +
                        "- Last Attempt: %s",
                        attempts, average, highest, lastDate
                    );

                    JOptionPane.showMessageDialog(null, message);
                } else {
                    JOptionPane.showMessageDialog(null, "ℹ️ You haven’t attempted any quizzes yet.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "ℹ️ No results found for this user.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error fetching stats: " + e.getMessage());
        }
    }
}