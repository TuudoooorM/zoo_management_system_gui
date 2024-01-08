package com.project.demo.Utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinderThreadTest {
    @Test
    void testFinderThreadConstructor() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        FinderThread<Integer> thread = new FinderThread<>(numbers, 0, numbers.size() - 1);

        assertIterableEquals(numbers, thread.getValues());
        assertEquals(0, thread.getChunkStart());
        assertEquals(numbers.size() - 1, thread.getChunkEnd());
    }

}