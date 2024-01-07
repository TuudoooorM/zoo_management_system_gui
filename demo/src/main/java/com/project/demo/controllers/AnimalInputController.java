package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import com.project.demo.Utils.Authenticator;
import com.project.demo.Utils.Constants;
import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.*;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.*;

public class AnimalInputController extends DefaultController {
    public TextField nameInput;
    public TextField speciesInput;
    public ChoiceBox<String> sexChoiceInput;
    public TextField ageInput;
    public CheckBox healthinessInput;

    public Button finishSetupButton;

    public AnchorPane inputsContainer;
    public Button submitButton;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            ViewModes viewMode = (ViewModes) super.getProps()[0];
            finishSetupButton.setVisible(viewMode == ViewModes.SETUP);
        });
    }

    @Override
    public void navigateToView(ActionEvent actionEvent) {
        ViewModes viewMode = (ViewModes) super.getProps()[0];

        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            String viewPath = (String) clickedButton.getUserData();

            // Reset the zoo state so far if the user ever chooses to abort.
            if (viewMode == ViewModes.SETUP)
                ZooApplication.zoo = new Zoo();

            ZooApplication.changeScene(viewPath);
        } catch (Exception error) {
            System.err.println("There's been an error changing scene. Received scene path: " + clickedButton.getUserData());
            Platform.exit();
        }
    }


    public void submitAnimalInput(ActionEvent actionEvent) {
        ViewModes viewMode = (ViewModes) super.getProps()[0];

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

        String name = nameInput.getText();
        String species = speciesInput.getText();
        Sex sex = sexChoiceInput.getSelectionModel().getSelectedIndex() == -1 ? Sex.male : Sex.female;
        boolean healthy = healthinessInput.isSelected();

        try {
            ZooApplication.zoo.addAnimal(name, species, sex, age, healthy);
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

        if (viewMode == ViewModes.INPUT) {
            // No need to check for null because zoo.addAnimal succeeded, if we get here.
            Enclosure enclosureHousingThisSpecies = ZooApplication.zoo.findEnclosureBySpecies(species);
            boolean didAddAnimalInDB = ZooDatabaseManager.tryQueryPreparedStatement(
                    "INSERT INTO animals (enclosure_id, name, species, age, sex, healthy)" +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Integer.parseInt(enclosureHousingThisSpecies.getId()),
                    name,
                    species,
                    age,
                    sex.ordinal(),
                    healthy
            );

            if (!didAddAnimalInDB) {
                Alert errorAddingAnimalInDBAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                errorAddingAnimalInDBAlert.setTitle("Error adding animal to DB");
                errorAddingAnimalInDBAlert.setHeaderText("There's been an error adding this animal to the database. Please try again.");
                errorAddingAnimalInDBAlert.show();
                ZooApplication.zoo.removeAnimal(enclosureHousingThisSpecies, new Animal(name, species, sex, age, healthy));
                return;
            }

            successAlert.show();
            super.navigateToView("zoo-input-view.fxml");
        } else if (viewMode == ViewModes.SETUP) {
            successAlert.show();

            nameInput.clear();
            speciesInput.clear();
            ageInput.clear();
            healthinessInput.setSelected(false);
        }
    }

    public void finishSetup(ActionEvent actionEvent) {
        inputsContainer.getChildren().clear();
        submitButton.setDisable(true);
        finishSetupButton.setDisable(true);

        Label addingEverythingToDatabaseLabel = new Label("Adding everything to the database...");
        addingEverythingToDatabaseLabel.getStyleClass().add("generic-centered-label");
        inputsContainer.getChildren().add(addingEverythingToDatabaseLabel);

        boolean didAddEverythingToDB =
                ZooDatabaseManager.tryInsertBatchEnclosuresAndAnimals(ZooApplication.zoo) &&
                        ZooDatabaseManager.tryInsertBatchZookeepers(ZooApplication.zoo);

        if (!didAddEverythingToDB) {
            Alert dbInsertionErrorAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            dbInsertionErrorAlert.setTitle("DB Update error");
            dbInsertionErrorAlert.setHeaderText("There's been an error adding something to the database. Aborting...");
            dbInsertionErrorAlert.show();
            return;
        }

        Authenticator.privilege = Privileges.ADMIN;
        Authenticator.employee = ZooApplication.zoo.admin;
        super.navigateToView(actionEvent);
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
