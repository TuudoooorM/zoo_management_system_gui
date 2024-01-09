package com.project.demo.Utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ParallelSearcherTest {
    @Test
    public void testFinderThreadConstructor() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        ParallelSearcher<Integer> parallelSearcher = new ParallelSearcher<>(numbers);
        assertIterableEquals(numbers, parallelSearcher.getItems());
    }

    @Test
    void findItems() {
        List<Integer> numbers = List.of(5, 9, 1, 2, 7, 9, 4, 2, 6, 5);
        ParallelSearcher<Integer> parallelSearcher = new ParallelSearcher<>(numbers);

        List<Integer> foundItems = parallelSearcher.findItems(number -> number == 9);
        assertIterableEquals(List.of(9, 9), foundItems);

        foundItems = parallelSearcher.findItems(number -> number == 5);
        assertIterableEquals(List.of(5, 5), foundItems);

        foundItems = parallelSearcher.findItems(number -> number == 1);
        assertIterableEquals(List.of(1), foundItems);

        foundItems = parallelSearcher.findItems(number -> number == 0);
        assertIterableEquals(List.of(), foundItems);
    }
}
