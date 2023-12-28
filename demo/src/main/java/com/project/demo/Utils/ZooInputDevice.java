package com.project.demo.Utils;

import com.project.demo.Zoo.*;

import java.util.ArrayList;
import java.util.List;

public class ZooInputDevice {
    public static String readZooName() {
        String zooName = InputReader.tryReadLineFromConsole("What should the new zoo be called? ");
        if (zooName == null) return null;

        if (zooName.isEmpty())
            throw new IllegalArgumentException("The zoo name is missing.");

        return zooName;
    }

    public static Admin readAdmin() {
        String rawAdminDetails = InputReader.tryReadLineFromConsole("For the admin, please enter: first name, last name, sex (m/f), salary (int), worked months (int), all separated by a single space. ");
        if (rawAdminDetails == null) return null;

        List<String> adminDetails = List.of(rawAdminDetails.split(" "));
        if (adminDetails.size() < ArgumentCount.ADMIN)
            throw new IllegalArgumentException("There are some arguments missing.");

        int sexEnumIndex = InputReader.getSexFromInput(adminDetails.get(2));

        if (sexEnumIndex == -1)
            throw new IllegalArgumentException("That is not a valid sex option.");

        int adminSalary = Integer.parseInt(adminDetails.get(3));
        int adminWorkedMonths = Integer.parseInt(adminDetails.get(4));

        String password = InputReader.tryReadLineFromConsole("What is the password for the admin? ");
        if (password == null) return null;
        if (password.isEmpty()) throw new IllegalArgumentException("No password has been supplied for this zookeeper.");

        Admin admin = new Admin(adminDetails.get(0) + " " + adminDetails.get(1), Sex.values()[sexEnumIndex], password);
        admin.setSalary(adminSalary);
        admin.increaseWorkedMonths(adminWorkedMonths);
        return admin;
    }

    public static List<Zookeeper> readZookeepers() {
        String userAnswerForZookeepers = InputReader.tryReadLineFromConsole("Do you want to add zookeepers? (y/n) ");
        if (userAnswerForZookeepers == null || !userAnswerForZookeepers.equals("y")) return null;

        List<Zookeeper> zookeepers = new ArrayList<>();

        String doesUserWantToKeepAddingZookeepers = "y";
        while (doesUserWantToKeepAddingZookeepers != null && doesUserWantToKeepAddingZookeepers.equals("y")) {
            try {
                Zookeeper newZookeeper = ZooInputDevice.readZookeeper();
                if (newZookeeper == null) {
                    doesUserWantToKeepAddingZookeepers = InputReader.tryReadLineFromConsole("Do you want to try to read another zookeeper? (y/n) ");
                    continue;
                }

                zookeepers.add(newZookeeper);
            } catch (NumberFormatException error) {
                System.err.println("\nOne of the \"salary\" and \"worked months\" parameters is not a valid number.");
            } catch (Exception error) {
                System.err.printf("\nThis error occurred while trying to read a zookeeper:\n%s\n", error.getMessage());
            }

            doesUserWantToKeepAddingZookeepers = InputReader.tryReadLineFromConsole("Do you want to read another zookeeper? (y/n) ");
        }

        return zookeepers;
    }

    public static Zookeeper readZookeeper() {
        String rawDetails = InputReader.tryReadLineFromConsole("Please enter: first name, last name, sex (m/f), salary (int), worked months (int), all separated by a single space. ");
        if (rawDetails == null) return null;

        List<String> zookeeperDetails = List.of(rawDetails.split(" "));
        if (zookeeperDetails.size() < ArgumentCount.ZOOKEEPER)
            throw new IllegalArgumentException("Some arguments are missing.");

        String name = zookeeperDetails.get(0) + " " + zookeeperDetails.get(1);
        int enumSexIndex = InputReader.getSexFromInput(zookeeperDetails.get(2));

        if (enumSexIndex == -1)
            throw new IllegalArgumentException("That is not a valid sex option.");

        int salary = Integer.parseInt(zookeeperDetails.get(3));
        int workedMonths = Integer.parseInt(zookeeperDetails.get(4));

        String job = InputReader.tryReadLineFromConsole("What is the job of this new zookeeper? ");
        if (job == null) return null;

        String password = InputReader.tryReadLineFromConsole("What is the password for this new zookeeper? ");
        if (password == null) return null;
        if(password.isEmpty()) throw new IllegalArgumentException("No password has been supplied for this zookeeper.");

        Zookeeper newZookeeper = new Zookeeper(name, job, Sex.values()[enumSexIndex], password);
        newZookeeper.setSalary(salary);
        newZookeeper.increaseWorkedMonths(workedMonths);
        return newZookeeper;
    }

