package com.project.demo.Zoo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnclosureTest {
    @Test
    void testEnclosureConstructor() {
        Enclosure enclosure1 = new Enclosure("1234", "species", 10, 2.1f, 3.4f, 2.2f);
        assertEquals("1234", enclosure1.getId());
        assertEquals("species", enclosure1.speciesHoused);
        assertEquals(10, enclosure1.capacity);
        assertEquals(2.1f, enclosure1.getWidth());
        assertEquals(3.4f, enclosure1.getHeight());
        assertEquals(2.2f, enclosure1.getLength());

        Enclosure enclosure2 = new Enclosure("species-2", 9, 2.1f, 1.1f, 9f);
        assertEquals(4, enclosure2.getId().length());
        assertEquals("species-2", enclosure2.speciesHoused);
        assertEquals(9, enclosure2.capacity);
        assertEquals(2.1f, enclosure2.getWidth());
        assertEquals(1.1f, enclosure2.getHeight());
        assertEquals(9f, enclosure2.getLength());
        
    }
}