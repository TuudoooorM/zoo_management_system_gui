package com.project.demo.Zoo;


import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.UUID;

public class Enclosure implements ISpace, Comparable<Enclosure> {
    private String id;

    @NotNull(message = "Missing what species an enclosure should house")
    public String speciesHoused;

    @PositiveOrZero(message = "Missing capacity of an enclosure")
    public int capacity;

    public ArrayList<@Valid Animal> animals = new ArrayList<>(1);

    @Positive(message = "Missing width, height or length of an enclosure")
    private float width, height, length;

    public Enclosure() {
        this.id = null;
        this.speciesHoused = null;
        this.capacity = -1;
        this.width = -1;
        this.height = -1;
        this.length = -1;
    }

    public Enclosure(String speciesHoused, int capacity, float width, float height, float length) {
        this.id = UUID.randomUUID().toString();
        this.speciesHoused = speciesHoused;
        this.capacity = capacity;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    void addAnimal(Animal animal) throws EnclosureCapacityExceededException {
        if (capacity == 0) throw new EnclosureCapacityExceededException("There is no more space for animal " + animal.name);

        animals.add(animal);
        capacity--;
    }

    public boolean removeAnimal(Animal animal) {
        boolean didAnimalExistInEnclosure = animals.remove(animal);
        if (didAnimalExistInEnclosure) capacity++;

        return didAnimalExistInEnclosure;
    }

    void listAnimals() {
       ArrayList<Animal> sortedAnimals = new ArrayList<>(animals);
       sortedAnimals.sort(Animal::compareTo);
        for (Animal animal : sortedAnimals)
            System.out.printf("\t %s (%d, %s) Health status: %s%n", animal.name, animal.age, animal.sex, animal.healthy ? "OK" : "UNHEALTHY");
    }

    public String getId() {
        return id;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getLength() {
        return length;
    }

    public void setId(String id) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setLength(float length) {
        this.length = length;
    }

    @Override @JsonIgnore
    public float getArea() {
        return width * height;
    }

    @Override @JsonIgnore
    public float getVolume() {
        return width * height * length;
    }

    @Override
    public int compareTo(Enclosure other) {
        return this.capacity - other.capacity;
    }
}
