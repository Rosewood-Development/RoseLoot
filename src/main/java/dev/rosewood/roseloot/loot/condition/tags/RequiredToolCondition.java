package dev.rosewood.roseloot.loot.condition.tags;

import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

/**
 * required-tool:pickaxe,stone
 * value 1: The tool type (shovel, pickaxe, axe, hoe, shears)
 * value 2 (optional): The minimum tool level (wood, stone/gold, iron, diamond, netherite)
 */
public class RequiredToolCondition extends LootCondition {

    private final static List<String> TOOL_TYPES = Arrays.asList("shovel", "pickaxe", "axe", "hoe", "sword", "shears");
    private final static Map<String, Integer> TOOL_QUALITY = new HashMap<String, Integer>() {{
        this.put("wood", 1);
        this.put("wooden", 1);
        this.put("stone", 2);
        this.put("gold", 2);
        this.put("golden", 2);
        this.put("iron", 3);
        this.put("diamond", 4);
        this.put("netherite", 5);
        this.put("shears", 1);
    }};

    private String toolType;
    private int toolQuality;

    public RequiredToolCondition(String tag) {
        super(tag);
    }

    @Override
    public boolean checkInternal(LootContext context) {
        Optional<ItemStack> itemUsed = context.getItemUsed();
        if (!itemUsed.isPresent())
            return false;

        String toolName = itemUsed.get().getType().name().toLowerCase();
        if (TOOL_TYPES.contains(toolName))
            return TOOL_QUALITY.getOrDefault(toolName, 0) >= this.toolQuality;

        if (!toolName.contains("_"))
            return false;

        String[] split = toolName.split("_");
        String qualityName = split[0];
        String type = split[1];

        return type.equals(this.toolType) && TOOL_QUALITY.getOrDefault(qualityName, 0) >= this.toolQuality;
    }

    @Override
    public boolean parseValues(String[] values) {
        if (values.length < 1)
            return false;

        this.toolType = values[0].toLowerCase();
        if (!TOOL_TYPES.contains(this.toolType))
            return false;

        if (values.length > 1) {
            this.toolQuality = TOOL_QUALITY.getOrDefault(values[1].toLowerCase(), 0);
            return this.toolQuality != 0;
        } else {
            this.toolQuality = 1;
        }

        return true;
    }

}
