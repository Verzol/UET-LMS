package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import service.ChatbotService; // Dịch vụ để gọi API chatbot

public class ChatBotController {

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


            chatDisplay.appendText("Bot: " + botResponse + "\n");


            userInput.clear();
        }
    }

}
