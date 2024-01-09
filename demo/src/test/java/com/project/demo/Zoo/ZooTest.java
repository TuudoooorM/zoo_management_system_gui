package com.project.demo.Zoo;

import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class ZooTest {

    @Test
    void testZooConstructor() {
        Zoo zoo = new Zoo();
        assertNull(zoo.name);

        zoo = new Zoo("test");
        assertEquals("test", zoo.name);
    }

    @Test
    void addAnimal() {
        Zoo zoo = new Zoo();
        // Enclosure test implemented.
        zoo.addEnclosure("species", 1, 1.2f, 3.2f, 2.2f);

        assertDoesNotThrow(
                () -> zoo.addAnimal("name", "species", Sex.male, 10, true)
        );

        assertThrows(MissingEnclosureException.class,
                () -> zoo.addAnimal("name2", "no enclosure", Sex.male, 10, true)
        );

        assertThrows(EnclosureCapacityExceededException.class,
                () -> zoo.addAnimal("name3", "species", Sex.female, 1, false)
        );
    }

    @Test
    void findAnimalInEnclosure() throws EnclosureCapacityExceededException, MissingEnclosureException {
        Zoo zoo = new Zoo();
        // Enclosure test already implemented.
        zoo.addEnclosure("test", 1, 1.2f, 3.2f, 2.2f);
        zoo.addEnclosure("test2", 1, 1.2f, 3.2f, 2.2f);
        zoo.addAnimal("name", "test", Sex.female, 1, false);
        zoo.addAnimal("name2", "test2", Sex.female, 1, false);

        Pair<Animal, Enclosure> data = zoo.findAnimalInEnclosure("test", "name");
        assertNotNull(data);
        assertNotNull(data.getKey());
        assertNotNull(data.getValue());

        assertEquals("name", data.getKey().name);
        assertEquals(1, data.getKey().age);

        assertEquals("test", data.getValue().speciesHoused);
        assertEquals(0, data.getValue().capacity);

        Pair<Animal, Enclosure> data2 = zoo.findAnimalInEnclosure("test", "doesn't exist");
        assertNull(data2);

        Pair<Animal, Enclosure> data3 = zoo.findAnimalInEnclosure("doesn't exist", "name");
        assertNull(data3);
    }
}