package game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.*;

public class QuizGameController {

    @FXML
    private Label questionLabel;

    @FXML
    private Button option1, option2, option3, option4;

    @FXML
    private TextField keywordTextField;

    private String correctAnswer;

    @FXML
    public void initialize() {
    }

    @FXML
    public void searchBooks(ActionEvent event) {
        String author = keywordTextField.getText();

        List<String> books = service.QuizGameAPI.fetchBooksByAuthor(author);

        if (books.isEmpty()) {
            questionLabel.setText("No books found for the author: " + author);
            return;
        }


        loadQuestion(books, author);
    }

    private void loadQuestion(List<String> books, String author) {
        Random random = new Random();

        String correctBook = books.get(random.nextInt(books.size()));

        List<String> incorrectBooks = getBooksNotByAuthor(author, 3);

        List<String> allOptions = new ArrayList<>();
        allOptions.add(correctBook);
        allOptions.addAll(incorrectBooks);
        Collections.shuffle(allOptions);

        option1.setText(allOptions.get(0));
        option2.setText(allOptions.get(1));
        option3.setText(allOptions.get(2));
        option4.setText(allOptions.get(3));

        correctAnswer = correctBook;
        questionLabel.setText("Which of these books is written by " + author + "?");
    }

    @FXML
    private void checkAnswer(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if (clickedButton.getText().equals(correctAnswer)) {
            questionLabel.setText("Correct!");
        } else {
            questionLabel.setText("Wrong! Try again.");
        }
    }

    private List<String> getBooksNotByAuthor(String author, int count) {
        List<String> allBooks = service.QuizGameAPI.fetchBooks("novel");
        List<String> filteredBooks = new ArrayList<>();

        for (String book : allBooks) {
            if (!book.contains("Authors: " + author)) {
                filteredBooks.add(book);
                if (filteredBooks.size() >= count) break;
            }
        }

        return filteredBooks.isEmpty() ? Collections.singletonList("No alternative books found") : filteredBooks;
    }
}
