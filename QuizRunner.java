package ui;

import db.DBConnection;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class QuizRunner {
    public static int UserId;         // Set during login
    public static int currentQuizId;  // Set when quiz is selected

    static class Question {
        String questionText;
        String optionA, optionB, optionC, optionD;
        String correctOption;
        String selectedOption;
    }
    static ArrayList<Question> questions = new ArrayList<>();
    static int currentIndex = 0;
    static int Score = 0;

    public static void main(String[] args) {
        QuizRunner.UserId = 1123; // For testing directly
        showStartScreen();
    }
    static void showStartScreen() {
        JFrame frame = new JFrame("Welcome to Quiz App");
        frame.setSize(400, 200);
        frame.setLayout(null);

        JLabel label = new JLabel("Start your quiz here!");
        label.setBounds(120, 30, 200, 30);
        frame.add(label);

        JButton startButton = new JButton("Start Quiz");
        startButton.setBounds(140, 80, 120, 30);
        frame.add(startButton);

        startButton.addActionListener(e -> {
            frame.dispose();
            startQuiz(1); // Use any quizId you'd like
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public static void startQuiz(int quizId) {
        currentQuizId = quizId;
        loadQuestions(quizId);
        showQuestion();
    }
    static void loadQuestions(int quizId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM Questions WHERE quiz_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();

            questions.clear();
            currentIndex = 0;
            Score = 0;

            while (rs.next()) {
                Question q = new Question();
                q.questionText = rs.getString("question_text");
                q.optionA = rs.getString("option_a");
                q.optionB = rs.getString("option_b");
                q.optionC = rs.getString("option_c");
                q.optionD = rs.getString("option_d");
                q.correctOption = rs.getString("correct_answer");
                questions.add(q);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "❌ Error loading questions: " + e.getMessage());
        }
    }

    static void showQuestion() {
        if (currentIndex == questions.size()) {
            JOptionPane.showMessageDialog(null, "🎉 Quiz finished! Your score: " + Score);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO result (user_id, quiz_id, score, date_taken) VALUES (?, ?, ?, NOW())";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, UserId);
                pstmt.setInt(2, currentQuizId);
                pstmt.setInt(3, Score);
                pstmt.executeUpdate();
                System.out.println("✅ Quiz result saved to database!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "❌ Error saving result: " + e.getMessage());
            }

            int option = JOptionPane.showOptionDialog(
                null,
                "🎉 Quiz finished! What would you like to view?",
                "View Options",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] {
                    "📊 View History",
                    "🧠 Review Answers",
                    "📈 My Stats",
                    "❌ Nothing"
                },
                "📊 View History"
            );

            if (option == 0) {
                ScoreViewer.showHistory(UserId);
            } else if (option == 1) {
                QuizReview.showReview(questions);
            } else if (option == 2) {
                UserStats.showStats(UserId);
            }

            return;
        }

        Question q = questions.get(currentIndex);
        JFrame frame = new JFrame("Question " + (currentIndex + 1));
        frame.setSize(500, 350);
        frame.setLayout(null);

        JLabel questionLabel = new JLabel("<html>" + q.questionText + "</html>");
        questionLabel.setBounds(30, 20, 440, 40);
        frame.add(questionLabel);

        JRadioButton optA = new JRadioButton("A. " + q.optionA);
        JRadioButton optB = new JRadioButton("B. " + q.optionB);
        JRadioButton optC = new JRadioButton("C. " + q.optionC);
        JRadioButton optD = new JRadioButton("D. " + q.optionD);

        ButtonGroup group = new ButtonGroup();
        group.add(optA); group.add(optB); group.add(optC); group.add(optD);

        optA.setBounds(30, 80, 420, 25);
        optB.setBounds(30, 110, 420, 25);
        optC.setBounds(30, 140, 420, 25);
        optD.setBounds(30, 170, 420, 25);
        frame.add(optA); frame.add(optB); frame.add(optC); frame.add(optD);
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(180, 230, 100, 30);
        frame.add(nextButton);

        nextButton.addActionListener(e -> {
            String selected = "";
            if (optA.isSelected()) selected = "A";
            else if (optB.isSelected()) selected = "B";
            else if (optC.isSelected()) selected = "C";
            else if (optD.isSelected()) selected = "D";

            if (selected.equalsIgnoreCase(q.correctOption)) {
                Score++;
            }
            q.selectedOption = selected;

            currentIndex++;
            frame.dispose();
            showQuestion();
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}