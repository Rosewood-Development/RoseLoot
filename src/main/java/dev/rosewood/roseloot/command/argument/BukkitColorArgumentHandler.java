package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import org.bukkit.Color;

public class BukkitColorArgumentHandler extends AbstractColorArgumentHandler<Color> {

    public BukkitColorArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Color.class);
    }

    @Override
    protected Color rgbToColor(int r, int g, int b) {
        return Color.fromRGB(r, g, b);
    }

}
