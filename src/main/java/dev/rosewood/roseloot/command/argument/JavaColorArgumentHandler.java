package dev.rosewood.roseloot.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import java.awt.Color;

public class JavaColorArgumentHandler extends AbstractColorArgumentHandler<Color> {

    public JavaColorArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Color.class);
    }

    @Override
    protected Color rgbToColor(int r, int g, int b) {
        return new Color(r, g, b);
    }

}
