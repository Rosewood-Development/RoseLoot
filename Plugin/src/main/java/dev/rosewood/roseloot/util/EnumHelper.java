package dev.rosewood.roseloot.util;

public final class EnumHelper {

    private EnumHelper() {

    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name) {
        return valueOf(enumClass, name, null);
    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name, T defaultValue) {
        if (name == null || name.isBlank())
            return defaultValue;
        for (T value : enumClass.getEnumConstants())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return defaultValue;
    }

}
