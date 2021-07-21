package dev.rosewood.roseloot.locale;

import dev.rosewood.rosegarden.locale.Locale;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {

    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "Esophose";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<String, Object>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&7[<g:#8A2387:#E94057:#F27121>RoseLoot&7] ");

            this.put("#1", "No Permission Message");
            this.put("no-permission", "&cYou don't have permission for that!");

            this.put("#2", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUse &b/rl help &efor command information.");

            this.put("#3", "Help Command");
            this.put("command-help-description", "&8 - &d/rl help &7- Displays the help menu... You have arrived");
            this.put("command-help-title", "&eAvailable Commands:");

            this.put("#4", "Reload Command");
            this.put("command-reload-description", "&8 - &d/rl reload &7- Reloads the plugin");
            this.put("command-reload-reloaded", "&eConfiguration and locale files were reloaded.");
        }};
    }

}
