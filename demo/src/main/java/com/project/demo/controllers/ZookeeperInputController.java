package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.Sex;
import com.project.demo.Zoo.Zoo;
import com.project.demo.Zoo.Zookeeper;
import com.project.demo.ZooApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class ZookeeperInputController extends DefaultZooInputController {
    public TextField firstNameInput;
    public TextField lastNameInput;
    public ChoiceBox<String> sexChoiceInput;
    public TextField yearlySalaryInput;
    public TextField workedMonthsInput;
    public TextField jobInput;
    public PasswordField passwordInput;

    public void submitZookeeperInput(ActionEvent actionEvent) {
        ViewModes viewMode = (ViewModes) super.getProps()[0];

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

        if (viewMode == ViewModes.INPUT) {
            boolean didAddZookeeperInDB = ZooDatabaseManager.tryQueryPreparedStatement(
                    "INSERT INTO zookeepers (id, name, sex, salary, worked_months, job, password)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    addedZookeeper.getId(),
                    addedZookeeper.getName(),
                    addedZookeeper.sex.ordinal(),
                    addedZookeeper.getSalary(),
                    addedZookeeper.getWorkedMonths(),
                    !addedZookeeper.getJob().isEmpty() ? addedZookeeper.getJob() : null,
                    addedZookeeper.getPassword()
            );

            if (!didAddZookeeperInDB) {
                Alert errorAddingZookeeperToDBAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                errorAddingZookeeperToDBAlert.setTitle("Error adding zookeeper to DB");
                errorAddingZookeeperToDBAlert.setHeaderText("There's been an error adding this zookeeper to the database. Please try again.");
                errorAddingZookeeperToDBAlert.show();
                ZooApplication.zoo.removeZookeeper(addedZookeeper);
                return;
            }

            successAlert.show();
            super.navigateToView("zoo-input-view.fxml");

        } else if (viewMode == ViewModes.SETUP) {
            successAlert.show();
            firstNameInput.clear();
            lastNameInput.clear();
            yearlySalaryInput.clear();
            workedMonthsInput.clear();
            jobInput.clear();
            passwordInput.clear();
        }
    }
}
