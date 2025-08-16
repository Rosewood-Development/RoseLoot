package dev.rosewood.roseloot.util;

import dev.rosewood.rosegarden.utils.NMSUtil;
import java.lang.reflect.Method;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

/**
 * 1.19+
 */
@SuppressWarnings("deprecation")
public final class NewerVersionUtils {

    private static final boolean HAS_REGISTRY = NMSUtil.getVersionNumber() > 21 || (NMSUtil.getVersionNumber() == 21 && NMSUtil.getMinorVersionNumber() >= 3);
    private static final boolean HAS_REGISTRY_GET_KEY = NMSUtil.isPaper() && HAS_REGISTRY;

    private NewerVersionUtils() {

    }

    @SuppressWarnings("removal") // using correct method per version, will use reflection after removal
    public static MusicInstrument getMusicInstrument(String id) {
        NamespacedKey key = NamespacedKey.fromString(id.toLowerCase());
        if (key == null)
            return null;

        if (HAS_REGISTRY)
            return Registry.INSTRUMENT.get(key);

        MusicInstrument byKey = MusicInstrument.getByKey(key);
        if (byKey != null)
            return byKey;

        return MusicInstrument.values().stream()
                .filter(x -> x.getKey().getKey().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    private static Method musicInstrumentGetKey;
    public static NamespacedKey getMusicInstrumentKey(MusicInstrument patternType) {
        if (HAS_REGISTRY_GET_KEY)
            return Registry.INSTRUMENT.getKey(patternType);

        try {
            if (musicInstrumentGetKey == null)
                musicInstrumentGetKey = MusicInstrument.class.getMethod("getKey", NamespacedKey.class);
            return (NamespacedKey) musicInstrumentGetKey.invoke(patternType);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
