package dev.rosewood.roseloot.manager;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import dev.rosewood.roseloot.RoseLoot;
import java.util.Collections;

public class ConfigurationManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        DISABLED_WORLDS("disabled-worlds", Collections.singletonList("disabled_world_name"), "A list of worlds that the plugin is disabled in"),
        ALLOW_BLOCK_EXPLOSION_LOOT("allow-block-explosion-loot", false, "If enabled, blocks destroyed by explosions will run loot tables", "You can use the condition 'explosion' to check for an explosion", "Avoid using item based conditions for explosions, you may get unexpected results", "WARNING: Do not trigger an explosion from a loot table triggered by an explosion.", "         It will likely cause an infinite loop and crash your server.");

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return RoseLoot.getInstance().getManager(ConfigurationManager.class).getConfig();
        }
    }

    public ConfigurationManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }

    @Override
    protected String[] getHeader() {
        return new String[] {
                "     __________                     ____                  __",
                "     \\______   \\ ____  ______ ____ |    |    ____   _____/  |_",
                "      |       _//  _ \\/  ___// __ \\|    |   /  _ \\ /  _ \\   __\\",
                "      |    |   (  <_> )___ \\\\  ___/|    |__(  <_> |  <_> )  |",
                "      |____|_  /\\____/____  >\\___  >_______ \\____/ \\____/|__|",
                "             \\/           \\/     \\/        \\/"
        };
    }

}
