module com.project.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires spring.security.core;
    requires spring.security.crypto;
    requires jakarta.validation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.hibernate.validator;

    opens com.project.demo to javafx.fxml;
    exports com.project.demo;
    exports com.project.demo.controllers;
    opens com.project.demo.controllers to javafx.fxml;
}