package com.project.demo.controllers;

import com.project.demo.Zoo.Animal;
import com.project.demo.ZooApplication;
import com.project.demo.components.ResultComponent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class SearchController extends DefaultController {
    public TextField searchInput;
    public VBox searchResultsContainer;

    public void handleSearch(ActionEvent actionEvent) {
        String searchInputText = searchInput.getText();
        if (searchInputText.trim().isEmpty()) {
            Alert badInputAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            badInputAlert.setTitle("Bad search input");
            badInputAlert.setHeaderText("You must input something.");
            badInputAlert.show();
        }
        searchResultsContainer.getChildren().clear();

        ArrayList<Animal> foundAnimals = ZooApplication.zoo.searchBySpecies(searchInputText);
        if (foundAnimals == null) {
            Label nothingFoundLabel = new Label("No animals with this species were found.");
            nothingFoundLabel.getStyleClass().add("generic-centered-label");
            searchResultsContainer.getChildren().add(nothingFoundLabel);
            return;
        }

        for (Animal animal : foundAnimals) {
            ResultComponent animalResultComponent = new ResultComponent();
            animalResultComponent.setText(
                    String.format("%s • %s • %s • %d year%s old • %s", animal.name, animal.species, animal.sex, animal.age, animal.age != 1 ? "s" : "", animal.healthy ? "Healthy" : "Unhealthy")
            );
            searchResultsContainer.getChildren().add(animalResultComponent);
        }
    }
}
