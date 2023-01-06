package dev.rosewood.roseloot.util;

import dev.rosewood.roseloot.loot.context.LootContext;
import org.bukkit.configuration.ConfigurationSection;

public abstract class NumberProvider {

    public int getInteger(LootContext context) {
        return (int) Math.round(this.getDouble(context));
    }

    public abstract double getDouble(LootContext context);

    public static NumberProvider fromSection(ConfigurationSection section, String value, int defaultValue) {
        return fromSection(section, value, (double) defaultValue);
    }

    public static NumberProvider fromSection(ConfigurationSection section, String value, Double defaultValue) {
        if (section == null || value == null || value.isEmpty()) {
            if (defaultValue == null) {
                return null;
            } else {
                return new ConstantNumberProvider(defaultValue);
            }
        }

        if (section.isConfigurationSection(value)) {
            ConfigurationSection numberSection = section.getConfigurationSection(value);
            if (numberSection == null)
                return new ConstantNumberProvider(defaultValue);

            if (numberSection.contains("min") || numberSection.contains("max")) {
                NumberProvider min = fromSection(numberSection, "min", defaultValue);
                NumberProvider max = fromSection(numberSection, "max", defaultValue);
                return new UniformDistributionNumberProvider(min, max);
            } else {
                NumberProvider n = fromSection(numberSection, "n", defaultValue);
                NumberProvider p = fromSection(numberSection, "p", defaultValue);
                return new BinomialDistributionNumberProvider(n, p);
            }
        } else if (section.contains(value)) {
            if (section.isString(value)) {
                String stringValue = section.getString(value, "");

                if (stringValue.endsWith("%")) {
                    if (stringValue.startsWith("%")) {
                        // Placeholder! Try parsing it as a double
                        return new PlaceholderNumberProvider(stringValue);
                    } else {
                        try {
                            double percentageValue = Double.parseDouble(stringValue.substring(0, stringValue.length() - 1));
                            return new ConstantNumberProvider(percentageValue / 100);
                        } catch (NumberFormatException ignored) { }
                    }
                }
            } else {
                double doubleValue = section.getDouble(value, Double.MIN_VALUE);
                if (doubleValue != Double.MIN_VALUE)
                    return new ConstantNumberProvider(doubleValue);
            }
        }

        if (defaultValue == null) {
            return null;
        } else {
            return new ConstantNumberProvider(section.getDouble(value, defaultValue));
        }
    }

    public static NumberProvider constant(double value) {
        return new ConstantNumberProvider(value);
    }

    private static class ConstantNumberProvider extends NumberProvider {

        private final double value;

        public ConstantNumberProvider(double value) {
            this.value = value;
        }

        @Override
        public double getDouble(LootContext context) {
            return this.value;
        }

    }

    private static class UniformDistributionNumberProvider extends NumberProvider {

        private final NumberProvider min, max;

        public UniformDistributionNumberProvider(NumberProvider min, NumberProvider max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public double getDouble(LootContext context) {
            return LootUtils.randomInRange(this.min.getDouble(context), this.max.getDouble(context));
        }

    }

    private static class BinomialDistributionNumberProvider extends NumberProvider {

        private final NumberProvider n, p;

        public BinomialDistributionNumberProvider(NumberProvider n, NumberProvider p) {
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

    }

    private static class PlaceholderNumberProvider extends NumberProvider {

        private final String placeholder;

        public PlaceholderNumberProvider(String placeholder) {
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

    }

}
