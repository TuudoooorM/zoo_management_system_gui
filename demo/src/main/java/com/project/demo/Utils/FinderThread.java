package com.project.demo.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FinderThread<T> extends Thread {
    private final List<T> values;
    private final int chunkStart, chunkEnd;
    private final List<Integer> resultIndices = new ArrayList<>();

    private Predicate<T> predicate;

    public void setPredicate(Predicate<T> receivedPredicate) {
        this.predicate = receivedPredicate;
    }

    public List<Integer> getResultIndices() {
        return resultIndices;
    }

    public FinderThread(List<T> values, int chunkStart, int chunkEnd) {
        this.values = values;
        this.chunkStart = chunkStart;
        this.chunkEnd = chunkEnd;
    }

    @Override
    public void run() {
        for (int i = chunkStart; i < chunkEnd; i++)
            if (predicate.test(values.get(i)))
                resultIndices.add(i);
    }
}
