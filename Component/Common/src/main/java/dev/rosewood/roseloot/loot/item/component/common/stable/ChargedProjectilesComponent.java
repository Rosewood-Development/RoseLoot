package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.loot.LootContents;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.LootItem;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.loot.item.meta.ItemLootMeta;
import dev.rosewood.roseloot.manager.LootTableManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ChargedProjectilesComponent implements LootItemComponent {

    private final List<LootItem> projectiles;

    public ChargedProjectilesComponent(ConfigurationSection section) {
        ConfigurationSection chargedProjectilesSection = section.getConfigurationSection("charged-projectiles");
        if (chargedProjectilesSection != null) {
            this.projectiles = new ArrayList<>();
            for (String key : chargedProjectilesSection.getKeys(false)) {
                ConfigurationSection projectileSection = chargedProjectilesSection.getConfigurationSection(key);
                if (projectileSection != null) {
                    LootItem lootItem = RoseLoot.getInstance().getManager(LootTableManager.class).parseLootItem("$internal", "none", "charged-projectile", key, projectileSection);
                    if (lootItem != null) {
                        this.projectiles.add(lootItem);
                    } else {
                        RoseLoot.getInstance().getLogger().warning("Ignoring invalid charged-projectile item: " + key);
                    }
                }
            }
        } else {
            this.projectiles = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        ChargedProjectiles.Builder builder = ChargedProjectiles.chargedProjectiles();

        if (this.projectiles != null) {
            LootContents lootContents = new LootContents(context);
            lootContents.add(this.projectiles);
            builder.addAll(lootContents.getItems());
        }

        itemStack.setData(DataComponentTypes.CHARGED_PROJECTILES, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.CHARGED_PROJECTILES))
            return;

        ChargedProjectiles chargedProjectiles = itemStack.getData(DataComponentTypes.CHARGED_PROJECTILES);
        if (!chargedProjectiles.projectiles().isEmpty()) {
            stringBuilder.append("charged-projectiles:\n");

            for (int i = 0; i < chargedProjectiles.projectiles().size(); i++) {
                stringBuilder.append("  ").append(i).append(":\n");
                StringBuilder subBuilder = new StringBuilder();
                ItemLootMeta.applyComponentProperties(chargedProjectiles.projectiles().get(i), subBuilder);
                stringBuilder.append(subBuilder.toString().indent(4));
            }
        }
    }

} 
