package com.project.demo.controllers;

import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.Zoo;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.io.IOException;

public class DefaultZooInputController extends DefaultController {
    public Button skipButton;

    public void initialize() {
        Platform.runLater(() -> {
            ViewModes viewMode = (ViewModes) super.getProps()[0];
            skipButton.setVisible(viewMode == ViewModes.SETUP);
        });
    }

    @Override
    public void navigateToView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;
        ViewModes viewMode = (ViewModes) super.getProps()[0];

        try {
            String viewPath = (String) clickedButton.getUserData();

            // Reset the zoo state so far if the user ever chooses to abort.
            if (viewMode == ViewModes.SETUP && viewPath.contains("main-view"))
                ZooApplication.zoo = new Zoo();

            ZooApplication.changeScene(viewPath, viewMode);
        } catch (Exception error) {
            System.err.println("There's been an error changing scene. Received scene path: " + clickedButton.getUserData());
            Platform.exit();
        }
    }

    public void goBack(ActionEvent actionEvent) {
        ViewModes viewMode = (ViewModes) super.getProps()[0];
        try {
            // Reset zoo state if the user ever chooses to abort.
            if (viewMode == ViewModes.SETUP) ZooApplication.zoo = new Zoo();

            ZooApplication.changeScene(viewMode == ViewModes.INPUT ? "zoo-input-view.fxml" : viewMode == ViewModes.SETUP ? "main-view.fxml" : "");
        } catch (IOException e) {
            System.err.println("There's been an error going back to the zoo input page: " + e.getMessage());
            Platform.exit();
        }
    }
}
