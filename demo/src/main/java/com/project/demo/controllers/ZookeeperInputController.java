package com.project.demo.controllers;

import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.Sex;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ZookeeperInputController {
    public TextField firstNameInput;
    public TextField lastNameInput;
    public ChoiceBox<String> sexChoiceInput;
    public TextField yearlySalaryInput;
    public TextField workedMonthsInput;
    public TextField jobInput;
    public PasswordField passwordInput;

    private static ViewModes viewMode = ViewModes.INPUT;
    public Button skipButton;

    @FXML
    public void initialize() {
        skipButton.setVisible(viewMode == ViewModes.SETUP);
    }

    public static void setMode(ViewModes receivedViewMode) {
        viewMode = receivedViewMode;
    }

    public void navigateToView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            String viewPath = (String) clickedButton.getUserData();
            if (viewPath.contains("enclosure-input-view")) EnclosureInputController.setMode(viewMode);

            ZookeeperInputController.setMode(ViewModes.INPUT);
            ZooApplication.changeScene(viewPath);
        } catch (Exception error) {
            System.err.println("There's been an error changing scene. Received scene path: " + clickedButton.getUserData());
            Platform.exit();
        }
    }


    public void submitZookeeperInput(ActionEvent actionEvent) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        errorAlert.setHeaderText("Zookeeper input error");
        StringBuilder errorsMessageBuilder = new StringBuilder();

        if (firstNameInput.getText().length() < 3)
            errorsMessageBuilder.append("The first name is too short.\n");

        if (lastNameInput.getText().length() < 3)
            errorsMessageBuilder.append("The last name is too short.\n");

        int yearlySalary = -1;
        try {
            yearlySalary = Integer.parseInt(yearlySalaryInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The yearly salary must be a number.\n");
        }

        int workedMonths = -1;
        try {
            workedMonths = Integer.parseInt(workedMonthsInput.getText());
        } catch (NumberFormatException error) {
            errorsMessageBuilder.append("The worked months input must receive a number.\n");
        }

        if (passwordInput.getText().length() < 8)
            errorsMessageBuilder.append("The password must be at least 8 characters long.\n");

        if (!errorsMessageBuilder.isEmpty()) {
            errorAlert.setContentText(errorsMessageBuilder.toString());
            errorAlert.show();
            return;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        ZooApplication.zoo.addZookeeper(
                firstNameInput.getText() + " " + lastNameInput.getText(),
                jobInput.getText(),
                sexChoiceInput.getSelectionModel().getSelectedIndex() == 0 ? Sex.male : Sex.female,
                yearlySalary,
                workedMonths,
                passwordEncoder.encode(passwordInput.getText())
        );

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        successAlert.setHeaderText("This zookeeper's details have been successfully configured.");
        successAlert.setTitle("Zookeeper input success");
        successAlert.show();

        if (viewMode == ViewModes.INPUT) {
            try {
                ZooApplication.changeScene("zoo-input-view.fxml");
            } catch (Exception error) {
                System.err.println("There's been an error changing scene. Received scene path: zoo-input-view.fxml");
                Platform.exit();
            }
        } else if (viewMode == ViewModes.SETUP) {
            firstNameInput.clear();
            lastNameInput.clear();
            yearlySalaryInput.clear();
            workedMonthsInput.clear();
            jobInput.clear();
            passwordInput.clear();
        }
    }
}
