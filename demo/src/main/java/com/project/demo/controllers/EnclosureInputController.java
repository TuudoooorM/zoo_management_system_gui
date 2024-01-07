package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.Enclosure;
import com.project.demo.Zoo.Zoo;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EnclosureInputController extends DefaultZooInputController {
    public TextField speciesInput;
    public TextField heightInput;
    public TextField lengthInput;
    public TextField capacityInput;
    public TextField widthInput;

    public void submitEnclosureInput(ActionEvent actionEvent) {
        ViewModes viewMode = (ViewModes) super.getProps()[0];

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

        if (viewMode == ViewModes.INPUT) {
            boolean didAddEnclosureToDB = ZooDatabaseManager.tryQueryPreparedStatement(
                    "INSERT INTO enclosures (id, species, capacity, width, height, length)" +
                            " VALUES (?, ?, ?, ?, ?, ?)",
                    Integer.parseInt(addedEnclosure.getId()),
                    addedEnclosure.speciesHoused,
                    addedEnclosure.capacity,
                    addedEnclosure.getWidth(),
                    addedEnclosure.getHeight(),
                    addedEnclosure.getLength()
            );

            if (!didAddEnclosureToDB) {
                Alert errorAddingEnclosureToDBALert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                errorAddingEnclosureToDBALert.setTitle("Error adding enclosure to DB");
                errorAddingEnclosureToDBALert.setHeaderText("There's been an error adding this enclosure to the database. Please try again.");
                errorAddingEnclosureToDBALert.show();
                ZooApplication.zoo.removeEnclosure(addedEnclosure);
                return;
            }

            successAlert.show();
            super.navigateToView("zoo-input-view.fxml");
        } else if (viewMode == ViewModes.SETUP) {
            successAlert.show();
            speciesInput.clear();
            capacityInput.clear();
            widthInput.clear();
            heightInput.clear();
            lengthInput.clear();
        }
    }
}
