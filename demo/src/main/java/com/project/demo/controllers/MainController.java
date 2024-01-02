package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;

public class MainController extends DefaultController {

    public void setUpNewZoo(ActionEvent actionEvent) {
        boolean didClearAllTables = ZooDatabaseManager.clearAllTables();
        if (!didClearAllTables) {
            Alert clearTableError = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            clearTableError.setTitle("Table clear error");
            clearTableError.setHeaderText("There was an error purging the database. Please try again.");
            return;
        }

        try {
            ZooApplication.changeScene("admin-input-view.fxml");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            Platform.exit();
        }
    }
}
