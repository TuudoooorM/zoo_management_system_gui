package com.project.demo.controllers;

import com.project.demo.Utils.ViewModes;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EnclosureInputController {

    public TextField speciesInput;
    public TextField heightInput;
    public TextField lengthInput;
    public TextField capacityInput;
    public TextField widthInput;
    public Button skipButton;


    private static ViewModes viewMode = ViewModes.INPUT;
    @FXML
    public void initialize() {
        skipButton.setVisible(viewMode == ViewModes.SETUP);
    }

    public static void setMode(ViewModes receivedViewMode) {
        viewMode = receivedViewMode;
    }

    public void navigateToView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            String viewPath = (String) clickedButton.getUserData();
            if (viewPath.contains("animal-input-view")) AnimalInputController.setMode(viewMode);

            EnclosureInputController.setMode(ViewModes.INPUT);
            ZooApplication.changeScene(viewPath);
        } catch (Exception error) {
            System.err.println("There's been an error changing scene. Received scene path: " + clickedButton.getUserData());
            Platform.exit();
        }
    }


    public void submitEnclosureInput(ActionEvent actionEvent) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        errorAlert.setHeaderText("Enclosure input error");
        StringBuilder errorsMessageBuilder = new StringBuilder();

        if (speciesInput.getText().isEmpty())
            errorsMessageBuilder.append("The enclosure must have a species.\n");

        int capacity = -1;
        try {
            capacity = Integer.parseInt(capacityInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The capacity must be a number.\n");
        }

        int width = -1;
        try {
            width = Integer.parseInt(widthInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The width input must receive a number.\n");
        }

        int height = -1;
        try {
            height = Integer.parseInt(heightInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The height input must receive a number.\n");
        }

        int length = -1;
        try {
            length = Integer.parseInt(lengthInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The length input must receive a number.\n");
        }

        if (!errorsMessageBuilder.isEmpty()) {
            errorAlert.setContentText(errorsMessageBuilder.toString());
            errorAlert.show();
            return;
        }

        ZooApplication.zoo.addEnclosure(speciesInput.getText(), capacity, width, height, length);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        successAlert.setHeaderText("This enclosure's details have been successfully configured.");
        successAlert.setTitle("Enclosure input success");
        successAlert.show();

        if (viewMode == ViewModes.INPUT) {
            try {
                ZooApplication.changeScene("zoo-input-view.fxml");
            } catch (Exception error) {
                System.err.println("There's been an error changing scene. Received scene path: zoo-input-view.fxml");
                Platform.exit();
            }
        } else if (viewMode == ViewModes.SETUP) {
            speciesInput.clear();
            capacityInput.clear();
            widthInput.clear();
            heightInput.clear();
            lengthInput.clear();
        }
    }
}
