package com.project.demo.Utils;

import com.project.demo.Database.JsonFileSystemDatabase;
import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import com.project.demo.Zoo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Setup {
    public static Zoo init() {
        System.out.println("--- NEW SETUP ---\n");
        String zooName;
        try {
            zooName = ZooInputDevice.readZooName();
            if (zooName == null) return null;
        } catch (IllegalArgumentException error) {
            System.err.println("\nTrying to read the zoo name, this error was returned: " + error.getMessage());
            return null;
        }

        Zoo zoo = new Zoo(zooName);

        try {
            Admin admin = ZooInputDevice.readAdmin();
            if (admin == null) return null;
            zoo.setAdmin(admin.name, admin.sex, admin.getSalary(), admin.getWorkedMonths(), admin.password);
        } catch (NumberFormatException error) {
            System.err.println("\nOne of the \"age\", \"salary\" or \"worked months\" parameters is not a valid number.");
            return null;
        } catch (Exception error) {
            System.err.printf("\nThe following error was returned whilst trying to read the admin of the zoo:\n%s\n", error);
            return null;
        }

        try {
            List<Zookeeper> zookeepers = ZooInputDevice.readZookeepers();
            if (zookeepers != null)
                for (Zookeeper zookeeper : zookeepers)
                    zoo.addZookeeper(zookeeper.name, zookeeper.job, zookeeper.sex, zookeeper.getSalary(), zookeeper.getWorkedMonths(), zookeeper.getPassword());
        } catch(Exception error) {
            System.err.printf("\nThis error occurred whilst trying to read zookeepers:\n%s\n", error.getMessage());
        }

        try {
            List<Enclosure> enclosures = ZooInputDevice.readEnclosures();
            if (enclosures != null)
                for (Enclosure enclosure : enclosures)
                    zoo.addEnclosure(enclosure.speciesHoused, enclosure.capacity, enclosure.getWidth(), enclosure.getHeight(), enclosure.getLength());
        } catch (Exception error) {
            System.err.printf("\nThis error occurred whilst trying to read enclosures:\n%s\n", error.getMessage());
        }


        try {
            List<Animal> animals = ZooInputDevice.readAnimals();
            if (animals != null)
                for (Animal animal : animals)
                    try {
                        zoo.addAnimal(animal);
                    } catch (EnclosureCapacityExceededException | MissingEnclosureException error) {
                        System.err.println(error.getMessage());
                    }
        } catch (Exception error) {
            System.err.printf("\nThis error occurred whilst trying to read animals:\n%s\n", error.getMessage());
        }

        System.out.println("\n--- Zoo created. ---\n");

        Authenticator.privilege = Privileges.ADMIN;
        Authenticator.employee = zoo.admin;
        return zoo;
    }

    public static Zoo load() {
        System.out.println("Loading database...");
        JsonFileSystemDatabase db = new JsonFileSystemDatabase(Constants.DATABASE_FILE_PATH);

        try {
            db.open();
        } catch (FileNotFoundException error) {
            System.out.printf("%s\nA new, empty database file has been created at that path. Running program in \"new\" mode...\n", error.getMessage());
            return Setup.init();
        } catch (IOException error) {
            System.err.printf("\nThe following error occurred whilst trying to open the database stored in file: \"%s\"\n%s\n", Constants.DATABASE_FILE_PATH, error);
            return null;
        }

        if(!db.tryOpen()) return null;
        Zoo zoo;
        try {
            zoo = db.load();
        } catch (FileNotFoundException error) {
            System.err.printf("\nThe database file \"%s\" does not exist.\n", Constants.DATABASE_FILE_PATH);
            return null;
        } catch (JsonProcessingException error){
            System.err.printf("\nThere's been an error deserializing the JSON: \n%s\n", error);
            return null;
        }

        Validator zooValidator;
        try (ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory()) {
            zooValidator = validatorFactory.getValidator();
        }

        Set<ConstraintViolation<Zoo>> zooStructureViolations = zooValidator.validate(zoo);
        if (!zooStructureViolations.isEmpty()) {
            System.err.print("\n--- ERRORS ---\nWhile reading the contents of the JSON database, the following data problems have occurred:");

            for (ConstraintViolation<Zoo> violation : zooStructureViolations) {
                System.err.printf("\n\t%s\n", violation.getMessage());
            }

            String doesUserWantToCreateNewZoo;
            do doesUserWantToCreateNewZoo = InputReader.tryReadLineFromConsole("Do you want to create a new zoo? (y/n) ");
            while (doesUserWantToCreateNewZoo == null);

            return doesUserWantToCreateNewZoo.equalsIgnoreCase("y") ? Setup.init() : null;
        }


        System.out.println("Database loaded!");
        return zoo;
    }
}
