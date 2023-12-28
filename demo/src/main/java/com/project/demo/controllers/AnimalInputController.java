package com.project.demo.controllers;

import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import com.project.demo.Utils.Authenticator;
import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.*;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AnimalInputController {
    public TextField nameInput;
    public TextField speciesInput;
    public ChoiceBox<?> sexChoiceInput;
    public TextField ageInput;
    public CheckBox healthinessInput;

    private static ViewModes viewMode = ViewModes.INPUT;
    public Button finishSetupButton;

    @FXML
    public void initialize() {
        finishSetupButton.setVisible(viewMode == ViewModes.SETUP);
    }

    public static void setMode(ViewModes receivedViewMode) {
        viewMode = receivedViewMode;
    }

    public void navigateToView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            AnimalInputController.setMode(ViewModes.INPUT);
            ZooApplication.changeScene((String) clickedButton.getUserData());
        } catch (Exception error) {
            System.err.println("There's been an error changing scene. Received scene path: " + clickedButton.getUserData());
            Platform.exit();
        }
    }


    public void submitAnimalInput(ActionEvent actionEvent) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        errorAlert.setHeaderText("Animal input error");
        StringBuilder errorsMessageBuilder = new StringBuilder();

        if (nameInput.getText().length() < 2)
            errorsMessageBuilder.append("The animal's name is too short.\n");

        if (speciesInput.getText().isEmpty())
            errorsMessageBuilder.append("The animal must have a species.\n");

        int age = 0;
        try {
            age = Integer.parseInt(ageInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The animal's age must be a number.\n");
        }

        if (age < 0)
            errorsMessageBuilder.append("The age of the animal must be positive.");

        try {
            ZooApplication.zoo.addAnimal(nameInput.getText(), speciesInput.getText(), sexChoiceInput.getSelectionModel().getSelectedIndex() == -1 ? Sex.male : Sex.female, age, healthinessInput.isSelected());
        } catch (EnclosureCapacityExceededException | MissingEnclosureException e) {
            errorsMessageBuilder.append(e.getMessage());
        }

        if (!errorsMessageBuilder.isEmpty()) {
            errorAlert.setContentText(errorsMessageBuilder.toString());
            errorAlert.show();
            return;
        }

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        successAlert.setHeaderText("This animal's details have been successfully configured.");
        successAlert.setTitle("Animal input success");
        successAlert.show();

        if (viewMode == ViewModes.INPUT) {
            try {
                ZooApplication.changeScene("zoo-input-view.fxml");
            } catch (Exception error) {
                System.err.println("There's been an error changing scene. Received scene path: zoo-input-view.fxml");
                Platform.exit();
            }
        } else if (viewMode == ViewModes.SETUP) {
            nameInput.clear();
            speciesInput.clear();
            ageInput.clear();
            healthinessInput.setSelected(false);
        }
    }

    public void finishSetup(ActionEvent actionEvent) {
        // TODO: save zooInputData to db
        System.out.println("Admin: ");
        Admin admin = ZooApplication.zoo.admin;
        System.out.printf("%s %s %s %d %d\n", admin.name, admin.sex, admin.password, admin.getSalary(), admin.getWorkedMonths());

        System.out.println("Zookeepers: ");
        ZooApplication.zoo.listZookeepers();
        System.out.println("Enclosures: ");
        ZooApplication.zoo.listEnclosures();
        System.out.println("Animals: ");
        ZooApplication.zoo.listAnimals();

        try {
            Authenticator.privilege = Privileges.ADMIN;
            Authenticator.employee = ZooApplication.zoo.admin;
            AnimalInputController.setMode(ViewModes.INPUT);
            ZooApplication.changeScene("browsing-view.fxml");
        } catch (Exception error) {
            System.err.println("There's been an error changing scene to browsing-view.fxml");
            Platform.exit();
        }
    }
}
