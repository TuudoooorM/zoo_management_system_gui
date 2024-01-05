package com.project.demo.Zoo;

public class Animal implements Comparable<Animal> {

    public final String name;

    public final String species;

    public int age;

    public Sex sex;

    public boolean healthy;

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
