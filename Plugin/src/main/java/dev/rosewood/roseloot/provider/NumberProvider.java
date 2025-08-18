package dev.rosewood.roseloot.provider;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.configuration.ConfigurationSection;

public interface NumberProvider {

    /**
     * Gets an integer number from this provider
     *
     * @param context The LootContext
     * @return The number
     */
    default int getInteger(LootContext context) {
        return (int) Math.round(this.getDouble(context));
    }

    /**
     * Gets a double number from this provider
     *
     * @param context The LootContext
     * @return The number
     */
    double getDouble(LootContext context);

    /**
     * Gets a float number from this provider
     *
     * @param context The LootContext
     * @return The number
     */
    default float getFloat(LootContext context) {
        return (float) this.getDouble(context);
    }

    /**
     * @return true if this number provider's input was represented with a percentage, false otherwise
     */
    boolean isPercentage();

    static NumberProvider fromString(String string) {
        if (string.endsWith("%")) {
            if (string.startsWith("%")) {
                return new PlaceholderNumberProvider(string);
            } else {
                try {
                    double percentageValue = Double.parseDouble(string.substring(0, string.length() - 1));
                    return new ConstantNumberProvider(percentageValue / 100, true);
                } catch (NumberFormatException ignored) { }
            }
        }

        try {
            return new ConstantNumberProvider(Double.parseDouble(string), false);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static NumberProvider fromSection(ConfigurationSection section, String value, int defaultValue) {
        return fromSection(section, value, (double) defaultValue);
    }

    static NumberProvider fromSection(ConfigurationSection section, String key, Double defaultValue) {
        if (section == null || key == null || key.isEmpty()) {
            if (defaultValue == null) {
                return null;
            } else {
                return new ConstantNumberProvider(defaultValue, false);
            }
        }

        if (section.isConfigurationSection(key)) {
            ConfigurationSection numberSection = section.getConfigurationSection(key);
            if (numberSection == null)
                return new ConstantNumberProvider(defaultValue, false);

            if (numberSection.contains("min") || numberSection.contains("max")) {
                NumberProvider min = fromSection(numberSection, "min", defaultValue);
                NumberProvider max = fromSection(numberSection, "max", defaultValue);
                int decimals = numberSection.getInt("decimals", 2);
                return new UniformDistributionNumberProvider(min, max, decimals);
            } else {
                NumberProvider n = fromSection(numberSection, "n", defaultValue);
                NumberProvider p = fromSection(numberSection, "p", defaultValue);
                return new BinomialDistributionNumberProvider(n, p);
            }
        } else if (section.contains(key)) {
            if (section.isString(key)) {
                String stringValue = section.getString(key, "");

                if (stringValue.endsWith("%")) {
                    if (stringValue.startsWith("%")) {
                        // Placeholder! Try parsing it as a double
                        return new PlaceholderNumberProvider(stringValue);
                    } else {
                        try {
                            double percentageValue = Double.parseDouble(stringValue.substring(0, stringValue.length() - 1));
                            return new ConstantNumberProvider(percentageValue / 100, true);
                        } catch (NumberFormatException ignored) { }
                    }
                }
            } else {
                double doubleValue = section.getDouble(key, Double.MIN_VALUE);
                if (doubleValue != Double.MIN_VALUE)
                    return new ConstantNumberProvider(doubleValue, false);
            }
        }

        if (defaultValue == null) {
            return null;
        } else {
            return new ConstantNumberProvider(section.getDouble(key, defaultValue), false);
        }
    }

    class ConstantNumberProvider implements NumberProvider {

        private final double value;
        private final boolean isPercentage;

        private ConstantNumberProvider(double value, boolean isPercentage) {
            this.value = value;
            this.isPercentage = isPercentage;
        }

        @Override
        public double getDouble(LootContext context) {
            return this.value;
        }

        @Override
        public boolean isPercentage() {
            return this.isPercentage;
        }

    }

    class UniformDistributionNumberProvider implements NumberProvider {

        private final NumberProvider min, max;
        private final int decimals;

        private UniformDistributionNumberProvider(NumberProvider min, NumberProvider max, int decimals) {
            this.min = min;
            this.max = max;
            this.decimals = LootUtils.clamp(decimals, 0, 15);
        }

        @Override
        public double getDouble(LootContext context) {
            double value = LootUtils.randomInRange(this.min.getDouble(context), this.max.getDouble(context));
            if (this.decimals == -1)
                return value;
            if (this.decimals == 0)
                return Math.round(value);
            return Math.round(value * Math.pow(10, this.decimals)) / Math.pow(10, this.decimals);
        }

        @Override
        public boolean isPercentage() {
            return this.min.isPercentage() || this.max.isPercentage();
        }

    }

    class BinomialDistributionNumberProvider implements NumberProvider {

        private final NumberProvider n, p;

        private BinomialDistributionNumberProvider(NumberProvider n, NumberProvider p) {
            this.n = n;
            this.p = p;
        }

        @Override
        public int getInteger(LootContext context) {
            int n = this.n.getInteger(context);
            double p = this.p.getDouble(context);
            int successes = 0;

            for (int i = 0; i < n; i++)
                if (LootUtils.checkChance(p))
                    successes++;

            return successes;
        }

        @Override
        public double getDouble(LootContext context) {
            return this.getInteger(context);
        }

        @Override
        public boolean isPercentage() {
            return this.n.isPercentage() || this.p.isPercentage();
        }

    }

    class PlaceholderNumberProvider implements NumberProvider {

        private final String placeholder;

        private PlaceholderNumberProvider(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public double getDouble(LootContext context) {
            try {
                return Double.parseDouble(context.applyPlaceholders(this.placeholder));
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        @Override
        public boolean isPercentage() {
            return false;
        }

    }

}
