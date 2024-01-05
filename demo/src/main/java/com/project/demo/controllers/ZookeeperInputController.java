package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.Utils.Randoms;
import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.Sex;
import com.project.demo.Zoo.Zoo;
import com.project.demo.Zoo.Zookeeper;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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

    public Button skipButton;

    private static ViewModes viewMode = ViewModes.INPUT;

    public static ViewModes getViewMode() {
        return viewMode;
    }

    public static void setViewMode(ViewModes viewMode) {
        ZookeeperInputController.viewMode = viewMode;
    }


    public void initialize() {
        skipButton.setVisible(getViewMode() == ViewModes.SETUP);
    }

    public void navigateToView(ActionEvent actionEvent) {
        Object eventSource = actionEvent.getSource();
        if (!(eventSource instanceof Button clickedButton)) return;

        try {
            String viewPath = (String) clickedButton.getUserData();
            if (viewPath.contains("enclosure-input-view")) EnclosureInputController.setViewMode(getViewMode());
            // Reset the zoo state so far if the user ever chooses to abort.
            if (viewPath.contains("main-view")) ZooApplication.zoo = new Zoo();

            ZookeeperInputController.setViewMode(ViewModes.INPUT);
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

        String fullName = firstNameInput.getText() + " " + lastNameInput.getText();
        String job = jobInput.getText();
        Sex sex = sexChoiceInput.getSelectionModel().getSelectedIndex() == 0 ? Sex.male : Sex.female;
        String hashedPassword = passwordEncoder.encode(passwordInput.getText());

        Zookeeper addedZookeeper =
                ZooApplication.zoo.addZookeeper(fullName, job, sex, yearlySalary, workedMonths, hashedPassword);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        successAlert.setHeaderText("This zookeeper's details have been successfully configured. Their ID is: " + addedZookeeper.getId());
        successAlert.setTitle("Zookeeper input success");
        successAlert.show();

        if (getViewMode() == ViewModes.INPUT) {
            try {
                ZooApplication.changeScene("zoo-input-view.fxml");
            } catch (Exception error) {
                System.err.println("There's been an error changing scene. Received scene path: zoo-input-view.fxml");
                Platform.exit();
            }
        } else if (getViewMode() == ViewModes.SETUP) {
            firstNameInput.clear();
            lastNameInput.clear();
            yearlySalaryInput.clear();
            workedMonthsInput.clear();
            jobInput.clear();
            passwordInput.clear();
        }
    }
}
