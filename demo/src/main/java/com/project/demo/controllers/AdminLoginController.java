package com.project.demo.controllers;

import com.project.demo.Utils.Authenticator;
import com.project.demo.Zoo.Privileges;
import com.project.demo.ZooApplication;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(adminPassword, ZooApplication.zoo.admin.getPassword())) {
            errorsAlert.setHeaderText("Admin password is incorrect.");
            errorsAlert.show();
            return;
        }

        Authenticator.privilege = Privileges.ADMIN;
        Authenticator.employee = ZooApplication.zoo.admin;

        super.navigateToView(actionEvent);
    }
}
