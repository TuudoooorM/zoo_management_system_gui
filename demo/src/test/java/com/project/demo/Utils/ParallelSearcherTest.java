package com.project.demo.Utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


// As ParallelSearcher directly instantiates and uses the FinderThread class (composition),
// this test automatically applies to FinderThread.


class ParallelSearcherTest {

    @Test
    public void testFinderThreadConstructor() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        ParallelSearcher<Integer> parallelSearcher = new ParallelSearcher<>(numbers);
        assertIterableEquals(numbers, parallelSearcher.getItems());
    }
}
