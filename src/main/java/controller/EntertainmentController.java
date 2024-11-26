package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class EntertainmentController {

    public void entertainment(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Chọn chế độ giải trí");
        alert.setHeaderText("Bạn muốn tham gia trò chơi nào?");
        alert.setContentText("Chọn một trong hai:");

        ButtonType buttonVocabularyGuessing = new ButtonType("VocabularyGuessing");
        ButtonType buttonLiteraryQuest = new ButtonType("LiteraryQuest");
        ButtonType buttonCancel = new ButtonType("Hủy");

        alert.getButtonTypes().setAll(buttonVocabularyGuessing, buttonLiteraryQuest, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == buttonVocabularyGuessing) {
                switchToVocabularyGuessing(event);
            } else if (result.get() == buttonLiteraryQuest) {
                switchToLiteraryQuest(event);
            }
        }
    }
    public void switchToVocabularyGuessing(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/VocabularyGuessing.fxml"));
            Parent quizGameRoot = loader.load();


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(quizGameRoot));
            stage.setTitle("Vocabulary Gussing");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void switchToLiteraryQuest(ActionEvent event) {
        try {
            // Tải giao diện NewGame
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/LiteraryQuest.fxml"));
            Parent newGameRoot = loader.load();

            // Mở cửa sổ mới
            Stage stage = new Stage();
            stage.setTitle("Literary Quest");
            stage.setScene(new Scene(newGameRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}