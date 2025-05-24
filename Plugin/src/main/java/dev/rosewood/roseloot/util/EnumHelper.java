package dev.rosewood.roseloot.util;

public final class EnumHelper {

    private EnumHelper() {

    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String name) {
        if (name == null || name.isBlank())
            return null;
        for (T value : enumClass.getEnumConstants())
            if (value.name().equalsIgnoreCase(name))
                return value;
        return null;
    }

}
