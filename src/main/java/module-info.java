module com.example.uetlms {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    requires java.sql;

    opens controller to javafx.fxml;
    exports controller;
}