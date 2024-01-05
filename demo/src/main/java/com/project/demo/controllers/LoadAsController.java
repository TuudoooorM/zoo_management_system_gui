package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.Zoo.Zoo;
import com.project.demo.ZooApplication;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;

public class LoadAsController extends DefaultController {

    public void initialize() {
        Pair<Zoo, String> zooData = ZooDatabaseManager.load();

        if (zooData.getValue() != null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            errorAlert.setTitle("DB Load error");
            errorAlert.setHeaderText(String.format(
                    "There's been an internal error whilst loading all the zoo data: (%s)",
                    zooData.getValue()
            ));

            errorAlert.show();
            return;
        }

        ZooApplication.zoo = zooData.getKey();
    }
}
