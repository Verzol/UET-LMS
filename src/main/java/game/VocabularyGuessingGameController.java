package game;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VocabularyGuessingGameController {

    @FXML
    private Label questionLabel;

    @FXML
    private TextField answerTextField;

    @FXML
    private Button submitButton;

    @FXML
    private Label resultLabel;

    @FXML
    private Label scoreLabel;

    private int score = 0;

    private int currentQuestionIndex = 0;

    private List<String> questions = new ArrayList<>();
    private List<String> answers = new ArrayList<>();
    private List<String> currentQuestions = new ArrayList<>();
    private List<String> currentAnswers = new ArrayList<>();

    @FXML
    public void initialize() {
        loadQuestionsFromFile("/questionsvocabulary.txt");
        loadNextQuestions();


        Platform.runLater(() -> {
            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setResizable(false);
        });

        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(600);
    }


    private void loadQuestionsFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(fileName)), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    questions.add(parts[0].trim());
                    answers.add(parts[1].trim());
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }


    private void loadNextQuestions() {
        if (questions.size() >= 5) {
            Random rand = new Random();
            Set<Integer> selectedIndexes = new HashSet<>();

            while (selectedIndexes.size() < 5) {
                selectedIndexes.add(rand.nextInt(questions.size()));
            }

            currentQuestions.clear();
            currentAnswers.clear();

            for (Integer index : selectedIndexes) {
                currentQuestions.add(questions.get(index));
                currentAnswers.add(answers.get(index));
            }

            currentQuestionIndex = 0;
            loadNextQuestion();
        } else {
            questionLabel.setText("Not enough questions available.");
        }
    }

    private void loadNextQuestion() {
        if (currentQuestionIndex < currentQuestions.size()) {
            questionLabel.setText(currentQuestions.get(currentQuestionIndex));
            answerTextField.clear();
            resultLabel.setText("");
            resultLabel.setVisible(false);

        } else {
            questionLabel.setText("Game Over!");
            resultLabel.setText("Your final score is: " + score);
            submitButton.setDisable(true);
            resultLabel.setVisible(true);
        }
    }


    @FXML
    private void checkAnswer(ActionEvent event) {
        String userAnswer = answerTextField.getText().trim().toLowerCase();
        String correctAnswer = currentAnswers.get(currentQuestionIndex).toLowerCase();

        if (userAnswer.equals(correctAnswer)) {
            score++;
            Platform.runLater(() -> {
                resultLabel.setText("Correct!");
                resultLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            });
        } else {
            Platform.runLater(() -> {
                resultLabel.setText("Wrong! The correct answer is: " + correctAnswer);
                resultLabel.setTextFill(javafx.scene.paint.Color.RED);
            });
        }

        Platform.runLater(() -> {
            scoreLabel.setText("Score: " + score);
            resultLabel.setVisible(true);
        });


        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    currentQuestionIndex++;
                    loadNextQuestion();
                });
            }
        }, 3000);
    }

}