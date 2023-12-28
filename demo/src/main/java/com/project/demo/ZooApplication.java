package com.project.demo;

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
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(ZooApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 512);
        primaryStage.setTitle("Zoo Management System");
        primaryStage.setScene(scene);

        scene.setFill(Color.web("11190F"));
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