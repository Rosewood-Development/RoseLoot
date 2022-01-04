package dev.rosewood.roseloot.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.rosewood.roseloot.RoseLoot;
import dev.rosewood.roseloot.manager.LootConditionManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.loot.LootTables;
import org.bukkit.map.MapView;

public class VanillaLootTableConverter {

    public static void convert(File directory) {
        File vanillaDirectory = new File(directory, "vanilla");
        vanillaDirectory.mkdirs();

        for (LootTables lootTables : LootTables.values()) {
            if (lootTables == LootTables.EMPTY)
                continue;

            String path = lootTables.getKey().getKey();
            handle(vanillaDirectory, path);
        }

        for (Material material : Material.values())
            handle(vanillaDirectory, "blocks/" + material.name().toLowerCase());
    }

    private static void handle(File directory, String path) {
        File destination = new File(directory, path + ".yml");
        if (destination.exists())
            return;

        try (InputStream inputStream = Bukkit.class.getClassLoader().getResourceAsStream("data/minecraft/loot_tables/" + path + ".json")) {
            if (inputStream == null)
                return;

            JsonElement json = new JsonParser().parse(new InputStreamReader(inputStream));

            destination.getParentFile().mkdirs();
            destination.createNewFile();

            try (FileWriter fileWriter = new FileWriter(destination)) {
                IndentedFileWriter writer = new IndentedFileWriter(fileWriter);
                if (path.startsWith("entities") && !path.equals("entities/sheep")) {
                    writeEntityHeader(path, writer);
                } else if (path.startsWith("blocks")) {
                    writeBlockHeader(path, writer);
                } else if (path.equals("gameplay/fishing")) {
                    writeFishingHeader(path, writer);
                } else if (path.startsWith("chests")) {
                    writeContainerHeader(path, writer);
                } else if (path.equals("gameplay/piglin_bartering")) {
                    writePiglinBarteringHeader(path, writer);
                } else if (path.equals("gameplay/cat_morning_gift") || path.startsWith("gameplay/hero_of_the_village")) {
                    writeEntityDropsHeader(path, writer);
                } else {
                    writeLootTableHeader(path, writer);
                }

                writeTableContents(path, writer, json.getAsJsonObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeEntityHeader(String path, IndentedFileWriter writer) throws IOException {
        writer.write("type: ENTITY");
        writer.write("overwrite-existing: items");
        writer.write("conditions:");
        writer.increaseIndentation();
        String entityType = path.substring("entities/".length());
        entityType = fixEntityNames(entityType);
        if (entityType.startsWith("sheep")) {
            writer.write("- 'entity-type:sheep'");
            writer.write("- 'sheep-color:" + entityType.substring("sheep/".length()) + "'");
        } else {
            writer.write("- 'entity-type:" + entityType + "'");
        }
        writer.decreaseIndentation();
    }

    private static void writeBlockHeader(String path, IndentedFileWriter writer) throws IOException {
        writer.write("type: BLOCK");
        writer.write("overwrite-existing: items");
        writer.write("conditions:");
        writer.increaseIndentation();
        writer.write("- 'block-type:" + path.substring("blocks/".length()) + "'");
        writer.decreaseIndentation();
    }

    private static void writeFishingHeader(String path, IndentedFileWriter writer) throws IOException {
        writer.write("type: FISHING");
        writer.write("overwrite-existing: items");
        writer.write("conditions: []");
    }

    private static void writeContainerHeader(String path, IndentedFileWriter writer) throws IOException {
        writer.write("type: CONTAINER");
        writer.write("overwrite-existing: items");
        writer.write("conditions:");
        writer.increaseIndentation();
        writer.write("- 'vanilla-loot-table:" + path + "'");
        writer.decreaseIndentation();
    }

    private static void writePiglinBarteringHeader(String path, IndentedFileWriter writer) throws IOException {
        writer.write("type: PIGLIN_BARTER");
        writer.write("overwrite-existing: items");
        writer.write("conditions: []");
    }

    private static void writeEntityDropsHeader(String path, IndentedFileWriter writer) throws IOException {
        writer.write("type: ENTITY_DROP_ITEM");
        writer.write("overwrite-existing: items");
        writer.write("conditions:");
        writer.increaseIndentation();
        if (path.startsWith("gameplay/cat_morning_gift")) {
            writer.write("- 'entity-type:cat'");
        } else {
            writer.write("- 'entity-type:villager'");
            writer.write("- 'villager-profession:" + path.substring("gameplay/hero_of_the_village/".length(), path.length() - "_gift".length()) + "'");
        }
        writer.decreaseIndentation();
    }

    private static void writeLootTableHeader(String path, IndentedFileWriter writer) throws IOException {
        writer.write("type: LOOT_TABLE");
    }

    private static void writeTableContents(String path, IndentedFileWriter writer, JsonObject json) throws IOException {
        JsonElement poolsElement = json.get("pools");
        if (poolsElement == null && !path.startsWith("entities/")) {
            writer.write("pools: {}");
            return;
        }

        writer.write("pools:");
        writer.increaseIndentation();

        int poolIndex = 0;
        if (poolsElement != null) {
            JsonArray pools = poolsElement.getAsJsonArray();
            for (; poolIndex < pools.size(); poolIndex++) {
                JsonObject pool = pools.get(poolIndex).getAsJsonObject();
                writer.write(poolIndex + ":");
                writer.increaseIndentation();

                writeItemConditions(path, writer, pool);
                writeNumberProvider("rolls", "rolls", writer, pool, 1.0);
                writeNumberProvider("bonus-rolls", "bonus_rolls", writer, pool, null);

                if (path.contains("entities/sheep/") && poolIndex == 0) {
                    // Manually add the sheared condition since this check is normally handled outside of loot tables
                    writer.write("conditions:");
                    writer.increaseIndentation();
                    writer.write("- '!sheep-sheared'");
                    writer.decreaseIndentation();
                }

                JsonElement entriesElement = pool.get("entries");
                if (entriesElement == null) {
                    writer.write("entries: {}");
                    continue;
                }

                writeEntries(path, "entries", writer, entriesElement.getAsJsonArray());

                writer.decreaseIndentation();
            }
        }

        // Manually add special items since they are handled outside of loot tables
        if (path.equals("entities/wither") || path.equals("entities/armor_stand")) {
            String item = path.equals("entities/wither") ? "nether_star" : "armor_stand";
            writer.write(poolIndex++ + ":");
            writer.increaseIndentation();
            writer.write("conditions: []");
            writer.write("rolls: 1");
            writer.write("bonus-rolls: 0");
            writer.write("entries:");
            writer.increaseIndentation();
            writer.write("0:");
            writer.increaseIndentation();
            writer.write("conditions: []");
            writer.write("items:");
            writer.increaseIndentation();
            writer.write("0:");
            writer.increaseIndentation();
            writer.write("type: item");
            writer.write("item: " + item);
            writer.write("amount: 1");
            writer.decreaseIndentation();
            writer.decreaseIndentation();
            writer.decreaseIndentation();
            writer.decreaseIndentation();
            writer.decreaseIndentation();
        }

        // Entity equipment
        if (path.startsWith("entities/")) {
            writer.write(poolIndex++ + ":");
            writer.increaseIndentation();
            writer.write("conditions: []");
            writer.write("rolls: 1");
            writer.write("bonus-rolls: 0");
            writer.write("entries:");
            writer.increaseIndentation();
            writer.write("0:");
            writer.increaseIndentation();
            writer.write("conditions: []");
            writer.write("items:");
            writer.increaseIndentation();
            writer.write("0:");
            writer.increaseIndentation();
            writer.write("type: entity_equipment");
            writer.decreaseIndentation();
            writer.decreaseIndentation();
            writer.decreaseIndentation();
            writer.decreaseIndentation();
            writer.decreaseIndentation();
        }

        // Charged creeper drops
        if (path.startsWith("entities/")) {
            String entityType = path.substring(path.indexOf("/") + 1);
            String skullItem;
            switch (entityType) {
                case "zombie":
                    skullItem = "zombie_head";
                    break;
                case "creeper":
                    skullItem = "creeper_head";
                    break;
                case "skeleton":
                    skullItem = "skeleton_skull";
                    break;
                case "wither_skeleton":
                    skullItem = "wither_skeleton_skull";
                    break;
                default:
                    skullItem = null;
                    break;
            }

            if (skullItem != null) {
                writer.write(poolIndex + ":");
                writer.increaseIndentation();
                writer.write("conditions: []");
                writer.write("rolls: 1");
                writer.write("bonus-rolls: 0");
                writer.write("entries:");
                writer.increaseIndentation();
                writer.write("0:");
                writer.increaseIndentation();
                writer.write("conditions: []");
                writer.write("items:");
                writer.increaseIndentation();
                writer.write("0:");
                writer.increaseIndentation();
                writer.write("type: item");
                writer.write("item: " + skullItem);
                writer.write("amount: 1");
                writer.decreaseIndentation();
                writer.decreaseIndentation();
                writer.decreaseIndentation();
                writer.decreaseIndentation();
                writer.decreaseIndentation();
            }
        }

        writer.decreaseIndentation();
    }

    private static void writeEntries(String path, String yamlName, IndentedFileWriter writer, JsonArray entries) throws IOException {
        writer.write(yamlName + ":");
        writer.increaseIndentation();

        for (int i = 0; i < entries.size(); i++) {
            JsonObject entry = entries.get(i).getAsJsonObject();
            writer.write(i + ":");
            writer.increaseIndentation();

            writeItemConditions(path, writer, entry);

            if (yamlName.equals("entries"))
                writeNumberProvider("weight", "weight", writer, entry, entries.size() == 1 ? null : 1.0);

            writeNumberProvider("quality", "quality", writer, entry, null);

            String type = entry.get("type").getAsString();
            switch (type) {
                case "minecraft:item":
                    writer.write("items:");
                    writer.increaseIndentation();

                    writer.write("0:");
                    writer.increaseIndentation();

                    writer.write("type: item");
                    writer.write("item: " + entry.get("name").getAsString().substring("minecraft:".length()));

                    writeAmountModifiers(path, writer, entry);
                    writeItemFunctions(path, writer, entry);

                    writer.decreaseIndentation();
                    writer.decreaseIndentation();
                    break;
                case "minecraft:tag":
                    writer.write("items:");
                    writer.increaseIndentation();

                    writer.write("0:");
                    writer.increaseIndentation();

                    writer.write("type: tag");
                    writer.write("tag: " + entry.get("name").getAsString().substring("minecraft:".length()));

                    writeAmountModifiers(path, writer, entry);
                    writeItemFunctions(path, writer, entry);

                    writer.decreaseIndentation();
                    writer.decreaseIndentation();
                    break;
                case "minecraft:loot_table":
                    writer.write("items:");
                    writer.increaseIndentation();

                    writer.write("0:");
                    writer.increaseIndentation();

                    writer.write("type: loot_table");
                    writer.write("value: " + entry.get("name").getAsString().substring("minecraft:".length()));

                    writer.decreaseIndentation();
                    writer.decreaseIndentation();
                    break;
                case "minecraft:alternatives":
                    writer.write("children-strategy: first_passing");
                    JsonArray children = entry.get("children").getAsJsonArray();
                    writeEntries(path, "children", writer, children);
                    break;
                case "minecraft:empty":
                    writer.write("items: {}");
                    break;
                default:
                    RoseLoot.getInstance().getLogger().warning("Unhandled item type: " + type + " | " + path);
                    break;
            }

            writer.decreaseIndentation();
        }

        writer.decreaseIndentation();
    }

    private static void writeNumberProvider(String yamlName, String jsonName, IndentedFileWriter writer, JsonObject json, Double defaultValue) throws IOException {
        writeNumberProvider(yamlName, jsonName, writer, json, defaultValue, 1.0);
    }

    private static void writeNumberProvider(String yamlName, String jsonName, IndentedFileWriter writer, JsonObject json, Double defaultValue, double multiplier) throws IOException {
        JsonElement element = json.get(jsonName);
        if (element == null) {
            if (defaultValue != null) {
                String value = defaultValue.intValue() == defaultValue ? String.valueOf(defaultValue.intValue()) : defaultValue.toString();
                writer.write(yamlName + ": " + value);
            }
            return;
        }

        if (element.isJsonObject()) {
            writer.write(yamlName + ":");
            writer.increaseIndentation();
            JsonObject object = element.getAsJsonObject();
            JsonElement typeElement = object.get("type");
            String type;
            if (typeElement != null) {
                type = typeElement.getAsString();
            } else {
                type = "minecraft:uniform";
            }

            switch (type) {
                case "minecraft:uniform":
                    writeNumberProvider("min", "min", writer, object, 0.0, multiplier);
                    writeNumberProvider("max", "max", writer, object, 0.0, multiplier);
                    break;
                case "minecraft:binomial":
                    writeNumberProvider("n", "n", writer, object, 0.0, multiplier);
                    writeNumberProvider("p", "p", writer, object, 0.0, multiplier);
                    break;
            }
            writer.decreaseIndentation();
        } else if (element.isJsonPrimitive()) {
            int intValue = (int) (element.getAsInt() * multiplier);
            double doubleValue = element.getAsDouble() * multiplier;
            if (intValue == doubleValue) {
                writer.write(yamlName + ": " + intValue);
            } else {
                writer.write(yamlName + ": " + doubleValue);
            }
        }
    }

    private static void writeItemConditions(String path, IndentedFileWriter writer, JsonObject json) throws IOException {
        JsonElement conditionsElement = json.get("conditions");
        if (conditionsElement == null) {
            writer.write("conditions: []");
            return;
        }

        JsonArray conditions = conditionsElement.getAsJsonArray();
        if (conditions.size() == 0) {
            writer.write("conditions: []");
            return;
        }

        List<String> conditionList = new ArrayList<>();
        for (JsonElement conditionElement : conditions) {
            StringBuilder conditionBuilder = new StringBuilder();
            JsonObject condition = conditionElement.getAsJsonObject();
            buildConditionRecursively(path, conditionBuilder, condition);
            if (conditionBuilder.length() > 0) {
                String output = conditionBuilder.toString();
                if (output.startsWith("!") && output.contains(LootConditionManager.OR_PATTERN)) {
                    String parsed = output.substring(1);
                    String[] splitConditions = parsed.split(Pattern.quote(LootConditionManager.OR_PATTERN));
                    for (String value : splitConditions)
                        conditionList.add("!" + value);
                } else {
                    conditionList.add(conditionBuilder.toString());
                }
            }
        }

        if (!conditionList.isEmpty()) {
            writer.write("conditions:");
            writer.increaseIndentation();

            for (String value : conditionList)
                writer.write("- '" + value + "'");

            writer.decreaseIndentation();
        } else {
            writer.write("conditions: []");
        }
    }

    private static void buildConditionRecursively(String path, StringBuilder stringBuilder, JsonObject json) {
        String type = json.get("condition").getAsString();
        switch (type) {
            case "minecraft:inverted":
                stringBuilder.append("!");
                buildConditionRecursively(path, stringBuilder, json.get("term").getAsJsonObject());
                break;
            case "minecraft:alternative":
                JsonArray terms = json.get("terms").getAsJsonArray();
                Iterator<JsonElement> termIterator = terms.iterator();
                while (termIterator.hasNext()) {
                    JsonElement termElement = termIterator.next();
                    JsonObject term = termElement.getAsJsonObject();
                    buildConditionRecursively(path, stringBuilder, term);
                    if (termIterator.hasNext())
                        stringBuilder.append(LootConditionManager.OR_PATTERN);
                }
                break;
            case "minecraft:random_chance":
                stringBuilder.append("chance:").append(LootUtils.getToMaximumDecimals(json.get("chance").getAsDouble() * 100, 3)).append('%');
                break;
            case "minecraft:random_chance_with_looting":
                stringBuilder.append("enchantment-chance:");
                stringBuilder.append(LootUtils.getToMaximumDecimals(json.get("chance").getAsDouble() * 100, 3)).append("%,looting,");
                stringBuilder.append(LootUtils.getToMaximumDecimals(json.get("looting_multiplier").getAsDouble() * 100, 3)).append('%');
                break;
            case "minecraft:table_bonus":
                stringBuilder.append("enchantment-chance-table:");
                stringBuilder.append(json.get("enchantment").getAsString().substring("minecraft:".length())).append(',');
                JsonArray chancesElement = json.get("chances").getAsJsonArray();
                Iterator<JsonElement> chanceElementIterator = chancesElement.iterator();
                while (chanceElementIterator.hasNext()) {
                    double chance = chanceElementIterator.next().getAsDouble() * 100;
                    stringBuilder.append(LootUtils.getToMaximumDecimals(chance, 3)).append('%');
                    if (chanceElementIterator.hasNext())
                        stringBuilder.append(',');
                }
                break;
            case "minecraft:block_state_property":
                JsonObject properties = json.get("properties").getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : properties.entrySet())
                    stringBuilder.append("block-data:").append(entry.getKey()).append("=").append(entry.getValue().getAsString());
                break;
            case "minecraft:killed_by_player":
                stringBuilder.append("killed-by:player");
                break;
            case "minecraft:match_tool":
                JsonObject predicate = json.get("predicate").getAsJsonObject();
                JsonElement itemElement = predicate.get("item");
                JsonElement itemsElement = predicate.get("items");
                JsonElement enchantmentsElement = predicate.get("enchantments");
                JsonElement tagElement = predicate.get("tag");
                if (itemElement != null) {
                    stringBuilder.append("required-tool-type:");
                    stringBuilder.append(itemElement.getAsString().substring("minecraft:".length()));
                } else if (itemsElement != null) {
                    JsonArray items = itemsElement.getAsJsonArray();
                    Iterator<JsonElement> toolsIterator = items.iterator();
                    stringBuilder.append("required-tool-type:");
                    while (toolsIterator.hasNext()) {
                        JsonElement toolElement = toolsIterator.next();
                        stringBuilder.append(toolElement.getAsString().substring("minecraft:".length()));
                        if (toolsIterator.hasNext())
                            stringBuilder.append(',');
                    }
                } else if (enchantmentsElement != null) {
                    JsonArray enchantments = enchantmentsElement.getAsJsonArray();
                    if (enchantments.size() > 0) {
                        JsonObject enchantment = enchantments.get(0).getAsJsonObject();
                        stringBuilder.append("enchantment:");
                        stringBuilder.append(enchantment.get("enchantment").getAsString().substring("minecraft:".length()));
                        JsonElement levels = enchantment.get("levels");
                        if (levels != null)
                            stringBuilder.append(',').append(levels.getAsJsonObject().get("min").getAsInt());
                    }
                } else if (tagElement != null) {
                    String tag = tagElement.getAsString().substring("minecraft:".length());
                    stringBuilder.append("tool-tag:").append(tag);
                } else {
                    RoseLoot.getInstance().getLogger().warning("minecraft:match_tool unhandled value" + " | " + path);
                }
                break;
            case "minecraft:damage_source_properties":
                JsonObject damagePredicate = json.get("predicate").getAsJsonObject();
                List<String> causes = new ArrayList<>();
                Map<String, List<String>> causeMapping = new HashMap<>();
                causeMapping.put("bypasses_armor", Collections.singletonList("suffocation"));
                causeMapping.put("bypasses_invulnerability", Arrays.asList("void", "custom"));
                causeMapping.put("bypasses_magic", Collections.singletonList("starvation"));
                causeMapping.put("is_explosion", Arrays.asList("block_explosion", "entity_explosion"));
                causeMapping.put("is_fire", Arrays.asList("fire", "fire_tick"));
                causeMapping.put("is_magic", Collections.singletonList("magic"));
                causeMapping.put("is_projectile", Collections.singletonList("projectile"));
                causeMapping.put("is_lightning", Collections.singletonList("lightning"));
                for (Map.Entry<String, JsonElement> entry : damagePredicate.entrySet()) {
                    JsonElement element = entry.getValue();
                    if (element.getAsBoolean()) {
                        List<String> mappedValues = causeMapping.get(entry.getKey());
                        if (mappedValues != null)
                            causes.addAll(mappedValues);
                    }
                }
                if (!causes.isEmpty()) {
                    stringBuilder.append("death-cause:");
                    Iterator<String> causeIterator = causes.iterator();
                    while (causeIterator.hasNext()) {
                        String cause = causeIterator.next();
                        stringBuilder.append(cause);
                        if (causeIterator.hasNext())
                            stringBuilder.append(',');
                    }
                }
                break;
            case "minecraft:entity_properties":
                JsonObject propertiesPredicate = json.get("predicate").getAsJsonObject();
                if (propertiesPredicate.has("type")) {
                    String entityType = propertiesPredicate.get("type").getAsString().replace("minecraft:", "");
                    entityType = fixEntityNames(entityType);

                    String entityTarget = json.get("entity").getAsString();
                    if (entityTarget.equals("killer")) {
                        stringBuilder.append("killed-by:").append(entityType);
                    } else if (entityTarget.equals("this")) {
                        stringBuilder.append("entity-type:").append(entityType);
                    }
                } else if (propertiesPredicate.has("fishing_hook")) {
                    JsonObject fishingHookObject = propertiesPredicate.get("fishing_hook").getAsJsonObject();
                    if (fishingHookObject.get("in_open_water").getAsBoolean()) {
                        stringBuilder.append("open-water");
                    } else {
                        stringBuilder.append("!open-water");
                    }
                }
                break;
            case "minecraft:location_check":
                JsonObject locationPredicate = json.get("predicate").getAsJsonObject();
                if (locationPredicate.has("biome")) {
                    stringBuilder.append("biome:").append(locationPredicate.get("biome").getAsString().substring("minecraft:".length()));
                } else if (locationPredicate.has("block")) {
                    JsonObject blockObject = locationPredicate.get("block").getAsJsonObject();
                    if (blockObject.has("blocks")) {
                        JsonArray blocksArray = blockObject.get("blocks").getAsJsonArray();
                        if (blocksArray.size() > 0) {
                            int offsetY = 0;
                            if (json.has("offsetY"))
                                offsetY = json.get("offsetY").getAsInt();

                            if (offsetY == 1) {
                                stringBuilder.append("above-block-type:");
                            } else if (offsetY == -1) {
                                stringBuilder.append("below-block-type:");
                            } else {
                                stringBuilder.append("block-type:");
                            }

                            Iterator<JsonElement> blocksIterator = blocksArray.iterator();
                            while (blocksIterator.hasNext()) {
                                stringBuilder.append(blocksIterator.next().getAsString().substring("minecraft:".length()));
                                if (blocksIterator.hasNext())
                                    stringBuilder.append(',');
                            }
                        }
                    }
                }
                break;
            case "minecraft:survives_explosion":
                // Ignored, still handled by vanilla logic
                break;
            default:
                RoseLoot.getInstance().getLogger().warning("Unhandled condition type: " + type + " | " + path);
                break;
        }
    }

    private static void writeAmountModifiers(String path, IndentedFileWriter writer, JsonObject json) throws IOException {
        JsonElement functionsElement = json.get("functions");
        if (functionsElement == null)
            return;

        List<AmountModifierData> amountModifiers = new ArrayList<>();

        JsonArray functions = functionsElement.getAsJsonArray();
        for (JsonElement functionElement : functions) {
            JsonObject function = functionElement.getAsJsonObject();
            String type = function.get("function").getAsString();
            if (!type.equals("minecraft:set_count"))
                continue;

            JsonElement conditionsElement = function.get("conditions");
            if (conditionsElement == null)
                continue;

            List<String> conditionList = new ArrayList<>();
            for (JsonElement conditionElement : conditionsElement.getAsJsonArray()) {
                StringBuilder conditionBuilder = new StringBuilder();
                JsonObject condition = conditionElement.getAsJsonObject();
                buildConditionRecursively(path, conditionBuilder, condition);
                if (conditionBuilder.length() > 0) {
                    String output = conditionBuilder.toString();
                    if (output.startsWith("!") && output.contains(LootConditionManager.OR_PATTERN)) {
                        String parsed = output.substring(1);
                        String[] splitConditions = parsed.split(Pattern.quote(LootConditionManager.OR_PATTERN));
                        for (String value : splitConditions)
                            conditionList.add("!" + value);
                    } else {
                        conditionList.add(conditionBuilder.toString());
                    }
                }
            }

            boolean add = function.has("add") && function.get("add").getAsBoolean();
            amountModifiers.add(new AmountModifierData(conditionList, function, add));
        }

        if (!amountModifiers.isEmpty()) {
            writer.write("amount-modifiers:");
            writer.increaseIndentation();
            int i = 0;
            for (AmountModifierData amountModifier : amountModifiers) {
                writer.write(i++ + ":");
                writer.increaseIndentation();
                amountModifier.write(writer);
                writer.decreaseIndentation();
            }
            writer.decreaseIndentation();
        }
    }

    private static void writeItemFunctions(String path, IndentedFileWriter writer, JsonObject json) throws IOException {
        JsonElement functionsElement = json.get("functions");
        if (functionsElement == null)
            return;

        JsonArray functions = functionsElement.getAsJsonArray();
        for (JsonElement functionElement : functions) {
            JsonObject function = functionElement.getAsJsonObject();
            String name = json.get("name").getAsString();
            String type = function.get("function").getAsString();
            switch (type) {
                case "minecraft:set_count":
                    if (function.has("conditions")) // set_count Conditions are handled in a different method
                        break;

                    if (path.contains("blocks/glow_lichen")) {
                        writer.write("amount: 0");
                        break;
                    }

                    writeNumberProvider("amount", "count", writer, function, 1.0);
                    break;
                case "minecraft:limit_count":
                    JsonObject limitObject = function.get("limit").getAsJsonObject();
                    if (limitObject.has("max"))
                        writer.write("max-amount: " + limitObject.get("max").getAsNumber().intValue());
                    break;
                case "minecraft:set_damage":
                    JsonObject damageObject = function.get("damage").getAsJsonObject();
                    double min = damageObject.get("min").getAsDouble() * 100;
                    double max = damageObject.get("max").getAsDouble() * 100;
                    writer.write("durability:");
                    writer.increaseIndentation();
                    writer.write("min: " + min + "%");
                    writer.write("max: " + max + "%");
                    writer.decreaseIndentation();
                    break;
                case "minecraft:set_contents":
                    if (name.contains("shulker")) {
                        writer.write("copy-block-state: true");
                    } else {
                        RoseLoot.getInstance().getLogger().warning("minecraft:set_contents unhandled: " + path);
                    }
                    break;
                case "minecraft:set_stew_effect":
                    JsonArray effectsArray = function.get("effects").getAsJsonArray();
                    writer.write("pick-random-effect: true");
                    writer.write("custom-effects:");
                    writer.increaseIndentation();
                    int i = 0;
                    for (JsonElement effectElement : effectsArray) {
                        JsonObject effectObject = effectElement.getAsJsonObject();
                        writer.write(i++ + ":");
                        writer.increaseIndentation();
                        writer.write("effect: " + effectObject.get("type").getAsString().substring("minecraft:".length()));
                        writeNumberProvider("duration", "duration", writer, effectObject, 8.0, 20);
                        writer.decreaseIndentation();
                    }
                    writer.decreaseIndentation();
                    break;
                case "minecraft:exploration_map":
                    byte zoom = function.get("zoom").getAsByte();
                    boolean skipExistingChunks = function.get("skip_existing_chunks").getAsBoolean();
                    MapView.Scale scale;
                    switch (zoom) {
                        case 0:
                            scale = MapView.Scale.CLOSEST;
                            break;
                        case 1:
                            scale = MapView.Scale.CLOSE;
                            break;
                        case 3:
                            scale = MapView.Scale.FAR;
                            break;
                        case 4:
                            scale = MapView.Scale.FARTHEST;
                            break;
                        case 2:
                        default:
                            scale = MapView.Scale.NORMAL;
                            break;
                    }
                    writer.write("destination: mansion");
                    writer.write("scale: " + scale.name().toLowerCase());
                    writer.write("search-radius: 50");
                    writer.write("skip-existing-chunks: " + skipExistingChunks);
                    break;
                case "minecraft:set_potion":
                    String potionType = function.get("id").getAsString().substring("minecraft:".length());
                    writer.write("potion-type: " + potionType);
                    break;
                case "minecraft:set_nbt":
                    if (name.contains("potion") || name.contains("tipped_arrow")) {
                        String potionTypeNbt = function.get("tag").getAsString();
                        potionTypeNbt = potionTypeNbt.substring(potionTypeNbt.lastIndexOf(":") + 1, potionTypeNbt.lastIndexOf("\""));
                        writer.write("potion-type: " + potionTypeNbt);
                    } else {
                        RoseLoot.getInstance().getLogger().warning("minecraft:set_nbt unhandled: " + path);
                    }
                    break;
                case "minecraft:copy_nbt":
                    if (name.contains("player_head") || name.contains("bee") || name.contains("banner")) {
                        writer.write("copy-block-state: true");
                    } else if (!name.contains("shulker")) {
                        RoseLoot.getInstance().getLogger().warning("minecraft:copy_nbt unhandled: " + path);
                    }
                    break;
                case "minecraft:copy_state":
                    writer.write("copy-block-data: true");
                    break;
                case "minecraft:copy_name":
                    writer.write("copy-block-name: true");
                    break;
                case "minecraft:enchant_randomly":
                    JsonElement enchantmentsElement = function.get("enchantments");
                    if (enchantmentsElement == null) {
                        writer.write("random-enchantments: []");
                    } else {
                        writer.write("random-enchantments:");
                        writer.increaseIndentation();
                        JsonArray enchantments = enchantmentsElement.getAsJsonArray();
                        for (JsonElement enchantmentElement : enchantments) {
                            String enchantment = enchantmentElement.getAsString().substring("minecraft:".length());
                            writer.write("- " + enchantment);
                        }
                        writer.decreaseIndentation();
                    }
                    break;
                case "minecraft:enchant_with_levels":
                    writer.write("enchant-randomly:");
                    writer.increaseIndentation();
                    writeNumberProvider("level", "levels", writer, function, 30.0);
                    JsonElement treasureElement = function.get("treasure");
                    boolean treasure = treasureElement != null && treasureElement.getAsBoolean();
                    writer.write("treasure: " + treasure);
                    writer.decreaseIndentation();
                    break;
                case "minecraft:looting_enchant":
                    writer.write("enchantment-bonus:");
                    writer.increaseIndentation();
                    writer.write("enchantment: looting");
                    writeNumberProvider("bonus-per-level", "count", writer, function, 0.0);
                    writeNumberProvider("max-bonus-levels", "limit", writer, function, null);
                    writer.decreaseIndentation();
                    break;
                case "minecraft:apply_bonus":
                    writer.write("enchantment-bonus:");
                    writer.increaseIndentation();

                    String formula = function.get("formula").getAsString().substring("minecraft:".length());
                    writer.write("formula: " + formula);
                    writer.write("enchantment: " + function.get("enchantment").getAsString().substring("minecraft:".length()));

                    JsonElement parametersElement = function.get("parameters");
                    if (parametersElement != null) {
                        JsonObject parametersObject = parametersElement.getAsJsonObject();
                        switch (formula) {
                            case "uniform_bonus_count":
                                writeNumberProvider("bonus", "bonusMultiplier", writer, parametersObject, 1.0);
                                break;
                            case "binomial_with_bonus_count":
                                writeNumberProvider("bonus", "extra", writer, parametersObject, 1.0);
                                writeNumberProvider("probability", "probability", writer, parametersObject, 0.5);
                                break;
                            case "ore_drops":
                                break;
                            default:
                                RoseLoot.getInstance().getLogger().warning("minecraft:apply_bonus unhandled formula: " + formula);
                                break;
                        }
                    }

                    writer.decreaseIndentation();
                    break;
                case "minecraft:furnace_smelt":
                    writer.write("smelt-if-burning: true");
                    break;
                case "minecraft:explosion_decay":
                    // Ignored, still handled by vanilla logic
                    break;
                default:
                    RoseLoot.getInstance().getLogger().warning("Unhandled item function type: " + type + " | " + path);
                    break;
            }
        }
    }

    private static String fixEntityNames(String entityType) {
        // Apply replacements for spigot enums that are wrong
        switch (entityType) {
            case "snow_golem":
                return entityType = "snowman";
            case "mooshroom":
                return entityType = "mushroom_cow";
            default:
                return entityType;
        }
    }

    private static class IndentedFileWriter {

        private final FileWriter fileWriter;
        private int indentation;

        public IndentedFileWriter(FileWriter fileWriter) {
            this.fileWriter = fileWriter;
            this.indentation = 0;
        }

        /**
         * Writes a line with the current indentation, also appends a newline at the end
         *
         * @param line The line to write
         */
        public void write(String line) throws IOException {
            this.fileWriter.write(new String(new char[this.indentation]).replace('\0', ' ') + line + '\n');
        }

        public void increaseIndentation() {
            this.indentation += 2;
        }

        public void decreaseIndentation() {
            this.indentation = Math.max(0, this.indentation - 2);
        }

    }

    private static class AmountModifierData {

        private final List<String> conditions;
        private final JsonObject parent;
        private final boolean add;

        public AmountModifierData(List<String> conditions, JsonObject parent, boolean add) {
            this.conditions = conditions;
            this.parent = parent;
            this.add = add;
        }

        public void write(IndentedFileWriter writer) throws IOException {
            if (this.conditions.isEmpty()) {
                writer.write("conditions: []");
            } else {
                writer.write("conditions:");
                writer.increaseIndentation();
                for (String condition : this.conditions)
                    writer.write("- '" + condition + "'");
                writer.decreaseIndentation();
            }
            writeNumberProvider("value", "count", writer, this.parent, 1.0);
            writer.write("add: " + this.add);
        }

    }

}
