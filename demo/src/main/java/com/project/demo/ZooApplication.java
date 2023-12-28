package com.project.demo;

import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import com.project.demo.Zoo.Sex;
import com.project.demo.Zoo.Zoo;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ZooApplication extends Application {
    private static Stage primaryStage;
    public static Zoo zoo = new Zoo();

    @Override
    public void start(Stage stage) throws IOException, EnclosureCapacityExceededException, MissingEnclosureException {
        primaryStage = stage;
        zoo.addEnclosure("a", 3, 1, 2, 3);
        zoo.addAnimal("Harambe", "a", Sex.male, 1, true);
        zoo.addAnimal("Harambo", "a", Sex.female, 2, true);
        zoo.addAnimal("Harambi", "a", Sex.male, 3, false);

        zoo.addEnclosure("b", 3, 1, 2, 3);
        zoo.addAnimal("Harambe", "b", Sex.male, 1, true);
        zoo.addAnimal("Harambo", "b", Sex.female, 2, true);
        zoo.addAnimal("Harambi", "b", Sex.male, 3, false);

        // TODO: style the input used for deleting and add the same logic to the rest
        // TODO: change the UUID generation to numerical IDs with 4 digits

        zoo.addZookeeper("John Doe", "Eats shit", Sex.male, 4000, 1, "aegfadgbn");
        zoo.addZookeeper("John Doe", "Eats shit", Sex.male, 4000, 1, "aegfadgbn");
        zoo.addZookeeper("John Doe", "Eats shit", Sex.male, 4000, 1, "aegfadgbn");
        zoo.addZookeeper("John Doe", "Eats shit", Sex.male, 4000, 1, "aegfadgbn");

        FXMLLoader fxmlLoader = new FXMLLoader(ZooApplication.class.getResource("zoo-input-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 512);
        primaryStage.setTitle("Zoo Management System");
        primaryStage.setScene(scene);

        scene.setFill(Color.web("#11190F"));
        primaryStage.show();
    }

    public static void changeScene(String fxmlFilePath) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ZooApplication.class.getResource(fxmlFilePath));
        Parent newScene = fxmlLoader.load();

        primaryStage.getScene().setRoot(newScene);
    }

    public static void main(String[] args) {
        launch();
    }
}