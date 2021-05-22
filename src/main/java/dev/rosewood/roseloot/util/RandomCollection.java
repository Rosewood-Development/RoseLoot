package dev.rosewood.roseloot.util;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class RandomCollection<T> {

    private final Map<T, Double> elementToWeight;

    public RandomCollection() {
        this.elementToWeight = new HashMap<>();
    }

    public void add(double weight, T element) {
        if (weight > 0)
            this.elementToWeight.put(element, weight);
    }

    public T next() {
        NavigableMap<Double, T> map = new TreeMap<>();
        double total = 0;
        for (Map.Entry<T, Double> entry : this.elementToWeight.entrySet()) {
            total += entry.getValue();
            map.put(total, entry.getKey());
        }

        double value = ThreadLocalRandom.current().nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    public T removeNext() {
        T next = this.next();
        this.elementToWeight.remove(next);
        return next;
    }

    public void removeIf(Predicate<T> predicate) {
        this.elementToWeight.entrySet().removeIf(x -> predicate.test(x.getKey()));
    }

    public boolean isEmpty() {
        return this.elementToWeight.isEmpty();
    }

}
