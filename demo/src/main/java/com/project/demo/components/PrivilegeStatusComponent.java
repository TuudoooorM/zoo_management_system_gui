package com.project.demo.components;

import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PrivilegeStatusComponent extends HBox {
    @FXML
    private Label privilegeLabel;

    public PrivilegeStatusComponent() {
        FXMLLoader privilegeComponentFXMLLoader = new FXMLLoader(ZooApplication.class.getResource("components/privilege-status-component.fxml"));
        privilegeComponentFXMLLoader.setRoot(this);
        privilegeComponentFXMLLoader.setController(this);

        try {
            privilegeComponentFXMLLoader.load();
        } catch (IOException error) {
            System.err.println("There's been an error loading the privilege status component.");
            Platform.exit();
        }
    }

    public void setPrivilegeLabelText(String privilege) {
        privilegeLabel.setText(privilege);
    }

}
