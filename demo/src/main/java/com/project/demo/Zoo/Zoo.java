package com.project.demo.Zoo;

import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import com.project.demo.ZooApplication;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;

public class Zoo {
    public Admin admin = null;

    public String name;

    private final HashSet<String> species = new HashSet<>();
    public final ArrayList<Enclosure> enclosures = new ArrayList<>(1);
    public final ArrayList<Zookeeper> zookeepers = new ArrayList<>(2);


    public Zoo() {
        this.name = null;
    }

    public Zoo(String name) {
        this.name = name;
    }


    public int getAnimalsCount() {
        return enclosures
                .stream()
                .reduce(0, (totalAnimalsCount, currentEnclosure) -> totalAnimalsCount + currentEnclosure.animals.size(), Integer::sum);
    }

    public int getSpeciesCount() {
        return species.size();
    }


    public void setAdmin(String name, Sex sex, int salary, int workedMonths, String password) {
        this.admin = new Admin(name, sex, password);
        this.admin.setSalary(salary);
        this.admin.increaseWorkedMonths(workedMonths);
    }

    public void addAnimal(String name, String species, Sex sex, int age, boolean healthy) throws EnclosureCapacityExceededException, MissingEnclosureException {
        addAnimal(new Animal(name, species, sex, age, healthy));
    }

    public Enclosure findEnclosureBySpecies(String species) {
        return enclosures.
                stream()
                .filter(enclosure -> enclosure.speciesHoused.equalsIgnoreCase(species))
                .findFirst()
                .orElse(null);
    }

    public void addAnimal(Animal animal) throws MissingEnclosureException, EnclosureCapacityExceededException {
        Enclosure enclosureHousingThisSpecies = findEnclosureBySpecies(animal.species);

        if (enclosureHousingThisSpecies == null)
            throw new MissingEnclosureException("There is no such enclosure for animal species " + animal.species);

        if (!species.contains(animal.species))
            species.add(animal.species.toLowerCase());

        enclosureHousingThisSpecies.addAnimal(animal);
    }

    public Pair<Animal, Enclosure> findAnimalInEnclosure(String species, String name) {
        Enclosure foundEnclosure = findEnclosureBySpecies(species);
        if (foundEnclosure == null) return null;

        Animal foundAnimal = foundEnclosure.animals
                .stream()
                .filter(animal -> animal.name.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (foundAnimal == null) return null;

        return new Pair<>(foundAnimal, foundEnclosure);
    }

     public boolean removeAnimal(Enclosure enclosure, Animal animal) {
        return enclosure.removeAnimal(animal);
    }

    public Enclosure addEnclosure(String id, String speciesHoused, int capacity, float width, float height, float length) {
        Enclosure newEnclosure = new Enclosure(id, speciesHoused, capacity, width, height, length);

        enclosures.add(newEnclosure);
        return newEnclosure;
    }

    public Enclosure addEnclosure(String speciesHoused, int capacity, float width, float height, float length) {
        Enclosure newEnclosure = new Enclosure(speciesHoused, capacity, width, height, length);

        enclosures.add(newEnclosure);
        return newEnclosure;
    }

    public void addEnclosure(Enclosure enclosure) {
        enclosures.add(enclosure);
    }

    public Enclosure findEnclosure(String enclosureID) {
        return enclosures
                .stream()
                .filter(enclosure -> enclosure.getId().equals(enclosureID))
                .findFirst()
                .orElse(null);
    }

    public boolean removeEnclosure(Enclosure enclosure) {
        return enclosures.remove(enclosure);
    }

    public Zookeeper addZookeeper(String id, String name, String job, Sex sex, int salary, int workedMonths, String password) {
        Zookeeper newZookeeper = new Zookeeper(id, name, job, sex, password);
        zookeepers.add(newZookeeper);

        newZookeeper.setSalary(salary);
        newZookeeper.increaseWorkedMonths(workedMonths);

        return newZookeeper;
    }

    public Zookeeper addZookeeper(String name, String job, Sex sex, int salary, int workedMonths, String password) {
        Zookeeper newZookeeper = new Zookeeper(name, job, sex, password);
        zookeepers.add(newZookeeper);

        newZookeeper.setSalary(salary);
        newZookeeper.increaseWorkedMonths(workedMonths);

        return newZookeeper;
    }

    public void addZookeeper(Zookeeper zookeeper) {
        zookeepers.add(zookeeper);
    }

    public Zookeeper findZookeeper(String zookeeperID) {
        return zookeepers
                .stream()
                .filter(zookeeper -> zookeeper.getId().equals(zookeeperID))
                .findFirst()
                .orElse(null);
    }

    public boolean removeZookeeper(Zookeeper foundZookeeper) {
        return zookeepers.remove(foundZookeeper);
    }

    public ArrayList<Animal> searchBySpecies(String rawReceivedSpecies) {
        String receivedSpecies = rawReceivedSpecies.toLowerCase();
        if (!species.contains(receivedSpecies)) return null;

        Enclosure enclosureWithWantedSpecies = enclosures
                .stream()
                .filter(enclosure -> enclosure.speciesHoused.toLowerCase().equals(receivedSpecies))
                .findFirst()
                .orElse(null);

        return enclosureWithWantedSpecies != null ? enclosureWithWantedSpecies.animals : null;
    }

}
