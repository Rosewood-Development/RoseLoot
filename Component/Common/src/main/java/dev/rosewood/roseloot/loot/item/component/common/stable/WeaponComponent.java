package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Weapon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class WeaponComponent implements LootItemComponent {

    private final NumberProvider itemDamagePerAttack;
    private final NumberProvider disableBlockingForSeconds;

    public WeaponComponent(ConfigurationSection section) {
        ConfigurationSection weaponSection = section.getConfigurationSection("weapon");
        if (weaponSection != null) {
            this.itemDamagePerAttack = NumberProvider.fromSection(weaponSection, "item-damage-per-attack", null);
            this.disableBlockingForSeconds = NumberProvider.fromSection(weaponSection, "disable-blocking-for-seconds", null);
        } else {
            this.itemDamagePerAttack = null;
            this.disableBlockingForSeconds = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        Weapon.Builder builder = Weapon.weapon();

        if (this.itemDamagePerAttack != null) {
            int damage = this.itemDamagePerAttack.getInteger(context);
            builder.itemDamagePerAttack(damage);
        }

        if (this.disableBlockingForSeconds != null) {
            float seconds = this.disableBlockingForSeconds.getFloat(context);
            builder.disableBlockingForSeconds(seconds);
        }

        itemStack.setData(DataComponentTypes.WEAPON, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.hasData(DataComponentTypes.WEAPON))
            return;

        Weapon weapon = itemStack.getData(DataComponentTypes.WEAPON);
        stringBuilder.append("tool:\n");
        stringBuilder.append("  item-damage-per-attack: ").append(weapon.itemDamagePerAttack()).append('\n');
        stringBuilder.append("  disable-blocking-for-seconds: ").append(weapon.disableBlockingForSeconds()).append('\n');
    }

} 
