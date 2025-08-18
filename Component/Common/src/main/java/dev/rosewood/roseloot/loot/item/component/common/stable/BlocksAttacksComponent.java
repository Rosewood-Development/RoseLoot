package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.loot.item.component.common.ParsingUtils;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.tag.Tag;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemStack;

public class BlocksAttacksComponent implements LootItemComponent {

    private final NumberProvider blockDelaySeconds;
    private final NumberProvider disableCooldownScale;
    private final List<DamageReductionData> damageReductions;
    private final ItemDamageData itemDamage;
    private final StringProvider bypassedBy;
    private final StringProvider blockSound;
    private final StringProvider disabledSound;

    public BlocksAttacksComponent(ConfigurationSection section) {
        ConfigurationSection blocksAttacksSection = section.getConfigurationSection("blocks-attacks");
        if (blocksAttacksSection != null) {
            this.blockDelaySeconds = NumberProvider.fromSection(blocksAttacksSection, "block-delay-seconds", null);
            this.disableCooldownScale = NumberProvider.fromSection(blocksAttacksSection, "disable-cooldown-scale", null);

            this.damageReductions = new ArrayList<>();
            ConfigurationSection damageReductionsSection = blocksAttacksSection.getConfigurationSection("damage-reductions");
            if (damageReductionsSection != null) {
                for (String key : damageReductionsSection.getKeys(false)) {
                    ConfigurationSection damageReductionSection = damageReductionsSection.getConfigurationSection(key);
                    if (damageReductionSection == null)
                        continue;

                    StringProvider type = StringProvider.fromSection(damageReductionSection, "type", null);
                    NumberProvider horizontalBlockingAngle = NumberProvider.fromSection(damageReductionSection, "horizontal-blocking-angle", null);
                    NumberProvider base = NumberProvider.fromSection(damageReductionSection, "base", null);
                    NumberProvider factor = NumberProvider.fromSection(damageReductionSection, "factor", null);
                    this.damageReductions.add(new DamageReductionData(type, horizontalBlockingAngle, base, factor));
                }
            }

            ConfigurationSection itemDamageSection = blocksAttacksSection.getConfigurationSection("item-damage");
            if (itemDamageSection != null) {
                NumberProvider threshold = NumberProvider.fromSection(itemDamageSection, "threshold", null);
                NumberProvider base = NumberProvider.fromSection(itemDamageSection, "base", null);
                NumberProvider factor = NumberProvider.fromSection(itemDamageSection, "factor", null);
                this.itemDamage = new ItemDamageData(threshold, base, factor);
            } else {
                this.itemDamage = null;
            }

            this.bypassedBy = StringProvider.fromSection(blocksAttacksSection, "bypassed-by", null);
            this.blockSound = StringProvider.fromSection(blocksAttacksSection, "block-sound", null);
            this.disabledSound = StringProvider.fromSection(blocksAttacksSection, "disabled-sound", null);
        } else {
            this.blockDelaySeconds = null;
            this.disableCooldownScale = null;
            this.damageReductions = null;
            this.itemDamage = null;
            this.bypassedBy = null;
            this.blockSound = null;
            this.disabledSound = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks();

        if (this.blockDelaySeconds != null)
            builder.blockDelaySeconds(this.blockDelaySeconds.getFloat(context));

        if (this.disableCooldownScale != null)
            builder.disableCooldownScale(disableCooldownScale.getFloat(context));

        if (this.damageReductions != null)
            for (DamageReductionData damageReductionData : this.damageReductions)
                builder.addDamageReduction(damageReductionData.toDamageReduction(context));

        if (this.itemDamage != null)
            builder.itemDamage(this.itemDamage.toItemDamageFunction(context));

        if (this.bypassedBy != null)
            builder.bypassedBy(ParsingUtils.parseRegistryTag(this.bypassedBy, RegistryKey.DAMAGE_TYPE, context));

        if (this.blockSound != null)
            builder.blockSound(Key.key(this.blockSound.get(context)));

        if (this.disabledSound != null)
            builder.disableSound(Key.key(this.disabledSound.get(context)));

        itemStack.setData(DataComponentTypes.BLOCKS_ATTACKS, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.BLOCKS_ATTACKS))
            return;

        BlocksAttacks blocksAttacks = itemStack.getData(DataComponentTypes.BLOCKS_ATTACKS);
        stringBuilder.append("blocks-attacks:\n");
        stringBuilder.append("  block-delay-seconds: ").append(blocksAttacks.blockDelaySeconds()).append('\n');
        stringBuilder.append("  disable-cooldown-scale: ").append(blocksAttacks.disableCooldownScale()).append('\n');

        List<DamageReduction> damageReductions = blocksAttacks.damageReductions();
        if (!damageReductions.isEmpty()) {
            stringBuilder.append("  damage-reductions:\n");
            int i = 0;
            for (DamageReduction damageReduction : damageReductions) {
                stringBuilder.append("    ").append(i++).append(":\n");
                RegistryKeySet<DamageType> keySet = damageReduction.type();
                if (keySet instanceof Tag<DamageType> tag) {
                    String name = tag.tagKey().key().asMinimalString();
                    stringBuilder.append("      ").append("type: '#").append(name).append("'\n");
                } else {
                    stringBuilder.append("      ").append("type:\n");
                    for (TypedKey<DamageType> typedKey : keySet.values()) {
                        String name = typedKey.key().asMinimalString();
                        stringBuilder.append("        - '").append(name).append("'\n");
                    }
                }
            }
        }

        ItemDamageFunction itemDamageFunction = blocksAttacks.itemDamage();
        stringBuilder.append("  item-damage:\n");
        stringBuilder.append("    threshold: ").append(itemDamageFunction.threshold()).append('\n');
        stringBuilder.append("    base: ").append(itemDamageFunction.base()).append('\n');
        stringBuilder.append("    factor: ").append(itemDamageFunction.factor()).append('\n');

        stringBuilder.append("  bypassed-by: '#").append(blocksAttacks.bypassedBy().key().asMinimalString()).append("'\n");
        stringBuilder.append("  block-sound: '").append(blocksAttacks.blockSound().asMinimalString()).append("'\n");
        stringBuilder.append("  disabled-sound: '").append(blocksAttacks.disableSound().asMinimalString()).append("'\n");
    }

