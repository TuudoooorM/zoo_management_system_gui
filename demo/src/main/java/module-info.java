module com.project.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires spring.security.core;
    requires spring.security.crypto;
    requires java.sql;
    requires waffle.jna;

    opens com.project.demo to javafx.fxml;
    exports com.project.demo;
    exports com.project.demo.controllers;
    opens com.project.demo.controllers to javafx.fxml;
    exports com.project.demo.components;
    opens com.project.demo.components to javafx.fxml;
}