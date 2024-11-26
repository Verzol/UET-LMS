package game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import service.Question;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LiteraryQuestController {

    public Button startButton;
    @FXML
    private Label questionLabel;

    @FXML
    private Button option1;

    @FXML
    private Button option2;

    @FXML
    private Button option3;

    @FXML
    private Button option4;

    @FXML
    private Label resultLabel;

    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;


    private void loadQuestions() {
        questions.clear();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/questions.txt")), StandardCharsets.UTF_8))) {

            String line;
            String currentQuestion = "";
            List<String> currentAnswers = new ArrayList<>();
            String correctAnswer = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Câu hỏi")) {
                    if (!currentQuestion.isEmpty()) {

                        questions.add(new Question(currentQuestion, currentAnswers.get(0), currentAnswers.get(1),
                                currentAnswers.get(2), currentAnswers.get(3), correctAnswer));
                    }
                    currentQuestion = line.substring(line.indexOf(":") + 1).trim();
                    currentAnswers.clear();
                } else if (line.startsWith("Đáp án đúng")) {
                    correctAnswer = line.substring(line.indexOf(":") + 1).trim();
                } else if (line.matches("^\\d\\.\\s.*")) {
                    currentAnswers.add(line.substring(line.indexOf(" ") + 1).trim());
                }
            }


            if (!currentQuestion.isEmpty()) {
                questions.add(new Question(currentQuestion, currentAnswers.get(0), currentAnswers.get(1),
                        currentAnswers.get(2), currentAnswers.get(3), correctAnswer));
            }


            Collections.shuffle(questions);


            if (questions.size() > 10) {
                questions = new ArrayList<>(questions.subList(0, 10));
            }

            System.out.println("Loaded and shuffled questions: " + questions.size());

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startGame() {
        loadQuestions();
        if (questions.isEmpty()) {
            resultLabel.setText("No questions available.");
            return;
        }
        score = 0;
        currentQuestionIndex = 0;
        displayNextQuestion();
    }


    private void displayNextQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            questionLabel.setText(question.getQuestion());

            option1.setText(question.getOption1());
            option2.setText(question.getOption2());
            option3.setText(question.getOption3());
            option4.setText(question.getOption4());

            resultLabel.setText("");
        } else {

            resultLabel.setText("Game over! You scored: " + score + " out of " + questions.size());
            questionLabel.setText("Thank you for playing!");

            option1.setDisable(true);
            option2.setDisable(true);
            option3.setDisable(true);
            option4.setDisable(true);
        }
    }


    @FXML
    private void handleAnswer(javafx.event.ActionEvent event) {
        Button selectedButton = (Button) event.getSource();
        String selectedAnswer = selectedButton.getText();

        Question currentQuestion = questions.get(currentQuestionIndex);
        String correctAnswer = currentQuestion.getCorrectAnswer();

        if (selectedAnswer.equals(correctAnswer)) {
            resultLabel.setText("Correct! The answer is: " + correctAnswer);
            score++;
        } else {
            resultLabel.setText("Incorrect. The correct answer is: " + correctAnswer);
        }


        resultLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: purple; -fx-translate-y: -50;");
        resultLabel.setLayoutY(100);
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> displayNextQuestion());
                }
            }, 3000);
        } else {

            resultLabel.setText("Game over! You scored: " + score + " out of " + questions.size());
            questionLabel.setText("Thank you for playing!");
            option1.setDisable(true);
            option2.setDisable(true);
            option3.setDisable(true);
            option4.setDisable(true);
        }
    }
}
