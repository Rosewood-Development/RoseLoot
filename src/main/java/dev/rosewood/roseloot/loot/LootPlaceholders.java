package dev.rosewood.roseloot.loot;

import dev.rosewood.rosegarden.utils.StringPlaceholders;
import java.util.HashMap;
import java.util.Map;

public class LootPlaceholders {

    private final Map<String, Object> placeholders;
    private StringPlaceholders compiled;

    public LootPlaceholders() {
        this.placeholders = new HashMap<>();
    }

    /**
     * Adds a placeholder to the loot placeholders
     *
     * @param key The key of the placeholder
     * @param value The value of the placeholder
     */
    public void add(String key, Object value) {
        this.compiled = null; // Any compiled placeholders will now be outdated and needs to be rebuilt

        key = key.toLowerCase();

        Object existingValue = this.placeholders.get(key);
        if (existingValue == null) {
            this.placeholders.put(key, value);
            return;
        }

        // If the previous and current values are both numbers, add them together
        if (existingValue instanceof Number && value instanceof Number) {
            double first = ((Number) existingValue).doubleValue();
            double second = ((Number) value).doubleValue();
            this.placeholders.put(key, first + second);
        } else { // Otherwise, just replace the existing value with the new one
            this.placeholders.put(key, value);
        }
    }

    /**
     * Checks if a placeholder with a given key exists
     *
     * @param key The key of the placeholder
     * @return true if the placeholder exists, false otherwise
     */
    public boolean containsKey(String key) {
        return this.placeholders.containsKey(key);
    }

    /**
     * Applies the placeholders to the given string
     *
     * @param string The string to apply the placeholders to
     * @return The string with the placeholders applied
     */
    public String apply(String string) {
        if (this.compiled == null)
            this.compile();

        return this.compiled.apply(string);
    }

    /**
     * Compiles the placeholders into a StringPlaceholders object
     */
    private void compile() {
        StringPlaceholders.Builder builder = StringPlaceholders.builder();

        for (Map.Entry<String, Object> entry : this.placeholders.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Number) {
                double numberValue = ((Number) value).doubleValue();
                int intValue = (int) numberValue;
                if (numberValue == intValue) // Cast floating point number to an int if there are no decimals
                    value = intValue;
            }

            builder.add(key, value);
        }

        this.compiled = builder.build();
    }

}
