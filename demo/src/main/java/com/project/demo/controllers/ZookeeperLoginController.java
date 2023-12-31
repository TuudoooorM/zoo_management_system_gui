package com.project.demo.controllers;

import com.project.demo.Utils.Authenticator;
import com.project.demo.Zoo.Privileges;
import com.project.demo.Zoo.Sex;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ZookeeperLoginController extends DefaultController {

    public PasswordField zookeeperPasswordInput;
    public TextField zookeeperIDInput;
    public PasswordField zookeeperIDPassword;

    public void submitZookeeperLoginDetails(ActionEvent actionEvent) {
        String zookeeperID = zookeeperIDInput.getText();
        String zookeeperPassword = zookeeperPasswordInput.getText();

        Alert errorsAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        errorsAlert.setTitle("Zookeeper password error");
        StringBuilder errorsTextBuilder = new StringBuilder();

        if (zookeeperID.length() < 4)
            errorsTextBuilder.append("The ID is 4 characters long.");

        if (zookeeperPassword.length() < 8)
            errorsTextBuilder.append("The password is at least 8 characters long.");

        if (!errorsTextBuilder.isEmpty()) {
            errorsAlert.setHeaderText(errorsTextBuilder.toString());
            return;
        }

        // from db
        if (!zookeeperID.equals("1234")) {
            errorsAlert.setHeaderText("There's no zookeeper with this ID.");
            errorsAlert.show();
            return;
        }

        // hash equals
        if (!zookeeperPassword.equals("TODO1234")) {
            errorsAlert.setHeaderText("Zookeeper password is incorrect.");
            errorsAlert.show();
            return;
        }

        // gets from db the zookeeper details
        Authenticator.privilege = Privileges.ZOOKEEPER;
        Authenticator.employee = ZooApplication.zoo.addZookeeper("Name from db", "Job from db", Sex.male, 1, 2, "password from db");

        try {
            ZooApplication.changeScene("browsing-view.fxml");
        } catch (IOException error) {
            System.err.println("Tried to change scene after logging in as zookeeper to browsing-view.fxml, but received an error.");
            Platform.exit();
        }
    }
}
