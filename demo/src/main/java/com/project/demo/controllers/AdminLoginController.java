package com.project.demo.controllers;

import com.project.demo.Utils.Authenticator;
import com.project.demo.Zoo.Admin;
import com.project.demo.Zoo.Privileges;
import com.project.demo.Zoo.Sex;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class AdminLoginController extends DefaultController {

    public PasswordField adminPasswordInput;

    public void submitAdminLoginDetails(ActionEvent actionEvent) {
        String adminPassword = adminPasswordInput.getText();
        Alert errorsAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        errorsAlert.setTitle("Admin password error");

        if (adminPassword.length() < 8) {
            errorsAlert.setHeaderText("The password is at least 8 characters long.");
            errorsAlert.show();
            return;
        }

        // hash equals
        if (!adminPassword.equals("TODO1234")) {
            errorsAlert.setHeaderText("Admin password is incorrect.");
            errorsAlert.show();
            return;
        }

        // gets from db the admin details
        Authenticator.privilege = Privileges.ADMIN;
        ZooApplication.zoo.setAdmin("get from db", Sex.male, 1, 2, "password hash from db");
        Authenticator.employee = ZooApplication.zoo.admin;

        try {
            ZooApplication.changeScene("browsing-view.fxml");
        } catch (IOException error) {
            System.err.println("Tried to change scene after logging in as admin to browsing-view.fxml, but received an error.");
            Platform.exit();
        }

    }
}
