package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;

public class LoadAsController extends DefaultController {

    public void loadDBAndNavigateToView(ActionEvent actionEvent) {
        ZooApplication.zoo = ZooDatabaseManager.load();
        if (ZooApplication.zoo == null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            errorAlert.setTitle("DB Load error");
            errorAlert.setHeaderText("Could not load the database contents because of an error. Please try again.");
            errorAlert.show();
            return;
        }

        try {
            ZooApplication.changeScene("browsing-view.fxml");
        } catch (IOException error) {
            System.err.println(error.getMessage());
            Platform.exit();
        }
    }
}
