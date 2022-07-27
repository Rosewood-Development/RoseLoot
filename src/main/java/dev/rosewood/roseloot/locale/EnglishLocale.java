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
        return new LinkedHashMap<>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&7[<g:#8A2387:#E94057:#F27121>RoseLoot&7] ");

            this.put("#1", "Generic Command Messages");
            this.put("no-permission", "&cYou don't have permission for that!");
            this.put("only-player", "&cThis command can only be executed by a player.");
            this.put("unknown-command", "&cUnknown command, use &b/%cmd% help &cfor more info.");
            this.put("unknown-command-error", "&cAn unknown error occurred; details have been printed to console. Please contact a server administrator.");
            this.put("invalid-subcommand", "&cInvalid subcommand.");
            this.put("invalid-argument", "&cInvalid argument: %message%.");
            this.put("invalid-argument-null", "&cInvalid argument: %name% was null.");
            this.put("missing-arguments", "&cMissing arguments, &b%amount% &crequired.");
            this.put("missing-arguments-extra", "&cMissing arguments, &b%amount%+ &crequired.");

            this.put("#2", "Base Command Message");
            this.put("base-command-color", "&e");
            this.put("base-command-help", "&eUse &b/%cmd% help &efor command information.");

            this.put("#3", "Help Command");
            this.put("command-help-description", "Displays the help menu... You have arrived");
            this.put("command-help-title", "&eAvailable Commands:");
            this.put("command-help-list-description", "&8 - &d/%cmd% %subcmd% %args% &7- %desc%");
            this.put("command-help-list-description-no-args", "&8 - &d/%cmd% %subcmd% &7- %desc%");
            this.put("command-help-wiki", "&9&o&nClick here to learn how to create loot tables.");
            this.put("command-help-wiki-hover", "&7%url%");

            this.put("#4", "Reload Command");
            this.put("command-reload-description", "Reloads the plugin");
            this.put("command-reload-reloaded", "&eConfiguration and locale files were reloaded.");

            this.put("#5", "Generate Command");
            this.put("command-generate-description", "Runs a loot table");
            this.put("command-generate-requires-player", "&cYou must specify a player when running this command through the console.");
            this.put("command-generate-invalid-loot-table-type", "&cOnly loot tables of type LOOT_TABLE can be used with this command.");
            this.put("command-generate-success", "&eLoot was generated for &b%player%&e using &b%loottable%&e.");

            this.put("#6", "List Command");
            this.put("command-list-description", "Display a list of active loot tables");
            this.put("command-list-none", "&eThere are no loot tables currently active.");
            this.put("command-list-header", "&eThere are &b%amount% &eloot tables currently active:");
            this.put("command-list-hierarchy-spacer", "  ");
            this.put("command-list-hierarchy-branch", "%spacer%&3%name%/");
            this.put("command-list-hierarchy-leaf", "%spacer%&b%name% &8- &7%type%");

            this.put("#7", "Copy Command");
            this.put("command-copy-description", "Get the item in your hand as a loot item");
            this.put("command-copy-success", "&eClick to copy the loot table entry to your clipboard.");
            this.put("command-copy-hover", "&7Click to copy");
            this.put("command-copy-no-item", "&cYou must be holding an item in your main hand to use this command.");

            this.put("#8", "Give Items Command");
            this.put("command-giveitems-description", "Gives all items listed in a loot table");
            this.put("command-giveitems-requires-player", "&cYou must specify a player when running this command through the console.");
            this.put("command-giveitems-success", "&eLoot items were given to &b%player%&e using &b%loottable%&e.");
            this.put("command-giveitems-empty", "&eThere are no valid items in the loot table.");

            this.put("#9", "Voucher Messages");
            this.put("voucher-expired", "&cThis voucher has either expired or was improperly configured and can no longer be redeemed. Please contact a server administrator.");

            this.put("#10", "Argument Handler Error Messages");
            this.put("argument-handler-loot-table", "LootTable [%input%] does not exist");
            this.put("argument-handler-player", "No Player with the username [%input%] was found online");
        }};
    }

}
