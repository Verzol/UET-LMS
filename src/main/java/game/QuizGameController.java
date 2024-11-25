package game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

import java.util.*;

public class QuizGameController {

    @FXML
    private Label questionLabel;

    @FXML
    private Button option1, option2, option3, option4;

    @FXML
    private TextField keywordTextField;

    @FXML
    private String correctAnswer;

    private boolean isFindBooksByAuthorMode = true;

    @FXML
    public void initialize() {

    }


    @FXML
    public void searchBooks(ActionEvent event) {
        String keyword = keywordTextField.getText().trim();

        if (keyword.isEmpty()) {
            questionLabel.setText("Please enter a valid input.");
            return;
        }

        if (isFindBooksByAuthorMode) {

            List<String> books = service.QuizGameAPI.fetchBooksByAuthor(keyword);

            if (books.isEmpty()) {
                questionLabel.setText("No books found for the author: " + keyword);
                return;
            }

            loadQuestion(books, keyword);
        } else {

            List<String> authors = service.QuizGameAPI.fetchAuthorsByBook(keyword);

            if (authors.isEmpty()) {
                questionLabel.setText("No authors found for the book: " + keyword);
                return;
            }

            loadAuthorQuestion(authors, keyword);
        }
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

    private void loadAuthorQuestion(List<String> authors, String bookTitle) {
        if (authors == null || authors.isEmpty()) {
            questionLabel.setText("No author found for this book.");
            return;
        }

        String correctAuthor = authors.get(0);


        List<String> incorrectAuthors = getFakeAuthors(3, correctAuthor);


        List<String> allOptions = new ArrayList<>(incorrectAuthors);
        allOptions.add(correctAuthor);


        Collections.shuffle(allOptions);


        option1.setText(allOptions.get(0));
        option2.setText(allOptions.get(1));
        option3.setText(allOptions.get(2));
        option4.setText(allOptions.get(3));


        correctAnswer = correctAuthor;


        questionLabel.setText("Who is the author of the book: " + bookTitle + "?");
    }

    private List<String> getFakeAuthors(int count, String correctAuthor) {
        List<String> fakeAuthors = new ArrayList<>();


        List<String> books = service.QuizGameAPI.fetchBooks("novel");

        for (String book : books) {
            List<String> authors = service.QuizGameAPI.fetchAuthorsByBook(book);

            for (String author : authors) {
                if (!author.equals(correctAuthor) && !fakeAuthors.contains(author)) {
                    fakeAuthors.add(author);
                }

                if (fakeAuthors.size() >= count) {
                    break;
                }
            }
            if (fakeAuthors.size() >= count) {
                break;
            }
        }


        while (fakeAuthors.size() < count) {
            fakeAuthors.add("Fake Author " + (fakeAuthors.size() + 1));
        }

        return fakeAuthors.subList(0, count);
    }


    private List<String> getIncorrectAuthors(String correctAuthor, int count) {
        List<String> allBooks = service.QuizGameAPI.fetchBooks("novel");
        List<String> incorrectAuthors = new ArrayList<>();

        for (String book : allBooks) {

            String[] parts = book.split(", Authors: ");
            if (parts.length > 1) {
                String author = parts[1].trim(); // Lấy phần tác giả
                if (!author.equals(correctAuthor)) {
                    incorrectAuthors.add(author);
                    if (incorrectAuthors.size() >= count) break;
                }
            }
        }

        return incorrectAuthors.isEmpty() ? Collections.singletonList("Unknown Author") : incorrectAuthors;
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


    @FXML
    private ToggleButton modeToggle;

    @FXML
    private void switchMode(ActionEvent event) {
        isFindBooksByAuthorMode = !isFindBooksByAuthorMode; // Chuyển trạng thái

        if (isFindBooksByAuthorMode) {
            modeToggle.setText("Mode: Find Books by Author");

        } else {
            modeToggle.setText("Mode: Find Authors by Book");

        }
    }


}
