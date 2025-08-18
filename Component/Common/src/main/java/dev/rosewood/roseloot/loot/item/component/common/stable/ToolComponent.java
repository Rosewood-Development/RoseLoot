package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ToolComponent implements LootItemComponent {

    private final NumberProvider defaultMiningSpeed;
    private final NumberProvider damagePerBlock;
    private final Boolean canDestroyBlocksInCreative;
    private final List<RuleConfig> rules;

    public ToolComponent(ConfigurationSection section) {
        ConfigurationSection toolSection = section.getConfigurationSection("tool");
        if (toolSection != null) {
            this.defaultMiningSpeed = NumberProvider.fromSection(toolSection, "default-mining-speed", null);
            this.damagePerBlock = NumberProvider.fromSection(toolSection, "damage-per-block", null);

            if (toolSection.isBoolean("can-destroy-blocks-in-creative")) {
                this.canDestroyBlocksInCreative = toolSection.getBoolean("can-destroy-blocks-in-creative", true);
            } else {
                this.canDestroyBlocksInCreative = null;
            }

            this.rules = new ArrayList<>();
            if (toolSection.contains("rules")) {
                for (String key : toolSection.getConfigurationSection("rules").getKeys(false)) {
                    ConfigurationSection ruleSection = toolSection.getConfigurationSection("rules." + key);
                    if (ruleSection != null) {
                        this.rules.add(new RuleConfig(
                            StringProvider.fromSection(ruleSection, "blocks", null),
                            NumberProvider.fromSection(ruleSection, "speed", null),
                            ruleSection.getString("correct-for-drops")
                        ));
                    }
                }
            }
        } else {
            this.defaultMiningSpeed = null;
            this.damagePerBlock = null;
            this.canDestroyBlocksInCreative = null;
            this.rules = new ArrayList<>();
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        Tool.Builder builder = Tool.tool();

        if (this.defaultMiningSpeed != null) {
            float speed = this.defaultMiningSpeed.getFloat(context);
            builder.defaultMiningSpeed(speed);
        }

        if (this.damagePerBlock != null) {
            int damage = this.damagePerBlock.getInteger(context);
            builder.damagePerBlock(damage);
        }

        if (this.canDestroyBlocksInCreative != null)
            builder.canDestroyBlocksInCreative(this.canDestroyBlocksInCreative);

        if (!this.rules.isEmpty()) {
            List<Tool.Rule> toolRules = new ArrayList<>();
            Registry<BlockType> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK);
            
            for (RuleConfig rule : this.rules) {
                String blocksString = rule.blocks.get(context);
                if (blocksString != null && !blocksString.isEmpty()) {
                    RegistryKeySet<BlockType> blocks;
                    if (blocksString.startsWith("#")) {
                        TagKey<BlockType> tagKey = TagKey.create(RegistryKey.BLOCK, Key.key(blocksString.substring(1)));
                        blocks = registry.getTag(tagKey);
                    } else {
                        BlockType blockType = registry.get(Key.key(blocksString));
                        if (blockType != null) {
                            blocks = RegistrySet.keySetFromValues(RegistryKey.BLOCK, List.of(blockType));
                        } else {
                            continue;
                        }
                    }

                    Float speed = null;
                    if (rule.speed != null) {
                        Double speedValue = rule.speed.getDouble(context);
                        if (speedValue != null) {
                            speed = speedValue.floatValue();
                        }
                    }

                    TriState correctForDrops = TriState.NOT_SET;
                    if (rule.correctForDrops != null) {
                        switch (rule.correctForDrops.toLowerCase()) {
                            case "true" -> correctForDrops = TriState.TRUE;
                            case "false" -> correctForDrops = TriState.FALSE;
                        }
                    }

                    toolRules.add(Tool.rule(blocks, speed, correctForDrops));
                }
            }
            
            if (!toolRules.isEmpty()) {
                builder.addRules(toolRules);
            }
        }

        itemStack.setData(DataComponentTypes.TOOL, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.TOOL))
            return;

        Tool tool = itemStack.getData(DataComponentTypes.TOOL);
        stringBuilder.append("tool:\n");
        stringBuilder.append("  default-mining-speed: ").append(tool.defaultMiningSpeed()).append('\n');
        stringBuilder.append("  damage-per-block: ").append(tool.damagePerBlock()).append('\n');
        stringBuilder.append("  can-destroy-blocks-in-creative: ").append(tool.canDestroyBlocksInCreative()).append('\n');
        
        if (!tool.rules().isEmpty()) {
            stringBuilder.append("  rules:\n");
            int i = 0;
            for (Tool.Rule rule : tool.rules()) {
                stringBuilder.append("    ").append(i++).append(":\n");
                if (rule.blocks() instanceof Tag<?> tag) {
                    stringBuilder.append("      blocks: '").append(tag.tagKey().key().asMinimalString()).append("'\n");
                } else {
                    stringBuilder.append("      blocks:\n");
                    for (TypedKey<BlockType> typedKey : rule.blocks().values())
                        stringBuilder.append("        - '").append(typedKey.key().asMinimalString()).append("'\n");
                }
                if (rule.speed() != null)
                    stringBuilder.append("      speed: ").append(rule.speed()).append('\n');
                stringBuilder.append("      correct-for-drops: ").append(rule.correctForDrops().name().toLowerCase()).append('\n');
            }
        }
    }

    private record RuleConfig(
        StringProvider blocks,
        NumberProvider speed,
        String correctForDrops
    ) {}

} 
