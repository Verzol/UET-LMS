module com.example.uetlms {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires org.json;
    requires java.net.http;
    requires mysql.connector.j;
    requires java.json;
    requires com.google.gson;

    opens adminController to javafx.fxml;
    exports adminController;
    opens models.documents to javafx.base;
    exports controller;
    opens controller to javafx.fxml;
    exports service;
    opens service to javafx.fxml;
    opens game to javafx.fxml;

    requires com.fasterxml.jackson.databind;
}
