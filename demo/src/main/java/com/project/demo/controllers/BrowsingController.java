package com.project.demo.controllers;

import com.project.demo.Utils.Authenticator;
import com.project.demo.Zoo.Privileges;
import com.project.demo.ZooApplication;
import com.project.demo.components.PrivilegeStatusComponent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class BrowsingController extends DefaultController {
    public Button zooInputButton;
    public Text browsingHeader;
    public PrivilegeStatusComponent privilegeStatus;

    public void initialize() {
        zooInputButton.setDisable(Authenticator.privilege != Privileges.ADMIN);
        browsingHeader.setText(ZooApplication.zoo.name + " > Browsing");
        String employeeDetails = Authenticator.privilege != Privileges.GUEST ? " (Hello, " + Authenticator.employee.getName() + ")" : "";

        privilegeStatus.setPrivilegeLabelText("Privilege: " + Authenticator.privilege + employeeDetails);
    }

    public void exit(ActionEvent ignored) {
        Platform.exit();
    }

}
