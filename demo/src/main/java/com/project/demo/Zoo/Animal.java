package com.project.demo.Zoo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class Animal implements Comparable<Animal> {

    @NotNull(message = "Missing the name of an animal")
    public final String name;

    @NotNull(message = "Missing the species of an animal")
    public final String species;

    @PositiveOrZero(message = "Missing the age of an animal")
    public int age;

    @NotNull(message = "Missing the sex of an animal")
    public final Sex sex;

    public boolean healthy;

    public Animal() {
        this.name = null;
        this.species = null;
        this.sex = null;
        this.age = -1;
        this.healthy = false;
    }
    public Animal(String name, String species, Sex sex, int age, boolean healthy) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.sex = sex;
        this.healthy = healthy;
    }

    @Override
    public int compareTo(Animal other) {
        return this.age - other.age;
    }
}
