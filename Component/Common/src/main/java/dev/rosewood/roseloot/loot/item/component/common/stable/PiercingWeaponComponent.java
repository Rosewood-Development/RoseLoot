package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PiercingWeaponComponent implements LootItemComponent {

    private final boolean dealsKnockback;
    private final boolean dismounts;
    private final StringProvider sound;
    private final StringProvider hitSound;

    public PiercingWeaponComponent(ConfigurationSection section) {
        ConfigurationSection piercingWeaponSection = section.getConfigurationSection("piercing-weapon");
        if (piercingWeaponSection != null) {
            this.dealsKnockback = piercingWeaponSection.getBoolean("deals-knockback", true);
            this.dismounts = piercingWeaponSection.getBoolean("dismounts", false);
            this.sound = StringProvider.fromSection(piercingWeaponSection, "sound", null);
            this.hitSound = StringProvider.fromSection(piercingWeaponSection, "hit-sound", null);
        } else {
            this.dealsKnockback = true;
            this.dismounts = false;
            this.sound = null;
            this.hitSound = null;
        }
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon();

        builder.dealsKnockback(this.dealsKnockback);
        builder.dismounts(this.dismounts);

        if (this.sound != null)
            builder.sound(Key.key(this.sound.get(context).toLowerCase()));

        if (this.hitSound != null)
            builder.hitSound(Key.key(this.hitSound.get(context).toLowerCase()));

        itemStack.setData(DataComponentTypes.PIERCING_WEAPON, builder.build());
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.PIERCING_WEAPON))
            return;

        PiercingWeapon piercingWeapon = itemStack.getData(DataComponentTypes.PIERCING_WEAPON);
        stringBuilder.append("piercing-weapon:\n");
        stringBuilder.append("  deals-knockback: ").append(piercingWeapon.dealsKnockback()).append('\n');
        stringBuilder.append("  dismounts: ").append(piercingWeapon.dismounts()).append('\n');

        Key sound = piercingWeapon.sound();
        if (sound != null)
            stringBuilder.append("  sound: ").append(sound.asMinimalString()).append('\n');

        Key hitSound = piercingWeapon.hitSound();
        if (hitSound != null)
            stringBuilder.append("  hit-sound: ").append(hitSound.asMinimalString()).append('\n');
    }

} 
