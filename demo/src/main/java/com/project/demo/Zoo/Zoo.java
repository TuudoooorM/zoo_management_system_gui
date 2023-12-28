package com.project.demo.Zoo;

import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import com.project.demo.Utils.Authenticator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public class Zoo {
    @Valid @NotNull(message = "Missing the admin of the zoo")
    public Admin admin = null;

    @NotNull(message = "Missing zoo name")
    public String name;

    private final HashSet<String> species = new HashSet<>();
    public final ArrayList<@Valid Enclosure> enclosures = new ArrayList<>(1);
    public final ArrayList<@Valid Zookeeper> zookeepers = new ArrayList<>(2);


    public Zoo() {
        this.name = null;
    }

    public Zoo(String name) {
        this.name = name;
    }

    @JsonIgnore
    public int getAnimalsCount() {
        return enclosures
                .stream()
                .reduce(0, (totalAnimalsCount, currentEnclosure) -> totalAnimalsCount + currentEnclosure.animals.size(), Integer::sum);
    }

    @JsonIgnore
    public int getSpeciesCount() {
        if (!species.isEmpty()) return species.size();
        if (enclosures.isEmpty()) return 0;

        // deserializing doesn't update species count, so after deserializing just take the distinct species from the enclosures
        for (Enclosure enclosure : enclosures)
            species.add(enclosure.speciesHoused);

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

    public void addAnimal(Animal animal) throws MissingEnclosureException, EnclosureCapacityExceededException {

        Enclosure enclosureHousingThisSpecies = enclosures.
                stream()
                .filter(enclosure -> enclosure.speciesHoused.equalsIgnoreCase(animal.species))
                .findFirst()
                .orElse(null);


        if (enclosureHousingThisSpecies == null)
            throw new MissingEnclosureException("There is no such enclosure for animal species " + animal.species);

        if (!species.contains(animal.species))
            species.add(animal.species.toLowerCase());

        enclosureHousingThisSpecies.addAnimal(animal);
    }

    public boolean removeAnimal(Enclosure enclosure, Animal animal) {
        return enclosure.removeAnimal(animal);
    }

    public Enclosure addEnclosure(String speciesHoused, int capacity, float width, float height, float length) {
        Enclosure newEnclosure = new Enclosure(speciesHoused, capacity, width, height, length);

        enclosures.add(newEnclosure);
        return newEnclosure;
    }

    public boolean removeEnclosure(Enclosure enclosure) {
        return enclosures.remove(enclosure);
    }

    public Zookeeper addZookeeper(String name, String job, Sex sex, int salary, int workedMonths, String password) {
        Zookeeper newZookeeper = new Zookeeper(name, job, sex, password);
        zookeepers.add(newZookeeper);

        newZookeeper.setSalary(salary);
        newZookeeper.increaseWorkedMonths(workedMonths);

        return newZookeeper;
    }

    public boolean removeZookeeper(Zookeeper zookeeper) {
        return zookeepers.remove(zookeeper);
    }

    public void listAnimals() {
        System.out.printf("Currently there %s %d animal%s in here.%n", getAnimalsCount() != 1 ? "are" : "is", getAnimalsCount(), getAnimalsCount() != 1 ? "s" : "");
        System.out.println("Total species count: " + getSpeciesCount());
        System.out.println("----------");

        int count = 0;
        ArrayList<Enclosure> sortedEnclosures = new ArrayList<>(enclosures);
        sortedEnclosures.sort(Enclosure::compareTo);

        for (Enclosure enclosure : sortedEnclosures) {
            System.out.printf("Enclosure %d (id: %s) | %d animal(s) inside. Capacity: %d. Species housed: %s%n", count + 1, enclosure.getId(), enclosure.animals.size(), enclosure.capacity, enclosure.speciesHoused);
            enclosure.listAnimals();
            count++;
        }
    }

    public void listEnclosures() {
        System.out.printf("Currently there are %d enclosure(s) in here.%n", enclosures.size());
        int count = 0;

        ArrayList<Enclosure> sortedEnclosures = new ArrayList<>(enclosures);
        sortedEnclosures.sort(Enclosure::compareTo);

        for (Enclosure enclosure : sortedEnclosures) {
            System.out.printf("Enclosure %d (id: %s)\n\tCapacity: %d. Area and volume: %.2fm^2 & %.2fm^3 Species housed: %s. Number of animals inside: %d%n", count + 1, enclosure.getId(), enclosure.capacity, enclosure.getArea(), enclosure.getVolume(), enclosure.speciesHoused, enclosure.animals.size());
            count++;
        }
    }

    public void listZookeepers() {
        System.out.printf("Currently there are %d zookeeper(s) working here.%n", zookeepers.size());

        if (Authenticator.privilege == Privileges.GUEST) {
            System.err.println("You're not allowed to list zookeeper details.");
            return;
        }

        if (Authenticator.privilege == Privileges.ZOOKEEPER) {
            System.out.println("Only showing the details for you.");
            Zookeeper zookeeper = (Zookeeper)Authenticator.employee;
            System.out.printf("Zookeeper %s (id: %s) | Job: \"%s\". Salary: %d. Worked months: %d\n", zookeeper.name, zookeeper.getId(), zookeeper.getJob(), zookeeper.getSalary(), zookeeper.getWorkedMonths());
            return;
        }

        int count = 0;
        for (Zookeeper zookeeper : zookeepers) {
            System.out.printf("Zookeeper %d -> %s, %s (id: %s) | Job: \"%s\". Salary: %d. Worked months: %d%n", count + 1, zookeeper.name, zookeeper.sex, zookeeper.getId(), zookeeper.getJob(), zookeeper.getSalary(), zookeeper.getWorkedMonths());
            count++;
        }
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
