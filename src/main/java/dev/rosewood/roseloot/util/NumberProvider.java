package dev.rosewood.roseloot.util;

import org.bukkit.configuration.ConfigurationSection;

public abstract class NumberProvider {

    public int getInteger() {
        return (int) Math.round(this.getDouble());
    }

    public abstract double getDouble();

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
        } else {
            if (defaultValue == null) {
                return null;
            } else {
                if (section.isString(value)) {
                    String percentage = section.getString(value, "");
                    if (percentage.endsWith("%")) {
                        try {
                            double percentageValue = Double.parseDouble(percentage.substring(0, percentage.length() - 1));
                            return new ConstantNumberProvider(percentageValue / 100);
                        } catch (NumberFormatException ignored) { }
                    }
                }
            }
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
        public double getDouble() {
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
        public double getDouble() {
            return LootUtils.randomInRange(this.min.getDouble(), this.max.getDouble());
        }

    }

    private static class BinomialDistributionNumberProvider extends NumberProvider {

        private final NumberProvider n, p;

        public BinomialDistributionNumberProvider(NumberProvider n, NumberProvider p) {
            this.n = n;
            this.p = p;
        }

        @Override
        public int getInteger() {
            int n = this.n.getInteger();
            double p = this.p.getDouble();
            int successes = 0;

            for (int i = 0; i < n; i++)
                if (LootUtils.checkChance(p))
                    successes++;

            return successes;
        }

        @Override
        public double getDouble() {
            return this.getInteger();
        }

    }

}
