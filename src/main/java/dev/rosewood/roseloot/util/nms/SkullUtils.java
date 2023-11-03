package dev.rosewood.roseloot.util.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.rosewood.rosegarden.utils.NMSUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

public final class SkullUtils {

    private static Method method_SkullMeta_setProfile;
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

        if (NMSUtil.getVersionNumber() >= 18) { // No need to use NMS on 1.18.1+
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures textures = profile.getTextures();

            String decodedTextureJson = new String(Base64.getDecoder().decode(texture));
            String decodedTextureUrl = decodedTextureJson.substring(28, decodedTextureJson.length() - 4);

            try {
                textures.setSkin(new URL(decodedTextureUrl));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            skullMeta.setOwnerProfile(profile);
            return;
        }

        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "");
        profile.getProperties().put("textures", new Property("textures", texture));

        try {
            if (NMSUtil.getVersionNumber() > 15) {
                if (method_SkullMeta_setProfile == null) {
                    method_SkullMeta_setProfile = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                    method_SkullMeta_setProfile.setAccessible(true);
                }

                method_SkullMeta_setProfile.invoke(skullMeta, profile);
            } else {
                if (field_SkullMeta_profile == null) {
                    field_SkullMeta_profile = skullMeta.getClass().getDeclaredField("profile");
                    field_SkullMeta_profile.setAccessible(true);
                }

                field_SkullMeta_profile.set(skullMeta, profile);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

}
