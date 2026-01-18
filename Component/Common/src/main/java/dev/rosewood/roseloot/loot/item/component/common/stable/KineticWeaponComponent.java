package dev.rosewood.roseloot.loot.item.component.common.stable;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.item.component.LootItemComponent;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.provider.StringProvider;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import io.papermc.paper.datacomponent.item.KineticWeapon.Condition;
import java.util.function.Consumer;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class KineticWeaponComponent implements LootItemComponent {

    private final NumberProvider contactCooldownTicks;
    private final NumberProvider delayTicks;
    private final KineticWeaponCondition dismountConditions;
    private final KineticWeaponCondition knockbackConditions;
    private final KineticWeaponCondition damageConditions;
    private final NumberProvider forwardMovement;
    private final NumberProvider damageMultiplier;
    private final StringProvider sound;
    private final StringProvider hitSound;

    public KineticWeaponComponent(ConfigurationSection section) {
        ConfigurationSection kineticWeaponSection = section.getConfigurationSection("kinetic-weapon");
        if (kineticWeaponSection != null) {
            this.contactCooldownTicks = NumberProvider.fromSection(kineticWeaponSection, "contact-cooldown-ticks", null);
            this.delayTicks = NumberProvider.fromSection(kineticWeaponSection, "delay-ticks", null);
            this.dismountConditions = this.parseCondition(kineticWeaponSection, "dismount-conditions");
            this.knockbackConditions = this.parseCondition(kineticWeaponSection, "knockback-conditions");
            this.damageConditions = this.parseCondition(kineticWeaponSection, "damage-conditions");
            this.forwardMovement = NumberProvider.fromSection(kineticWeaponSection, "forward-movement", null);
            this.damageMultiplier = NumberProvider.fromSection(kineticWeaponSection, "damage-multiplier", null);
            this.sound = StringProvider.fromSection(kineticWeaponSection, "sound", null);
            this.hitSound = StringProvider.fromSection(kineticWeaponSection, "hit-sound", null);
        } else {
            this.contactCooldownTicks = null;
            this.delayTicks = null;
            this.dismountConditions = null;
            this.knockbackConditions = null;
            this.damageConditions = null;
            this.forwardMovement = null;
            this.damageMultiplier = null;
            this.sound = null;
            this.hitSound = null;
        }
    }

    private KineticWeaponCondition parseCondition(ConfigurationSection baseSection, String key) {
        ConfigurationSection section = baseSection.getConfigurationSection(key);
        if (section == null)
            return null;

        NumberProvider maxDurationTicks = NumberProvider.fromSection(section, "max-duration-ticks", null);
        if (maxDurationTicks == null)
            return null;

        NumberProvider minSpeed = NumberProvider.fromSection(section, "min-speed", 0.0);
        NumberProvider minRelativeSpeed = NumberProvider.fromSection(section, "min-relative-speed", 0.0);
        return new KineticWeaponCondition(maxDurationTicks, minSpeed, minRelativeSpeed);
    }

    @Override
    public void apply(ItemStack itemStack, LootContext context) {
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (this.contactCooldownTicks != null) {
            int delay = this.contactCooldownTicks.getInteger(context);
            builder.contactCooldownTicks(delay);
        }

        if (this.delayTicks != null) {
            int delay = this.delayTicks.getInteger(context);
            builder.delayTicks(delay);
        }

        this.applyCondition(context, this.dismountConditions, builder::dismountConditions);
        this.applyCondition(context, this.knockbackConditions, builder::knockbackConditions);
        this.applyCondition(context, this.damageConditions, builder::damageConditions);

        if (this.forwardMovement != null) {
            float distance = this.forwardMovement.getFloat(context);
            builder.forwardMovement(distance);
        }

        if (this.damageMultiplier != null) {
            float multiplier = this.damageMultiplier.getFloat(context);
            builder.damageMultiplier(multiplier);
        }

        if (this.sound != null)
            builder.sound(Key.key(this.sound.get(context).toLowerCase()));

        if (this.hitSound != null)
            builder.hitSound(Key.key(this.hitSound.get(context).toLowerCase()));

        itemStack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
    }

    private void applyCondition(LootContext context, KineticWeaponCondition condition, Consumer<Condition> applicator) {
        if (condition == null)
            return;

        int maxDurationTicks = condition.maxDurationTicks().getInteger(context);
        if (maxDurationTicks < 0)
            return;

        float minSpeed = condition.minSpeed().getFloat(context);
        float minRelativeSpeed = condition.minRelativeSpeed().getFloat(context);

        applicator.accept(KineticWeapon.condition(maxDurationTicks, minSpeed, minRelativeSpeed));
    }

    public static void applyProperties(ItemStack itemStack, StringBuilder stringBuilder) {
        if (!itemStack.isDataOverridden(DataComponentTypes.KINETIC_WEAPON))
            return;

        KineticWeapon kineticWeapon = itemStack.getData(DataComponentTypes.KINETIC_WEAPON);
        stringBuilder.append("kinetic-weapon:\n");
        stringBuilder.append("  contact-cooldown-ticks: ").append(kineticWeapon.contactCooldownTicks()).append('\n');
        stringBuilder.append("  delay-ticks: ").append(kineticWeapon.delayTicks()).append('\n');
        applyConditionProperties("dismount-conditions", stringBuilder, kineticWeapon.dismountConditions());
        applyConditionProperties("knockback-conditions", stringBuilder, kineticWeapon.knockbackConditions());
        applyConditionProperties("damage-conditions", stringBuilder, kineticWeapon.damageConditions());
        stringBuilder.append("  foward-movement: ").append(kineticWeapon.forwardMovement()).append('\n');

        Key sound = kineticWeapon.sound();
        if (sound != null)
            stringBuilder.append("  sound: ").append(sound.asMinimalString()).append('\n');

        Key hitSound = kineticWeapon.hitSound();
        if (hitSound != null)
            stringBuilder.append("  hit-sound: ").append(hitSound.asMinimalString()).append('\n');
    }

    private static void applyConditionProperties(String key, StringBuilder stringBuilder, Condition condition) {
        stringBuilder.append("  ").append(key).append(":\n");
        stringBuilder.append("    max-duration-ticks: ").append(condition.maxDurationTicks()).append('\n');
        stringBuilder.append("    min-speed: ").append(condition.minSpeed()).append('\n');
        stringBuilder.append("    min-relative-speed: ").append(condition.minRelativeSpeed()).append('\n');
    }

    private record KineticWeaponCondition(NumberProvider maxDurationTicks,
                                          NumberProvider minSpeed,
                                          NumberProvider minRelativeSpeed) { }

} 
