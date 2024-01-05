package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.Utils.Authenticator;
import com.project.demo.Utils.Constants;
import com.project.demo.Zoo.Privileges;
import com.project.demo.Zoo.Sex;
import com.project.demo.Zoo.Zookeeper;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;

public class ZookeeperLoginController extends DefaultController {

    public PasswordField zookeeperPasswordInput;
    public TextField zookeeperIDInput;

    public void submitZookeeperLoginDetails(ActionEvent actionEvent) {
        String zookeeperID = zookeeperIDInput.getText();
        String zookeeperPassword = zookeeperPasswordInput.getText();

        Alert errorsAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        errorsAlert.setTitle("Zookeeper password error");
        StringBuilder errorsTextBuilder = new StringBuilder();

        if (zookeeperID.length() < Constants.ID_SIZE)
            errorsTextBuilder.append("The ID is " + Constants.ID_SIZE + " characters long.");

        if (zookeeperPassword.length() < 8)
            errorsTextBuilder.append("The password is at least 8 characters long.");

        if (!errorsTextBuilder.isEmpty()) {
            errorsAlert.setHeaderText(errorsTextBuilder.toString());
            return;
        }

        Pair<Zookeeper, String> foundZookeeperData = ZooDatabaseManager.tryFindZookeeper(zookeeperID);
        Zookeeper foundZookeeper = foundZookeeperData.getKey();
        String errorMessage = foundZookeeperData.getValue();
        if (errorMessage != null) {
            errorsAlert.setHeaderText(errorMessage);
            errorsAlert.show();
            return;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(zookeeperPassword, foundZookeeper.getPassword())) {
            errorsAlert.setHeaderText("The password of this zookeeper is incorrect.");
            errorsAlert.show();
            return;
        }

        Authenticator.privilege = Privileges.ZOOKEEPER;
        Authenticator.employee = foundZookeeper;

        super.navigateToView(actionEvent);
    }
}
