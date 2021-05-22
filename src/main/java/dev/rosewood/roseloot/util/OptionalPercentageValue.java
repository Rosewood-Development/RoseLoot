package dev.rosewood.roseloot.util;

public class OptionalPercentageValue {

    private final double value;
    private final boolean isPercentage;

    private OptionalPercentageValue(double value, boolean isPercentage) {
        this.value = value;
        this.isPercentage = isPercentage;
    }

    public int getAsInt(int maxValue) {
        return (int) Math.round(this.getAsDouble(maxValue));
    }

    public double getAsDouble(double maxValue) {
        if (this.isPercentage) {
            return maxValue * this.value / 100;
        } else {
            return this.value;
        }
    }

    public static OptionalPercentageValue parse(String value) {
        if (value == null || value.isEmpty())
            return null;

        boolean isPercentage = false;
        if (value.endsWith("%")) {
            isPercentage = true;
            value = value.substring(0, value.length() - 1);
        }

        try {
            return new OptionalPercentageValue(Double.parseDouble(value), isPercentage);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
