package com.project.demo.Zoo;

import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import com.project.demo.Utils.ParallelSearcher;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    public List<Enclosure> findEnclosuresBySpecies(String species) {
        ParallelSearcher<Enclosure> parallelSearcher = new ParallelSearcher<>(enclosures);
        List<Enclosure> foundEnclosures = parallelSearcher.findItems(
                enclosure -> enclosure.speciesHoused.equals(species)
        );

        return !foundEnclosures.isEmpty() ? foundEnclosures : null;
    }

    public void addAnimal(Animal animal) throws MissingEnclosureException, EnclosureCapacityExceededException {
        List<Enclosure> enclosuresHousingThisSpecies = findEnclosuresBySpecies(animal.species);

        if (enclosuresHousingThisSpecies == null)
            throw new MissingEnclosureException("There are no enclosures for animal species " + animal.species);

        if (!species.contains(animal.species))
            species.add(animal.species.toLowerCase());

        boolean didAddAnimal = false;
        for (Enclosure enclosure : enclosuresHousingThisSpecies) {
            try {
                enclosure.addAnimal(animal);

                didAddAnimal = true;
                break;
            } catch (EnclosureCapacityExceededException ignored) {
                // Just pass over it and try the next one.
            }
        }

        if (!didAddAnimal)
            throw new EnclosureCapacityExceededException("There is no more space for animal " + animal.name);
    }

    public Pair<Animal, Enclosure> findAnimalInEnclosure(String species, String name) {
        List<Enclosure> foundEnclosures = findEnclosuresBySpecies(species);
        if (foundEnclosures == null) return null;

        for (Enclosure enclosure : foundEnclosures) {
            ParallelSearcher<Animal> parallelSearcher = new ParallelSearcher<>(enclosure.animals);
            List<Animal> foundAnimals = parallelSearcher.findItems(animal -> animal.name.equals(name));
            if (foundAnimals.isEmpty()) continue;

            return new Pair<>(foundAnimals.get(0), enclosure);
        }

        return null;
    }

     public boolean removeAnimal(Enclosure enclosure, Animal animal) {
        return enclosure.removeAnimal(animal);
    }

    public Enclosure addEnclosure(String id, String speciesHoused, int capacity, float width, float height, float length) {
        Enclosure newEnclosure = new Enclosure(id, speciesHoused, capacity, width, height, length);
        enclosures.add(newEnclosure);
        species.add(speciesHoused);
        return newEnclosure;
    }

    public Enclosure addEnclosure(String speciesHoused, int capacity, float width, float height, float length) {
        Enclosure newEnclosure = new Enclosure(speciesHoused, capacity, width, height, length);

        enclosures.add(newEnclosure);
        species.add(speciesHoused);
        return newEnclosure;
    }

    public void addEnclosure(Enclosure enclosure) {
        species.add(enclosure.speciesHoused);
        enclosures.add(enclosure);
    }

    public Enclosure findEnclosure(String enclosureID) {
        ParallelSearcher<Enclosure> parallelSearcher = new ParallelSearcher<>(enclosures);
        List<Enclosure> foundEnclosures = parallelSearcher.findItems(
                enclosure -> enclosure.getId().equals(enclosureID)
        );

        // There should only be one enclosure with this ID.
        return !enclosures.isEmpty() ? foundEnclosures.get(0) : null;
    }

    public boolean removeEnclosure(Enclosure enclosure) {
        if (enclosure == null) return false;

        species.remove(enclosure.speciesHoused);
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
        ParallelSearcher<Zookeeper> parallelSearcher = new ParallelSearcher<>(zookeepers);
        List<Zookeeper> foundZookeeper = parallelSearcher.findItems(zookeeper -> zookeeper.getId().equals(zookeeperID));

        // There should only be one zookeeper with this ID.
        return !foundZookeeper.isEmpty() ? foundZookeeper.get(0) : null;
    }

    public boolean removeZookeeper(Zookeeper foundZookeeper) {
        return zookeepers.remove(foundZookeeper);
    }

    public ArrayList<Animal> searchBySpecies(String rawReceivedSpecies) {
        String receivedSpecies = rawReceivedSpecies.toLowerCase();

        ParallelSearcher<Enclosure> parallelSearcher = new ParallelSearcher<>(enclosures);
        List<Enclosure> foundEnclosures = parallelSearcher.findItems(
                enclosure -> enclosure.speciesHoused.equals(receivedSpecies)
        );

        if (foundEnclosures.isEmpty()) return null;

        // Consider the initial capacity to be the number of found enclosures. It's probably unlikely
        // for this to be enough to store all the animals, unless each enclosure found has exactly one animal
        // in it.
        ArrayList<Animal> foundAnimals = new ArrayList<>(foundEnclosures.size());

        for (Enclosure enclosure : foundEnclosures)
            foundAnimals.addAll(enclosure.animals);

        return !foundAnimals.isEmpty() ? foundAnimals : null;
    }
}

