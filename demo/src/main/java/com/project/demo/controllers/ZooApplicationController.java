package com.project.demo.controllers;

import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.io.IOException;


public class ZooApplicationController {
    @FXML
    public Text listViewText;

    public void navigateToLoadAsView(ActionEvent ignored) {
        try {
            ZooApplication.changeScene("load-as-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exit(ActionEvent ignored) {
        Platform.exit();
    }

    public void navigateToView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            ZooApplication.changeScene((String) clickedButton.getUserData());
        } catch (Exception error) {
            System.err.println("There's been an error changing scene. Received scene path: " + clickedButton.getUserData());
            Platform.exit();
        }
    }

    public void navigateToListView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        String listViewType = (String) clickedButton.getUserData();
        // TODO
    }
}