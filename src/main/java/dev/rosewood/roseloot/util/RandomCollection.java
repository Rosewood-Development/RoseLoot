package dev.rosewood.roseloot.util;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCollection<T> {

    private final NavigableMap<Double, T> map;
    private double total;

    public RandomCollection() {
        this.map = new TreeMap<>();
        this.total = 0;
    }

    public void add(double weight, T result) {
        if (weight > 0) {
            this.total += weight;
            this.map.put(this.total, result);
        }
    }

    public T next() {
        double value = ThreadLocalRandom.current().nextDouble() * this.total;
        return this.map.higherEntry(value).getValue();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

}
