package dev.rosewood.roseloot.loot.item;

import dev.rosewood.rosegarden.utils.NMSUtil;
import dev.rosewood.roseloot.loot.RelativeTo;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.loot.context.LootContextParams;
import dev.rosewood.roseloot.provider.NumberProvider;
import dev.rosewood.roseloot.util.EnumHelper;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class BreakBlockLootItem implements TriggerableLootItem {

    private static final boolean PAPER_EXPERIENCE = NMSUtil.isPaper() && NMSUtil.getVersionNumber() >= 19;
    private static final boolean PAPER_TRIGGER_EFFECTS = NMSUtil.isPaper() && NMSUtil.getVersionNumber() >= 17;

    private final RelativeTo relativeTo;
    private final NumberProvider xOffset;
    private final NumberProvider yOffset;
    private final NumberProvider zOffset;
    private final boolean breakAsLooter;
    private final boolean replace;
    private final boolean triggerEffects;

    protected BreakBlockLootItem(RelativeTo relativeTo, NumberProvider xOffset, NumberProvider yOffset, NumberProvider zOffset, boolean breakAsLooter, boolean replace, boolean triggerEffects) {
        this.relativeTo = relativeTo;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.breakAsLooter = breakAsLooter;
        this.replace = replace;
        this.triggerEffects = triggerEffects;
    }

    @Override
    public void trigger(LootContext context, Location location) {
        Optional<Entity> looter = context.get(LootContextParams.LOOTER);
        Location blockLocation = switch (this.relativeTo) {
            case LOOTER -> looter.map(Entity::getLocation).orElse(null);
            default -> location;
        };

        if (blockLocation == null)
            return;

        blockLocation = blockLocation.clone().add(this.xOffset.getDouble(context), this.yOffset.getDouble(context), this.zOffset.getDouble(context));

        Block block = blockLocation.getBlock();
        Material type = block.getType();
        if (type == Material.AIR || type == Material.WATER || type == Material.LAVA)
            return;

        boolean waterlogged = block.getBlockData() instanceof Waterlogged data && data.isWaterlogged();
        if (this.replace) {
            block.setType(waterlogged ? Material.WATER : Material.AIR);
            return;
        }

        if (this.breakAsLooter) {
            if (looter.isPresent() && looter.get() instanceof LivingEntity livingLooter) {
                EntityEquipment equipment = livingLooter.getEquipment();
                ItemStack tool = null;
                if (equipment != null)
                    tool = livingLooter.getEquipment().getItemInMainHand();

                if (livingLooter instanceof Player player) {
                    BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
                    Bukkit.getPluginManager().callEvent(blockBreakEvent);
                    if (blockBreakEvent.isCancelled())
                        return;

                    if (blockBreakEvent.isDropItems()) {
                        this.breakNaturally(block, tool, true);
                    } else {
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, type);
                        block.setType(waterlogged ? Material.WATER : Material.AIR);
                    }
                } else {
                    block.breakNaturally(tool);
                }
            }
        } else {
            this.breakNaturally(block, null, false);
        }
    }

    private void breakNaturally(Block block, ItemStack tool, boolean player) {
        if (tool == null) {
            if (PAPER_EXPERIENCE && player) {
                block.breakNaturally(this.triggerEffects, true);
            } else if (PAPER_TRIGGER_EFFECTS) {
                block.breakNaturally(this.triggerEffects);
            } else {
                block.breakNaturally();
            }
        } else {
            if (PAPER_EXPERIENCE && player) {
                block.breakNaturally(tool, this.triggerEffects, true);
            } else if (PAPER_TRIGGER_EFFECTS) {
                block.breakNaturally(tool, this.triggerEffects);
            } else {
                block.breakNaturally(tool);
            }
        }
    }

    public static BreakBlockLootItem fromSection(ConfigurationSection section) {
        RelativeTo relativeTo = EnumHelper.valueOf(RelativeTo.class, section.getString("relative-to"), RelativeTo.LOOTED);
        NumberProvider x = NumberProvider.fromSection(section, "x", 0);
        NumberProvider y = NumberProvider.fromSection(section, "y", 0);
        NumberProvider z = NumberProvider.fromSection(section, "z", 0);
        boolean breakAsLooter = section.getBoolean("break-as-looter", false);
        boolean replace = section.getBoolean("replace", false);
        boolean triggerEffects = section.getBoolean("trigger-effects", false);
        return new BreakBlockLootItem(relativeTo, x, y, z, breakAsLooter, replace, triggerEffects);
    }

}
