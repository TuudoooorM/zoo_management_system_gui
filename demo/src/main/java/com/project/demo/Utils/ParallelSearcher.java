package com.project.demo.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ParallelSearcher<T> {
    private final List<T> items;

    public ParallelSearcher(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public List<T> findItems(Predicate<T> predicate) {
        if (items.size() < Constants.THREAD_COUNT)
            return sequentialSearch(predicate);

        int chunkSize = (int) Math.ceil((double) items.size() / Constants.THREAD_COUNT);
        List<FinderThread<T>> threads = new ArrayList<>(Constants.THREAD_COUNT);

        for (int i = 0; i < Constants.THREAD_COUNT; i++) {
            int chunkSizeStart = i * chunkSize;
            int chunkSizeEnd = Math.min(items.size(), chunkSizeStart + chunkSize);
            FinderThread<T> thread = new FinderThread<>(items, chunkSizeStart, chunkSizeEnd);
            thread.setPredicate(predicate);
            threads.add(thread);
            thread.start();
        }

        try {
            for (FinderThread<T> thread : threads) thread.join();
        } catch (InterruptedException error) {
            System.err.println("There's been an error joining a thread. Resorting to sequential search. Error cause: " + error.getMessage());
            return sequentialSearch(predicate);
        }

        List<T> foundItems = new ArrayList<>();

        for (FinderThread<T> thread : threads) {
            List<Integer> resultIndices = thread.getResultIndices();
            for (int index : resultIndices)
                foundItems.add(items.get(index));
        }

        return foundItems;
    }

    private List<T> sequentialSearch(Predicate<T> predicate) {
        List<T> foundItems = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item))
                foundItems.add(item);
        }

        return foundItems;
    }
}
