package com.dashboard.uetlms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Dashboard extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("LibraryDashboard.fxml"));
        primaryStage.setTitle("UET Library Management");
        primaryStage.setScene(new Scene(root, 1200, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
