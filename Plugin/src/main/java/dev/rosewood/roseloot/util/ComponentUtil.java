package dev.rosewood.roseloot.util;

import dev.rosewood.rosegarden.utils.HexUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentUtil {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexColors()
            .build();

    public static Component colorifyAndComponentify(String legacyText) {
        return LegacyComponentSerializer.legacySection().deserialize(HexUtils.colorify(legacyText));
    }

    public static String decomponentifyAndDecolorify(Component component) {
        return LootUtils.decolorize(SERIALIZER.serialize(component));
    }

}
