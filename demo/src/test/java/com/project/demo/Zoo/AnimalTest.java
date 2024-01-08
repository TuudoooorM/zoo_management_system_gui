package com.project.demo.Zoo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {
    @Test
    void testAnimalConstructor() {
        Animal animal = new Animal("test", "species", Sex.female, 7, true);
        assertEquals("test", animal.name);
        assertEquals("species", animal.species);
        assertEquals(Sex.female, animal.sex);
        assertEquals(7, animal.age);
        assertTrue(animal.healthy);
    }
}