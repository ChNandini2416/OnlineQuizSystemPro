package ui;
import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Leaderboard {

    public static void showLeaderboard() {
        JFrame frame = new JFrame("🏆 Leaderboard");
        frame.setSize(530, 460);
        frame.setLayout(null);

        JLabel filterLabel = new JLabel("Select Quiz:");
        filterLabel.setBounds(30, 20, 100, 25);
        frame.add(filterLabel);

        JComboBox<String> quizBox = new JComboBox<>();
        quizBox.setBounds(130, 20, 260, 25);
        frame.add(quizBox);
        // Table setup
        String[] columns = { "Username", "Quiz Name", "Score", "Date" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 60, 460, 330);
        frame.add(scrollPane);
        // 🔁 Load all quiz names
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT quiz_id, quiz_name FROM quizzes";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("quiz_id");
                String name = rs.getString("quiz_name");
                quizBox.addItem(id + " - " + name);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error loading quiz list: " + e.getMessage());
        }
        // 🔍 Filter leaderboard when quiz is selected
        quizBox.addActionListener(e -> {
            model.setRowCount(0); // Clear table

            String selected = (String) quizBox.getSelectedItem();
            if (selected == null) return;

            int selectedQuizId = Integer.parseInt(selected.split(" - ")[0]);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = """
                    SELECT u.username, q.quiz_name, r.score, r.date_taken
                    FROM result r
                    JOIN users u ON r.user_id = u.user_id
                    JOIN quizzes q ON r.quiz_id = q.quiz_id
                    WHERE r.quiz_id = ?
                    ORDER BY r.score DESC, r.date_taken DESC
                    LIMIT 10
                """;
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, selectedQuizId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[] {
                        rs.getString("username"),
                        rs.getString("quiz_name"),
                        rs.getInt("score"),
                        rs.getString("date_taken")
                    });
                }
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "ℹ️ No attempts found for this quiz.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "❌ Error fetching leaderboard: " + ex.getMessage());
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}