    private record DamageReductionData(StringProvider type,
                                       NumberProvider horizontalBlockingAngle,
                                       NumberProvider base,
                                       NumberProvider factor) {

        public DamageReduction toDamageReduction(LootContext context) {
            DamageReduction.Builder builder = DamageReduction.damageReduction();

            if (this.type != null)
                builder.type(ParsingUtils.parseRegistryTags(this.type, RegistryKey.DAMAGE_TYPE, context));

            if (this.horizontalBlockingAngle != null) {
                float horizontalBlockingAngle = this.horizontalBlockingAngle.getFloat(context);
                if (horizontalBlockingAngle > 0)
                    builder.horizontalBlockingAngle(horizontalBlockingAngle);
            }

            if (this.base != null)
                builder.base(this.base.getFloat(context));

            if (this.factor != null)
                builder.factor(this.factor.getFloat(context));

            return builder.build();
        }

    }

    private record ItemDamageData(NumberProvider threshold,
                                  NumberProvider base,
                                  NumberProvider factor) {

        public ItemDamageFunction toItemDamageFunction(LootContext context) {
            ItemDamageFunction.Builder builder = ItemDamageFunction.itemDamageFunction();

            if (this.threshold != null) {
                float threshold = this.threshold.getFloat(context);
                if (threshold >= 0)
                    builder.threshold(threshold);
            }

            if (this.base != null)
                builder.base(this.base.getFloat(context));

            if (this.factor != null)
                builder.factor(this.factor.getFloat(context));

            return builder.build();
        }

    }

}
