package com.project.demo.controllers;

import com.project.demo.Zoo.Sex;
import com.project.demo.ZooApplication;
import com.project.demo.ZooInputDataStore;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ZookeeperInputController {

    public TextField firstNameInput;
    public TextField lastNameInput;
    public ChoiceBox<?> sexChoiceInput;
    public TextField yearlySalaryInput;
    public TextField workedMonthsInput;
    public TextField jobInput;
    public PasswordField passwordInput;

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
        ZooInputDataStore.setZookeeperInput(firstNameInput.getText(), lastNameInput.getText(), sexChoiceInput.getSelectionModel().getSelectedIndex() == 0 ? Sex.male : Sex.female, yearlySalary, workedMonths, jobInput.getText(), passwordEncoder.encode(passwordInput.getText()));

        // TODO: this should not always change to animal-view because we may use this for the admin to add a new zookeeper, not just on setup
    }
}
