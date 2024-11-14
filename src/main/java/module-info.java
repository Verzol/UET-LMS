module com.example.uetlms {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;

    opens adminController to javafx.fxml;
    exports adminController;
    opens models.documents to javafx.base;
}