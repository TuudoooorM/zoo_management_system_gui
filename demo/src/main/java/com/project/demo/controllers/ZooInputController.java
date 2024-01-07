package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.Animal;
import com.project.demo.Zoo.Enclosure;
import com.project.demo.Zoo.Zookeeper;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class ZooInputController extends DefaultController {
    public VBox inputsRemovalContainer;

    public void navigateToAddInputView(ActionEvent actionEvent) {
        super.navigateToView(actionEvent, ViewModes.INPUT);
    }

    public void removeZookeeper(ActionEvent actionEvent) {
        inputsRemovalContainer.getChildren().clear();

        TextField zookeeperIDInput = new TextField();
        zookeeperIDInput.setId("zookeeper-id-input-removal");
        zookeeperIDInput.setPromptText("Enter the ID of the zookeeper");
        HBox.setHgrow(zookeeperIDInput, Priority.ALWAYS);

        zookeeperIDInput.setOnAction((ignored) -> {
            Zookeeper foundZookeeper = ZooApplication.zoo.findZookeeper(zookeeperIDInput.getText());
            boolean didZookeeperExistInZoo = ZooApplication.zoo.removeZookeeper(foundZookeeper);

            if (!didZookeeperExistInZoo) {
                Alert noZookeeperFoundAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                noZookeeperFoundAlert.setTitle("No zookeeper with that ID");
                noZookeeperFoundAlert.setHeaderText("There's no zookeeper with that ID.");
                noZookeeperFoundAlert.show();
                return;
            }

            boolean didDeleteZookeeperFromDB =
                    ZooDatabaseManager.tryQueryPreparedStatement(
                            "DELETE FROM zookeepers WHERE id = ?",
                            Integer.parseInt(foundZookeeper.getId())
                    );

            if (!didDeleteZookeeperFromDB) {
                Alert zookeeperErrorDBDeleteAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                zookeeperErrorDBDeleteAlert.setTitle("Zookeeper db deletion error");
                zookeeperErrorDBDeleteAlert.setHeaderText("There's been an error deleting this zookeeper from the database.");
                zookeeperErrorDBDeleteAlert.show();

                ZooApplication.zoo.addZookeeper(foundZookeeper);
                return;
            }

            Alert zookeeperDeleteSuccessAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            zookeeperDeleteSuccessAlert.setTitle("Zookeeper deletion successful");
            zookeeperDeleteSuccessAlert.setHeaderText("The zookeeper has been successfully deleted.");
            zookeeperDeleteSuccessAlert.show();
            inputsRemovalContainer.getChildren().remove(zookeeperIDInput);
        });

        inputsRemovalContainer.getChildren().add(zookeeperIDInput);
    }

    public void removeEnclosure(ActionEvent actionEvent) {
        inputsRemovalContainer.getChildren().clear();

        TextField enclosureIDInput = new TextField();
        enclosureIDInput.setId("enclosure-id-input-removal");
        enclosureIDInput.setPromptText("Enter the ID of the enclosure");
        HBox.setHgrow(enclosureIDInput, Priority.ALWAYS);

        enclosureIDInput.setOnAction((ignored) -> {
            Enclosure foundEnclosure = ZooApplication.zoo.findEnclosure(enclosureIDInput.getText());
            boolean didEnclosureExistInZoo = ZooApplication.zoo.removeEnclosure(foundEnclosure);
            
            if (!didEnclosureExistInZoo) {
                Alert noEnclosureFoundAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                noEnclosureFoundAlert.setTitle("No enclosure with that ID");
                noEnclosureFoundAlert.setHeaderText("There's no enclosure with that ID.");
                noEnclosureFoundAlert.show();
                return;
            }

            boolean didDeleteEnclosureFromDB =
                    ZooDatabaseManager.tryQueryPreparedStatement(
                            "DELETE FROM enclosures WHERE id = ?",
                            Integer.parseInt(foundEnclosure.getId()));

            if (!didDeleteEnclosureFromDB) {
                Alert zookeeperErrorDBDeleteAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                zookeeperErrorDBDeleteAlert.setTitle("Enclosure db deletion error");
                zookeeperErrorDBDeleteAlert.setHeaderText("There's been an error deleting this enclosure from the database.");
                zookeeperErrorDBDeleteAlert.show();

                ZooApplication.zoo.addEnclosure(foundEnclosure);
                return;
            }

            Alert enclosureDeletionSuccessfullAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            enclosureDeletionSuccessfullAlert.setTitle("Enclosure deletion successful");
            enclosureDeletionSuccessfullAlert.setHeaderText("The enclosure has been successfully deleted.");
            enclosureDeletionSuccessfullAlert.show();
            inputsRemovalContainer.getChildren().remove(enclosureIDInput);
        });

        inputsRemovalContainer.getChildren().add(enclosureIDInput);
    }

    public void removeAnimal(ActionEvent actionEvent) {
        inputsRemovalContainer.getChildren().clear();

        TextField animalSpeciesInput = new TextField();
        animalSpeciesInput.setId("animal-species-input-removal");
        animalSpeciesInput.setPromptText("Enter the species of the animal");
        HBox.setHgrow(animalSpeciesInput, Priority.ALWAYS);

        TextField animalNameInput = new TextField();
        animalNameInput.setId("animal-name-input-removal");
        animalNameInput.setPromptText("Enter the name of the animal, then press Enter here");
        HBox.setHgrow(animalNameInput, Priority.ALWAYS);

        animalNameInput.setOnAction((ignored) -> {
            Pair<Animal, Enclosure> foundAnimalInEnclosure = ZooApplication.zoo.findAnimalInEnclosure(
                    animalSpeciesInput.getText(),
                    animalNameInput.getText()
            );

            boolean didAnimalExistInZoo = ZooApplication.zoo.removeAnimal(
                    foundAnimalInEnclosure.getValue(),
                    foundAnimalInEnclosure.getKey()
            );

            if (!didAnimalExistInZoo) {
                Alert noAnimalFoundAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                noAnimalFoundAlert.setTitle("No animal found");
                noAnimalFoundAlert.setHeaderText("There's no animal with that species and that name.");
                noAnimalFoundAlert.show();
                return;
            }

            boolean didDeleteAnimalFromDB = ZooDatabaseManager.tryQueryPreparedStatement(
                    "DELETE FROM animals WHERE name = ? AND species = ?",
                    foundAnimalInEnclosure.getKey().name,
                    foundAnimalInEnclosure.getKey().species
            );

            if (!didDeleteAnimalFromDB) {
                Alert animalDBDeleteErrorAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                animalDBDeleteErrorAlert.setTitle("Animal db deletion error");
                animalDBDeleteErrorAlert.setHeaderText("There's been an error deleting this animal from the database.");
                animalDBDeleteErrorAlert.show();

                try {
                    foundAnimalInEnclosure.getValue().addAnimal(foundAnimalInEnclosure.getKey());
                } catch (EnclosureCapacityExceededException e) {
                    // Never throws because removeAnimal increases the capacity, and addAnimal decreases it.
                }
                return;
            }

            Alert animalDeleteSuccess = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
            animalDeleteSuccess.setTitle("Animal deletion successful");
            animalDeleteSuccess.setHeaderText("The animal has been successfully deleted.");
            animalDeleteSuccess.show();

            inputsRemovalContainer.getChildren().remove(animalSpeciesInput);
            inputsRemovalContainer.getChildren().remove(animalNameInput);
        });

        inputsRemovalContainer.getChildren().add(animalSpeciesInput);
        inputsRemovalContainer.getChildren().add(animalNameInput);
    }
}
