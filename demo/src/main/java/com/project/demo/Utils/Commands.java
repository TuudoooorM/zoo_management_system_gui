package com.project.demo.Utils;

import com.project.demo.Database.JsonFileSystemDatabase;
import com.project.demo.Zoo.*;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    public static void listCommands() {
        System.out.println("""
                'list' animals/zookeepers/enclosures
                'search' species
                'add' animal/zookeeper/enclosure
                'remove' animal/zookeeper/enclosure
                'exit'
                """);
    }
    public static void handleInput(Zoo zoo) {
        while (true) {
            List<String> args;
            String rawCommandInput = InputReader.tryReadLineFromConsole("Input the command here: ");

            if (rawCommandInput == null) {
                System.err.println("Let's try again...");
                continue;
            }

            args = List.of(rawCommandInput.split(" "));

            if (args.isEmpty()) {
                System.err.println("Please input an argument.");
                continue;
            }

            switch (args.get(0)) {
                case "list" -> list(args, zoo);
                case "search" -> search(args, zoo);
                case "add" -> add(args, zoo);
                case "remove" -> remove(args, zoo);
                case "exit" -> {
                    save(zoo);
                    return;
                }

                default -> System.err.println("\nThat is an invalid command.");
            }
        }
    }

    private static void add(List<String> args, Zoo zoo) {
        if (Authenticator.privilege != Privileges.ADMIN) {
            System.err.println("\nYou are not authorized to perform this command.");
            return;
        }

        if (args.size() < 2) {
            System.err.println("\nYou have not specified what to add (animal/enclosure/zookeeper).");
            return;
        }

        switch (args.get(1)) {
            case "animal" -> {
                try {
                    Animal newAnimal = ZooInputDevice.readAnimal();
                    if (newAnimal == null) return;
                    zoo.addAnimal(newAnimal);

                    System.out.println("Animal added successfully.");
                } catch (NumberFormatException error) {
                    System.err.printf("\nOne of the \"age\" or \"healthy\" parameters don't have a valid format. Namely:\n%s\n", error.getMessage());
                } catch (Exception error) {
                    System.err.printf("\nThis error occurred whilst trying to read an animal:\n%s\n", error.getMessage());
                }
            }

            case "enclosure" -> {
                try {
                    Enclosure newEnclosure = ZooInputDevice.readEnclosure();
                    if (newEnclosure == null) return;

                    zoo.addEnclosure(newEnclosure.speciesHoused, newEnclosure.capacity, newEnclosure.getWidth(), newEnclosure.getHeight(), newEnclosure.getLength());
                    System.out.println("Enclosure added successfully.");
                } catch (NumberFormatException error) {
                    System.err.println("\nOne of the \"capacity\", \"width\", \"height\", and \"length\" parameters are not valid decimal numbers.");
                } catch (Exception error) {
                    System.err.printf("\nThe following error has occurred when trying to read an enclosure:\n%s\n", error.getMessage());
                }
            }

            case "zookeeper" -> {
                try {
                    Zookeeper newZookeeper = ZooInputDevice.readZookeeper();
                    if (newZookeeper == null) return;

                    zoo.addZookeeper(newZookeeper.name, newZookeeper.job, newZookeeper.sex, newZookeeper.getSalary(), newZookeeper.getWorkedMonths(), newZookeeper.getPassword());
                    System.out.println("Zookeeper added successfully.");
                } catch (NumberFormatException error) {
                    System.err.println("\nOne of the \"salary\" and \"worked months\" parameters is not a valid number.");
                } catch (Exception error) {
                    System.err.printf("\nThis error occurred while trying to read a zookeeper:\n%s\n", error.getMessage());
                }
            }
        }
    }

    public static void remove(List<String> args, Zoo zoo) {
        if (Authenticator.privilege != Privileges.ADMIN) {
            System.err.println("\nYou are not authorized to perform this command.");
            return;
        }

        if (args.size() < 2) {
            System.err.println("\nYou have not specified what to remove (animal/enclosure/zookeeper).");
            return;
        }

        switch (args.get(1)) {
            case "animal" -> {
                if (zoo.enclosures.isEmpty()) {
                    System.out.println("There are no enclosures in the zoo.");
                    return;
                }

                String name = InputReader.tryReadLineFromConsole("Please input the animal name: ");
                if (name == null) return;

                String species = InputReader.tryReadLineFromConsole("Please input the animal species: ");
                if (species == null) return;

                String rawAge = InputReader.tryReadLineFromConsole("Please input the animal age: ");
                if (rawAge == null) return;

                int age;
                try {
                    age = Integer.parseInt(rawAge);
                } catch (NumberFormatException error) {
                    System.err.println("\nThat is not a valid age number.");
                    return;
                }


                for (Enclosure enclosure : zoo.enclosures)  {
                    Animal foundAnimal = enclosure.animals
                            .stream()
                            .filter(
                            animal -> animal.name.equalsIgnoreCase(name) &&
                                    animal.species.equalsIgnoreCase(species) &&
                                    animal.age == age)
                            .findFirst()
                            .orElse(null);

                    if (foundAnimal == null) {
                        System.err.println("\nThis animal does not exist.");
                        return;
                    }

                    boolean didAnimalExistInEnclosure = zoo.removeAnimal(enclosure, foundAnimal);
                    if (!didAnimalExistInEnclosure)
                        System.out.println("\nThat animal did not exist in the enclosure... somehow?");
                    else
                        System.out.println("Animal deleted successfully!");
                    return;
                }
            }

            case "enclosure" -> {
                if (zoo.enclosures.isEmpty()) {
                    System.out.println("There are no enclosures in the zoo.");
                    return;
                }

                String enclosureID = InputReader.tryReadLineFromConsole("Please enter the enclosure's ID: ");
                if (enclosureID == null) return;

                Enclosure foundEnclosure = zoo.enclosures
                        .stream()
                        .filter(enclosure -> enclosure.getId().equals(enclosureID))
                        .findFirst()
                        .orElse(null);

                if (foundEnclosure == null) {
                    System.err.println("\nThere's no enclosure with that ID.");
                    return;
                }

                boolean didEnclosureExistInZoo = zoo.removeEnclosure(foundEnclosure);
                if (!didEnclosureExistInZoo)
                    System.out.println("That enclosure did not exist... somehow?");
                else
                    System.out.println("Enclosure deleted successfully!");
            }

            case "zookeeper" -> {
                if (zoo.zookeepers.isEmpty()) {
                    System.out.println("There are no zookeepers in the zoo.");
                    return;
                }

                String zookeeperID = InputReader.tryReadLineFromConsole("Please enter the zookeeper's ID: ");
                if (zookeeperID == null) return;

                boolean didZookeeperExistInZoo = zoo.removeZookeeper(zookeeperID);
                if (!didZookeeperExistInZoo)
                    System.out.println("There's no zookeeper with that ID.");
                else
                    System.out.println("Zookeeper deleted successfully!");
            }

            default -> System.err.println("\nThat is not a valid argument (animal/enclosure/zookeeper).");
        }
    }

    private static void search(List<String> args, Zoo zoo) {
        if (args.size() < 2) {
            System.err.println("\nYou have not given any species to search after.");
            return;
        }

        ArrayList<Animal> filtered = zoo.searchBySpecies(args.get(1));
        if (filtered == null) {
            System.err.println("\nNo animals of this type have been found.");
            return;
        }

        for (Animal animal : filtered)
            System.out.printf("%s (%d, %s) Health: %s%n", animal.name, animal.age, animal.sex, animal.healthy ? "OK" : "UNHEALTHY");
    }

    private static void save(Zoo zoo) {
        if (Authenticator.privilege != Privileges.ADMIN) return;

        String shouldStoreStateAnswer;
        do shouldStoreStateAnswer = InputReader.tryReadLineFromConsole("Save everything? (y/n) ");
        while (shouldStoreStateAnswer == null);

        if (!shouldStoreStateAnswer.equalsIgnoreCase("y")) return;

        JsonFileSystemDatabase db = new JsonFileSystemDatabase(Constants.DATABASE_FILE_PATH);
        if (!db.tryOpen()) return;
        db.trySave(zoo);
    }


    private static void list(List<String> args, Zoo zoo) {
        if (args.size() < 2) {
            System.err.println("Insufficient arguments for list command.");
            return;
        }

        System.out.println("--- LIST ---\n\n");
        System.out.printf("The zoo \"%s\" administrated by \"%s\" has the following stats:%n%n", zoo.name, zoo.admin.getName());

        switch (args.get(1)) {
            case "animals" -> zoo.listAnimals();
            case "enclosures" -> zoo.listEnclosures();
            case "zookeepers" -> zoo.listZookeepers();
        }
    }
}