    public static List<Enclosure> readEnclosures() {
        String userAnswerForEnclosures = InputReader.tryReadLineFromConsole("Do you want to add enclosures? (y/n) ");
        if (userAnswerForEnclosures == null || !userAnswerForEnclosures.equals("y")) return null;

        List<Enclosure> enclosures = new ArrayList<>();

        String doesUserWantToKeepAddingEnclosures = "y";
        while (doesUserWantToKeepAddingEnclosures != null && doesUserWantToKeepAddingEnclosures.equals("y")) {
            try {
                Enclosure enclosure = ZooInputDevice.readEnclosure();
                if (enclosure == null) {
                    doesUserWantToKeepAddingEnclosures = InputReader.tryReadLineFromConsole("Do you want to try to read another enclosure? (y/n) ");
                    continue;
                }

                enclosures.add(enclosure);
            } catch (NumberFormatException error) {
                System.err.println("\nOne of the \"capacity\", \"width\", \"height\", and \"length\" parameters are not valid decimal numbers.");
            } catch (Exception error) {
                System.err.printf("\nThe following error has occurred when trying to read an enclosure:\n%s\n", error.getMessage());
            }

            doesUserWantToKeepAddingEnclosures = InputReader.tryReadLineFromConsole("Do you want to read another enclosure? (y/n) ");
        }

        return enclosures;
    }

    public static Enclosure readEnclosure() {
        String rawDetails = InputReader.tryReadLineFromConsole("Please enter: species housed, capacity (int), width, height, length (floats), all separated by a single space. ");
        if (rawDetails == null) return null;

        List<String> enclosureDetails = List.of(rawDetails.split(" "));
        if (enclosureDetails.size() < ArgumentCount.ENCLOSURE)
            throw new IllegalArgumentException("Some arguments are missing.");

        String speciesHoused = enclosureDetails.get(0);
        int capacity = Integer.parseInt(enclosureDetails.get(1));
        float width = Float.parseFloat(enclosureDetails.get(2));
        float height = Float.parseFloat(enclosureDetails.get(3));
        float length = Float.parseFloat(enclosureDetails.get(4));

        return new Enclosure(speciesHoused, capacity, width, height, length);
    }

    public static List<Animal> readAnimals() {
        String userAnswerForAnimals = InputReader.tryReadLineFromConsole("Do you want to add animals? (y/n) ");
        if (userAnswerForAnimals == null || !userAnswerForAnimals.equals("y")) return null;

        String doesUserWantToKeepAddingAnimals = "y";
        List<Animal> animals = new ArrayList<>();

        while (doesUserWantToKeepAddingAnimals != null && doesUserWantToKeepAddingAnimals.equals("y")) {
            try {
                Animal animal = ZooInputDevice.readAnimal();
                if (animal == null) {
                    doesUserWantToKeepAddingAnimals = InputReader.tryReadLineFromConsole("Do you want to try to read another animal? (y/n) ");
                    continue;
                }

                animals.add(animal);
            } catch (NumberFormatException error) {
                System.err.printf("\nOne of the \"age\" or \"healthy\" parameters don't have a valid format. Namely:\n%s\n", error.getMessage());
            } catch (Exception error) {
                System.err.printf("\nThis error occurred whilst trying to read an animal:\n%s\n", error.getMessage());
            }

            doesUserWantToKeepAddingAnimals = InputReader.tryReadLineFromConsole("Do you want to read another animal? (y/n) ");
        }

        return animals;
    }

    public static Animal readAnimal() throws IllegalArgumentException, NumberFormatException {
        String rawDetails = InputReader.tryReadLineFromConsole("Please enter: animal name, species, sex (m/f), age (int), healthiness (y/n), all separated by a single space. ");
        if (rawDetails == null) return null;

        List<String> animalDetails = List.of(rawDetails.split(" "));
        if (animalDetails.size() < ArgumentCount.ANIMAL)
            throw new IllegalArgumentException("Some arguments are missing.");

        String name = animalDetails.get(0), species = animalDetails.get(1);
        int enumSexIndex = InputReader.getSexFromInput(animalDetails.get(2));

        if (enumSexIndex == -1)
            throw new IllegalArgumentException("That is not a valid sex option.");

        int age = Integer.parseInt(animalDetails.get(3));
        String isHealthyRawInput = animalDetails.get(4);
        boolean isHealthy;
        if (isHealthyRawInput.equals("y")) isHealthy = true;
        else if (isHealthyRawInput.equals("n")) isHealthy = false;
        else throw new NumberFormatException("The \"healthiness\" parameter is not \"y\" or \"n\".");


        return new Animal(name, species, Sex.values()[enumSexIndex], age, isHealthy);
    }


}
