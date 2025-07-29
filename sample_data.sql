INSERT INTO Users (username, password) VALUES ('nandu', 'nan'), ('pavani', 'pav');

INSERT INTO Quizzes (title) VALUES ('Java Basics'), ('Database Fundamentals');

INSERT INTO Questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_option)
VALUES
(1, 'What is JVM?', 'Java Virtual Machine', 'Java Version Manager', 'Java Visual Memory', 'Joint Variable Map', 'A'),
(2, 'What does SQL stand for?', 'Structured Question Language', 'Simple Query Logic', 'Structured Query Language', 'Statement Query Lookup', 'C');