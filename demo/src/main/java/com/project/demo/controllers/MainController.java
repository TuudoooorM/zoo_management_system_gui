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
        Alert clearError = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        clearError.setTitle("Clear error");
        boolean didClearConfigFile = ZooDatabaseManager.clearConfigFile();
        boolean didClearAllTables = ZooDatabaseManager.clearAllTables();

        if (!didClearConfigFile) {
            clearError.setHeaderText("There was an error clearing the config file. Please try again.");
            return;
        }

        if (!didClearAllTables) {
            clearError.setHeaderText("There was an error purging the database. Please try again.");
            return;
        }

       super.navigateToView(actionEvent);
    }
}
