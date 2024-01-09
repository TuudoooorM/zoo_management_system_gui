package com.project.demo.Utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

    @Test
    void run() {
        List<Integer> numbers = List.of(9, 9, 4, 3, 2, 7);

        FinderThread<Integer> thread = new FinderThread<>(numbers, 0, numbers.size());
        thread.setPredicate(number -> number == 9);
        thread.run();
        List<Integer> resultIndices = thread.getResultIndices();

        assertIterableEquals(List.of(0, 1), resultIndices);

        thread = new FinderThread<>(numbers, 0, numbers.size());
        thread.setPredicate(number -> number == 0);
        thread.run();
        resultIndices = thread.getResultIndices();

        assertIterableEquals(List.of(), resultIndices);

        thread = new FinderThread<>(numbers, 0, numbers.size());
        thread.setPredicate(number -> number == 7);
        thread.run();
        resultIndices = thread.getResultIndices();

        assertIterableEquals(List.of(numbers.size() - 1), resultIndices);
    }


    @Test
    void runMultithreaded() throws InterruptedException {
        List<Integer> numbers = List.of(9, 9, 4, 3, 2, 7, 9, 1, 1, 9);
        List<FinderThread<Integer>> threads = new ArrayList<>();
        int chunkSize = (int) Math.ceil((double) numbers.size() / Constants.THREAD_COUNT);

        for (int i = 0; i < Constants.THREAD_COUNT; i++) {
            int start = i * chunkSize;
            int end = Math.min(numbers.size(), start + chunkSize);
            FinderThread<Integer> thread = new FinderThread<>(numbers, start, end);
            thread.setPredicate(number -> number == 9);
            threads.add(thread);
        }

        for (Thread thread : threads) thread.start();
        for (Thread thread : threads) thread.join();

        List<Integer> result = new ArrayList<>();
        for (FinderThread<Integer> thread : threads) {
            result.addAll(thread.getResultIndices());
        }

        assertIterableEquals(List.of(0, 1, 6, 9), result);
    }
}