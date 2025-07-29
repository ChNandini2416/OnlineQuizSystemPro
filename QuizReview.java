package ui;

import javax.swing.*;
import java.util.List;
import ui.QuizReview;

public class QuizReview {
    public static void showReview(List<QuizRunner.Question> questions) {
        JFrame frame = new JFrame("Quiz Review");
        frame.setSize(600, 600);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        for (int i = 0; i < questions.size(); i++) {
            QuizRunner.Question q = questions.get(i);
            String summary = "<html><b>Q" + (i + 1) + ":</b> " + q.questionText + "<br>" +
                             "✔ Correct Answer: <b>" + q.correctOption + "</b><br>" +
                             "❓ Your Answer: <span style='color:" + 
                             (q.selectedOption.equalsIgnoreCase(q.correctOption) ? "green" : "red") + "'>" +
                             q.selectedOption + "</span><br><br></html>";

            JLabel label = new JLabel(summary);
            frame.add(label);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        frame.add(closeButton);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}