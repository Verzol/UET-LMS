package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import service.ChatbotService;

public class ChatBotController {

    @FXML
    public Button sendButton;

    @FXML
    private TextArea chatDisplay;

    @FXML
    private TextField userInput;

    private final ChatbotService chatBotService = new ChatbotService();

    @FXML
    private void handleSendMessage() throws Exception {
        String userMessage = userInput.getText().trim();

        if (!userMessage.isEmpty()) {
            chatDisplay.appendText("You: " + userMessage + "\n");

            String botResponse = chatBotService.getChatbotResponse(userMessage);

            displayBotResponse("Bot: " +  botResponse);

            userInput.clear();
        }
    }

    private void displayBotResponse(String response) {
        final StringBuilder botMessage = new StringBuilder(response);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(20), event -> {
            if (botMessage.length() > 0) {
                chatDisplay.appendText(String.valueOf(botMessage.charAt(0)));
                botMessage.deleteCharAt(0);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    @FXML
    private void initialize() {
        userInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    handleSendMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
