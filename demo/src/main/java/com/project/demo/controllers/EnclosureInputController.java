package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.Utils.Randoms;
import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.Enclosure;
import com.project.demo.Zoo.Zoo;
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


    public static ViewModes getViewMode() {
        return viewMode;
    }

    public static void setViewMode(ViewModes viewMode) {
        EnclosureInputController.viewMode = viewMode;
    }

    private static ViewModes viewMode = ViewModes.INPUT;

    @FXML
    public void initialize() {
        skipButton.setVisible(getViewMode() == ViewModes.SETUP);
    }

    public void navigateToView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            String viewPath = (String) clickedButton.getUserData();
            if (viewPath.contains("animal-input-view")) AnimalInputController.setViewMode(getViewMode());

            // Reset the zoo state so far if the user ever chooses to abort.
            if (viewPath.contains("main-view")) ZooApplication.zoo = new Zoo();

            EnclosureInputController.setViewMode(ViewModes.INPUT);
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

        float width = -1;
        try {
            width = Float.parseFloat(widthInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The width input must receive a whole number, or a decimal number.\n");
        }

        float height = -1;
        try {
            height = Float.parseFloat(heightInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The height input must receive a whole number, or a decimal number.\n");
        }

        float length = -1;
        try {
            length = Float.parseFloat(lengthInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The length input must receive a whole number, or a decimal number.\n");
        }

        if (!errorsMessageBuilder.isEmpty()) {
            errorAlert.setContentText(errorsMessageBuilder.toString());
            errorAlert.show();
            return;
        }

        Enclosure addedEnclosure = ZooApplication.zoo.addEnclosure(speciesInput.getText(), capacity, width, height, length);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        successAlert.setHeaderText("This enclosure's details have been successfully configured. Its ID is: " + addedEnclosure.getId());
        successAlert.setTitle("Enclosure input success");
        successAlert.show();

        if (getViewMode() == ViewModes.INPUT) {
            try {
                ZooApplication.changeScene("zoo-input-view.fxml");
            } catch (Exception error) {
                System.err.println("There's been an error changing scene. Received scene path: zoo-input-view.fxml");
                Platform.exit();
            }
        } else if (getViewMode() == ViewModes.SETUP) {
            speciesInput.clear();
            capacityInput.clear();
            widthInput.clear();
            heightInput.clear();
            lengthInput.clear();
        }
    }
}
