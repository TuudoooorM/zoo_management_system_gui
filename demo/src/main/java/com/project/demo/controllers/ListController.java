package com.project.demo.controllers;

import com.project.demo.Zoo.Animal;
import com.project.demo.Zoo.Enclosure;
import com.project.demo.Zoo.Zookeeper;
import com.project.demo.ZooApplication;
import com.project.demo.components.ResultComponent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ListController {
    public ChoiceBox<String> listModeInput;
    public VBox listResultsContainer;

    public void navigateToView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            ZooApplication.changeScene((String) clickedButton.getUserData());
        } catch (Exception error) {
            System.err.println("There's been an error changing scene. Received scene path: " + clickedButton.getUserData());
            Platform.exit();
        }
    }


    public void submitListChoiceInput(ActionEvent actionEvent) {
        String listMode = listModeInput.getSelectionModel().getSelectedItem().toLowerCase();
        listResultsContainer.getChildren().clear();

        switch (listMode) {
            case "zookeepers" -> listZookeepers();
            case "animals" -> listAnimals();
            case "enclosures" -> listEnclosures();
        }
    }

    private void listZookeepers() {
        ArrayList<Zookeeper> zookeepers = ZooApplication.zoo.zookeepers;
        if (zookeepers.size() == 0) {
            Label noZookeepersLabel = new Label("There are no zookeepers in this zoo.");
            noZookeepersLabel.getStyleClass().add("nothing-found-label");
            listResultsContainer.getChildren().add(noZookeepersLabel);
            return;
        }

        for (Zookeeper zookeeper : zookeepers) {
            ResultComponent resultComponent = new ResultComponent();
            resultComponent.setText(
                    String.format("%s • %s • %s • %s • Salary: %d • %d month%s worked", zookeeper.getId(), zookeeper.name, zookeeper.getJob(), zookeeper.sex, zookeeper.getSalary(), zookeeper.getWorkedMonths(), zookeeper.getWorkedMonths() != 1 ? "s" : "")
            );

            listResultsContainer.getChildren().add(resultComponent);
        }
    }


    private void listEnclosures() {
        ArrayList<Enclosure> enclosures = ZooApplication.zoo.enclosures;
        if (enclosures.isEmpty()) {
            Label noEnclosuresLabel = new Label("There are no enclosures in this zoo.");
            noEnclosuresLabel.getStyleClass().add("nothing-found-label");
            listResultsContainer.getChildren().add(noEnclosuresLabel);
            return;
        }

        int enclosureIndex = 1;
        for (Enclosure enclosure : enclosures) {
            ResultComponent resultComponent = new ResultComponent();

            resultComponent.setText(
                    String.format("Enclosure %d • %s housed • %d animal%s • %d capacity • %s", enclosureIndex, enclosure.speciesHoused, enclosure.animals.size(), enclosure.animals.size() != 1 ? "s" : "", enclosure.capacity, enclosure.getId())
            );

            listResultsContainer.getChildren().add(resultComponent);
            enclosureIndex++;
        }
    }

    private void listAnimals() {
        ArrayList<Enclosure> enclosures = ZooApplication.zoo.enclosures;
        if (enclosures.isEmpty()) {
            Label noEnclosuresLabel = new Label("There are no enclosures in this zoo.");
            noEnclosuresLabel.getStyleClass().add("nothing-found-label");
            listResultsContainer.getChildren().add(noEnclosuresLabel);
            return;
        }

        int enclosureIndex = 1;
        for (Enclosure enclosure : enclosures) {
            ResultComponent resultComponent = new ResultComponent();
            resultComponent.setText(
                    String.format("Enclosure %d • %s housed • %d animal%s • %d capacity • %s", enclosureIndex, enclosure.speciesHoused, enclosure.animals.size(), enclosure.animals.size() != 1 ? "s" : "", enclosure.capacity, enclosure.getId())
            );

            listResultsContainer.getChildren().add(resultComponent);

            ArrayList<Animal> animals = enclosure.animals;
            if (animals.isEmpty()) {
                Label noAnimalsLabel = new Label("There are no animals in this enclosures.");
                noAnimalsLabel.getStyleClass().add("nothing-found-label");
                listResultsContainer.getChildren().add(noAnimalsLabel);
                enclosureIndex++;

                continue;
            }

            for (Animal animal : animals) {
                ResultComponent animalResultComponent = new ResultComponent();
                animalResultComponent.setText(
                        String.format("%s • %s • %s • %d year%s old • %s", animal.name, animal.species, animal.sex, animal.age, animal.age != 1 ? "s" : "", animal.healthy ? "Healthy" : "Unhealthy")
                );

                listResultsContainer.getChildren().add(animalResultComponent);
                animalResultComponent.getStyleClass().add("result-component--indented");
            }

            enclosureIndex++;
        }
    }
}
