module com.example.uetlms {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;

    opens com.dashboard.uetlms to javafx.fxml;
    exports com.dashboard.uetlms;
}