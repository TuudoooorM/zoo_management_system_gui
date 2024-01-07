package com.project.demo.controllers;

import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class DefaultController {
    private Object[] props = new Object[]{};

    public void navigateToView(ActionEvent actionEvent) {
        navigateToView(actionEvent, props);
    }

    public void navigateToView(ActionEvent actionEvent, Object... props) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            ZooApplication.changeScene((String) clickedButton.getUserData(), props);
        } catch (Exception error) {
            System.err.println("There's been an error changing scene. Received scene path: " + clickedButton.getUserData());
            error.printStackTrace();
            Platform.exit();
        }
    }

    public void navigateToView(String viewPath, Object... props) {
        try {
            ZooApplication.changeScene(viewPath, props);
        } catch (Exception error) {
            System.err.println("There's been an error changing scene. Received scene path: " + viewPath);
            Platform.exit();
        }
    }


    public Object[] getProps() {
        return props;
    }

    public void setProps(Object... receivedProps) {
        if (receivedProps.length > 0)
            this.props = receivedProps;
    }
}