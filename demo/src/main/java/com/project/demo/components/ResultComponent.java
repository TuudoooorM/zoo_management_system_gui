package com.project.demo.components;

import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

// https://docs.oracle.com/javafx/2/fxml_get_started/custom_control.htm
public class ResultComponent extends HBox {
    @FXML
    private Label resultLabel;

    public ResultComponent() {
        FXMLLoader resultComponentFXMLLoader = new FXMLLoader(ZooApplication.class.getResource("components/result-component.fxml"));
        resultComponentFXMLLoader.setRoot(this);
        resultComponentFXMLLoader.setController(this);

        try {
            resultComponentFXMLLoader.load();
        } catch (IOException error) {
            System.err.println("There's been an error loading a result component.");
            Platform.exit();
        }
    }

    public String getText() {
        return resultLabel.getText();
    }

    public void setText(String value) {
        resultLabel.setText(value);
    }
}
