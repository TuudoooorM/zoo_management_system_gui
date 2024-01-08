package com.project.demo.Zoo;

import com.project.demo.Exceptions.EnclosureCapacityExceededException;
import com.project.demo.Exceptions.MissingEnclosureException;
import org.junit.jupiter.api.BeforeAll;
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
}