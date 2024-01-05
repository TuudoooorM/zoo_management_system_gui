package com.project.demo.controllers;

import com.project.demo.Database.ZooDatabaseManager;
import com.project.demo.Utils.ViewModes;
import com.project.demo.Zoo.Sex;
import com.project.demo.ZooApplication;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AdminInputController extends DefaultController {
    public TextField zooNameInput;
    public TextField firstNameInput;
    public TextField lastNameInput;
    public ChoiceBox<String> sexChoiceInput;
    public TextField yearlySalaryInput;
    public TextField workedMonthsInput;
    public PasswordField passwordInput;

    public void submitAdminInput(ActionEvent actionEvent) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        errorAlert.setHeaderText("Admin input error");
        StringBuilder errorsMessageBuilder = new StringBuilder();

        if (zooNameInput.getText().isEmpty())
            errorsMessageBuilder.append("The zoo must have a name.\n");

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
        Sex sex = sexChoiceInput.getSelectionModel().getSelectedIndex() == 0 ? Sex.male : Sex.female;
        String hashedPassword = passwordEncoder.encode(passwordInput.getText());

        boolean didAddAdmin = ZooDatabaseManager.tryAddAdminAndZooName(zooNameInput.getText(), fullName, sex, yearlySalary, workedMonths, hashedPassword);
        if (!didAddAdmin) {
            errorAlert.setContentText("There's been an error adding the admin to the database. Please try again.");
            errorAlert.show();
            return;
        }

        ZooApplication.zoo.name = zooNameInput.getText();
        ZooApplication.zoo.setAdmin(fullName, sex, yearlySalary, workedMonths, hashedPassword);

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        successAlert.setHeaderText("The admin details have been successfully configured.");
        successAlert.setTitle("Admin input success");
        successAlert.show();

        ZookeeperInputController.setViewMode(ViewModes.SETUP);
        super.navigateToView(actionEvent);
    }
}
