package dev.rosewood.roseloot.loot.item.meta.component;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.ToolComponent;

public class ToolComponentLootMeta implements ComponentLootMeta {

    private final NumberProvider defaultMiningSpeed;
    private final NumberProvider damagePerBlock;
    private final List<ToolRuleData> toolRules;

    public ToolComponentLootMeta(ConfigurationSection section) {
        this.defaultMiningSpeed = NumberProvider.fromSection(section, "default-mining-speed", null);
        this.damagePerBlock = NumberProvider.fromSection(section, "damage-per-block", null);

        this.toolRules = new ArrayList<>();
        ConfigurationSection toolRulesSection = section.getConfigurationSection("rules");
        if (toolRulesSection != null) {
            for (String key : toolRulesSection.getKeys(false)) {
                ConfigurationSection toolRuleSection = toolRulesSection.getConfigurationSection(key);
                if (toolRuleSection == null)
                    continue;

                StringProvider blocks = StringProvider.fromSection(toolRuleSection, "blocks", null);
                NumberProvider speed = NumberProvider.fromSection(toolRuleSection, "speed", null);
                Boolean correctForDrops = null;
                if (toolRuleSection.isBoolean("correct-for-drops")) correctForDrops = toolRuleSection.getBoolean("correct-for-drops");
                this.toolRules.add(new ToolRuleData(blocks, speed, correctForDrops));
            }
        }
    }

    @Override
    public void apply(ItemMeta itemMeta, LootContext context) {
        ToolComponent toolComponent = itemMeta.getTool();

        if (this.defaultMiningSpeed != null) toolComponent.setDefaultMiningSpeed((float) this.defaultMiningSpeed.getDouble(context));
        if (this.damagePerBlock != null) toolComponent.setDamagePerBlock(this.damagePerBlock.getInteger(context));

        outer:
        for (ToolRuleData toolRuleData : this.toolRules) {
            List<String> blockStrings = toolRuleData.blocks().getList(context);

            Float speed = toolRuleData.speed() == null ? null : (float) toolRuleData.speed().getDouble(context);
            Set<Material> blockMaterials = new HashSet<>();
            for (String blockString : blockStrings) {
                try {
                    if (blockString.startsWith("#")) {
                        Tag<Material> tagBlocks = LootUtils.getTag(blockString.substring(1), Material.class, "blocks");
                        if (tagBlocks != null) {
                            toolComponent.addRule(tagBlocks, speed, toolRuleData.correctForDrops());
                            continue outer;
                        }
                    }

                    Material blockMaterial = Material.matchMaterial(blockString);
                    if (blockMaterial != null && blockMaterial.isBlock())
                        blockMaterials.add(blockMaterial);
                } catch (Exception ignored) { }
            }

            if (!blockMaterials.isEmpty())
                toolComponent.addRule(blockMaterials, speed, toolRuleData.correctForDrops());
        }

        itemMeta.setTool(toolComponent);
    }

    public static void applyProperties(ItemMeta itemMeta, StringBuilder stringBuilder) {
        if (!itemMeta.hasTool())
            return;

        ToolComponent toolComponent = itemMeta.getTool();

        stringBuilder.append("tool-component:\n");
        stringBuilder.append("  default-mining-speed: ").append(toolComponent.getDefaultMiningSpeed()).append('\n');
        stringBuilder.append("  damage-per-block: ").append(toolComponent.getDamagePerBlock()).append('\n');

        List<ToolComponent.ToolRule> toolRules = toolComponent.getRules();
        if (!toolRules.isEmpty()) {
            stringBuilder.append("  rules:\n");
            for (int i = 0; i < toolRules.size(); i++) {
                ToolComponent.ToolRule toolRule = toolRules.get(i);
                stringBuilder.append("  ").append(i).append(":\n");
                stringBuilder.append("    speed: ").append(toolRule.getSpeed()).append('\n');
                stringBuilder.append("    correct-for-drops: ").append(toolRule.isCorrectForDrops()).append('\n');
                Collection<Material> blocks = toolRule.getBlocks();
                if (!blocks.isEmpty()) {
                    stringBuilder.append("    blocks:\n");
                    for (Material block : blocks)
                        stringBuilder.append("      - ").append(block).append('\n');
                }
            }
        }
    }

    private record ToolRuleData(StringProvider blocks, NumberProvider speed, Boolean correctForDrops) { }

}
