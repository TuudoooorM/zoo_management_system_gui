package com.project.demo.Zoo;


import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Utils.Randoms;

import java.util.ArrayList;
import java.util.Random;

public class Enclosure implements ISpace, Comparable<Enclosure> {
    private String id;

    public String speciesHoused;

    public int capacity;

    public ArrayList<Animal> animals = new ArrayList<>();

    private float width, height, length;

    public Enclosure(String id, String speciesHoused, int capacity, float width, float height, float length) {
        this.id = id;
        this.speciesHoused = speciesHoused;
        this.capacity = capacity;
        this.width = width;
        this.height = height;
        this.length = length;
    }


    public Enclosure(String speciesHoused, int capacity, float width, float height, float length) {
        this.id = String.valueOf(Randoms.getRandomNumberBetween(1000, 9999));
        this.speciesHoused = speciesHoused;
        this.capacity = capacity;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public void addAnimal(Animal animal) throws EnclosureCapacityExceededException {
        if (capacity == 0) throw new EnclosureCapacityExceededException("There is no more space for animal " + animal.name);

        animals.add(animal);
        capacity--;
    }

    public boolean removeAnimal(Animal animal) {
        boolean didAnimalExistInEnclosure = animals.remove(animal);
        if (didAnimalExistInEnclosure) capacity++;

        return didAnimalExistInEnclosure;
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
        if (id == null) {
            Random random = new Random();
            this.id = String.valueOf(random.nextInt(9999 - 1000) + 1000);
        } else this.id = id;
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

    @Override
    public float getArea() {
        return width * height;
    }

    @Override
    public float getVolume() {
        return width * height * length;
    }

    @Override
    public int compareTo(Enclosure other) {
        return this.capacity - other.capacity;
    }
}
