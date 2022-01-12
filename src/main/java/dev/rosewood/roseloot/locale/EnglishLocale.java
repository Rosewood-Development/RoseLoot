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

            this.put("#1", "Generic Command Messages");
            this.put("no-permission", "&cYou don't have permission for that!");
            this.put("command-description", "&8 - &d/rl %cmd% %args% &7- %desc%");
            this.put("only-player", "&cThis command can only be executed by a player.");
            this.put("unknown-command", "&cUnknown command, use &b/rl help &cfor more info.");
            this.put("invalid-subcommand", "&cInvalid subcommand.");
            this.put("invalid-arguments-header", "&cInvalid arguments:");
            this.put("invalid-argument-header", "&cInvalid argument:");
            this.put("invalid-argument", " &8- &7%message%");
            this.put("missing-arguments", "&cMissing arguments, &b%amount% &crequired.");
            this.put("missing-arguments-extra", "&cMissing arguments, &b%amount%+ &crequired.");

            this.put("#2", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUse &b/rl help &efor command information.");

            this.put("#3", "Help Command");
            this.put("command-help-description", "Displays the help menu... You have arrived");
            this.put("command-help-title", "&eAvailable Commands:");

            this.put("#4", "Reload Command");
            this.put("command-reload-description", "Reloads the plugin");
            this.put("command-reload-reloaded", "&eConfiguration, locale files, and loot tables were reloaded.");

            this.put("#5", "Test Command");
            this.put("command-test-description", "Simulates a loot table");

            this.put("#6", "Generate Command");
            this.put("command-generate-description", "Runs a loot table");

            this.put("#7", "List Command");
            this.put("command-list-description", "Display a list of active loot tables");
            this.put("command-list-none", "&eThere are no loot tables currently active.");
            this.put("command-list-header", "&eThere are &b%amount% &eloot tables currently active:");
            this.put("command-list-hierarchy-spacer", "  ");
            this.put("command-list-hierarchy-branch", "%spacer%&3%name%/");
            this.put("command-list-hierarchy-leaf", "%spacer%&b%name% &8- &7%type%");

            this.put("#8", "Copy Command");
            this.put("command-copy-description", "Get the item in your hand as a loot item");
            this.put("command-copy-success", "&eClick to copy the loot table entry to your clipboard.");
            this.put("command-copy-hover", "&7Click to copy");
            this.put("command-copy-no-item", "&cYou must be holding an item in your main hand to use this command.");
        }};
    }

}
