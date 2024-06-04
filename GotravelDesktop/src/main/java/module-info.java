module com.gotravel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.compiler;
    requires static lombok;
    requires jdk.httpserver;


    opens com.gotravel to javafx.fxml;
    exports com.gotravel;
    exports com.gotravel.Controller;
    opens com.gotravel.Controller to javafx.fxml;
    exports com.gotravel.Utils;
    opens com.gotravel.Utils to javafx.fxml;
    exports com.gotravel.Model to com.google.gson;
    opens com.gotravel.Model to com.google.gson;
}