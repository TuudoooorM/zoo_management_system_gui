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

import java.sql.*;

public class AnimalInputController extends DefaultController {
    public TextField nameInput;
    public TextField speciesInput;
    public ChoiceBox<String> sexChoiceInput;
    public TextField ageInput;
    public CheckBox healthinessInput;

    public Button finishSetupButton;


    private static ViewModes viewMode = ViewModes.INPUT;
    public AnchorPane inputsContainer;
    public Button submitButton;

    public static void setViewMode(ViewModes viewMode) {
        AnimalInputController.viewMode = viewMode;
    }

    public static ViewModes getViewMode() {
        return viewMode;
    }

    @FXML
    public void initialize() {
        finishSetupButton.setVisible(getViewMode() == ViewModes.SETUP);
    }

    @Override
    public void navigateToView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            // TODO: add functionality for this, EnclosureInputController and ZookeeperInputController on "INPUT" view
            // TODO: fix routing when on "INPUT" view
            String viewPath = (String) clickedButton.getUserData();

            // Reset the zoo state so far if the user ever chooses to abort.
            if (getViewMode() == ViewModes.SETUP && viewPath.contains("main-view"))
                ZooApplication.zoo = new Zoo();

            AnimalInputController.setViewMode(ViewModes.INPUT);

            ZooApplication.changeScene(viewPath);
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

        if (getViewMode() == ViewModes.INPUT) {
            // No need to check for null because zoo.addAnimal succeeded, if we get here.
            Enclosure enclosureHousingThisSpecies = ZooApplication.zoo.findEnclosureBySpecies(species);
            boolean didAddAnimalInDB = ZooDatabaseManager.tryQueryPreparedStatement(
                    "INSERT INTO animals VALUES (?, ?, ?, ?, ?, ?)",
                    Integer.parseInt(enclosureHousingThisSpecies.getId()),
                    name,
                    species,
                    age,
                    sex,
                    healthy
            );

            if (!didAddAnimalInDB) {
                ZooApplication.zoo.removeAnimal(enclosureHousingThisSpecies, new Animal(name, species, sex, age, healthy));

                Alert errorAddingAnimalInDBAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                errorAddingAnimalInDBAlert.setTitle("Error adding animal to DB");
                errorAddingAnimalInDBAlert.setHeaderText("There's been an error adding this animal to the database. Please try again.");
                errorAddingAnimalInDBAlert.show();
                return;
            }

            try {
                ZooApplication.changeScene("zoo-input-view.fxml");
            } catch (Exception error) {
                System.err.println("There's been an error changing scene. Received scene path: zoo-input-view.fxml");
                Platform.exit();
            }
        } else if (getViewMode() == ViewModes.SETUP) {
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
        AnimalInputController.setViewMode(ViewModes.INPUT);
        super.navigateToView(actionEvent);
    }
}
