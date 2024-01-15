package com.project.demo;

import com.project.demo.Zoo.Zoo;
import com.project.demo.controllers.DefaultController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class ZooApplication extends Application {
    public static Zoo zoo = new Zoo();

    private static Stage primaryStage;
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(ZooApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 512);
        primaryStage.setTitle("Zoo Management System");
        primaryStage.setScene(scene);

        scene.setFill(Color.web("#11190F"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void changeScene(String fxmlFilePath, Object... controllerProps) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ZooApplication.class.getResource(fxmlFilePath));
        Parent newScene = fxmlLoader.load();

        DefaultController controller = fxmlLoader.getController();
        controller.setProps(controllerProps);

        primaryStage.getScene().setRoot(newScene);
    }

}