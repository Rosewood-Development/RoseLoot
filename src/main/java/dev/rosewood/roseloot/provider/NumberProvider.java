package dev.rosewood.roseloot.provider;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import org.bukkit.configuration.ConfigurationSection;

public interface NumberProvider {

    default int getInteger(LootContext context) {
        return (int) Math.round(this.getDouble(context));
    }

    double getDouble(LootContext context);

    static NumberProvider fromString(String string) {
        if (string.endsWith("%")) {
            if (string.startsWith("%")) {
                return new PlaceholderNumberProvider(string);
            } else {
                try {
                    double percentageValue = Double.parseDouble(string.substring(0, string.length() - 1));
                    return new ConstantNumberProvider(percentageValue / 100);
                } catch (NumberFormatException ignored) { }
            }
        }

        try {
            return new ConstantNumberProvider(Double.parseDouble(string));
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
                return new ConstantNumberProvider(defaultValue);
            }
        }

        if (section.isConfigurationSection(key)) {
            ConfigurationSection numberSection = section.getConfigurationSection(key);
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
                            return new ConstantNumberProvider(percentageValue / 100);
                        } catch (NumberFormatException ignored) { }
                    }
                }
            } else {
                double doubleValue = section.getDouble(key, Double.MIN_VALUE);
                if (doubleValue != Double.MIN_VALUE)
                    return new ConstantNumberProvider(doubleValue);
            }
        }

        if (defaultValue == null) {
            return null;
        } else {
            return new ConstantNumberProvider(section.getDouble(key, defaultValue));
        }
    }

    class ConstantNumberProvider implements NumberProvider {

        private final double value;

        private ConstantNumberProvider(double value) {
            this.value = value;
        }

        @Override
        public double getDouble(LootContext context) {
            return this.value;
        }

    }

    class UniformDistributionNumberProvider implements NumberProvider {

        private final NumberProvider min, max;

        private UniformDistributionNumberProvider(NumberProvider min, NumberProvider max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public double getDouble(LootContext context) {
            return LootUtils.randomInRange(this.min.getDouble(context), this.max.getDouble(context));
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

    }

}
