package dev.rosewood.roseloot.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.UUID;
import org.bukkit.inventory.meta.SkullMeta;

public final class SkullUtils {

    private static Field field_SkullMeta_profile;

    private SkullUtils() {

    }

    /**
     * Applies a base64 encoded texture to an item's SkullMeta
     *
     * @param skullMeta The ItemMeta for the Skull
     * @param texture The texture to apply to the skull
     */
    public static void setSkullTexture(SkullMeta skullMeta, String texture) {
        if (texture == null || texture.isEmpty())
            return;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));

        try {
            if (field_SkullMeta_profile == null) {
                field_SkullMeta_profile = skullMeta.getClass().getDeclaredField("profile");
                field_SkullMeta_profile.setAccessible(true);
            }

            field_SkullMeta_profile.set(skullMeta, profile);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

}
