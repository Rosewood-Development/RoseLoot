package dev.rosewood.roseloot.provider;

import dev.rosewood.rosegarden.utils.HexUtils;
import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

public interface StringProvider {

    /**
     * Gets a string from this provider with placeholders applied
     *
     * @param context The LootContext
     * @return The string
     */
    String get(LootContext context);

    /**
     * Gets a string from this provider with placeholders and colors applied
     *
     * @param context The LootContext
     * @return The string with placeholders and colors applied
     */
    default String getFormatted(LootContext context) {
        return HexUtils.colorify(this.get(context));
    }

    /**
     * Gets a list of strings from this provider with placeholders applied
     *
     * @param context The LootContext
     * @return The list of strings
     */
    List<String> getList(LootContext context);

    /**
     * Gets a list of strings from this provider with placeholders and colors applied
     *
     * @param context The LootContext
     * @return The list of strings with placeholders and colors applied
     */
    default List<String> getListFormatted(LootContext context) {
        return this.getList(context).stream().map(HexUtils::colorify).toList();
    }

    static StringProvider fromString(String string) {
        return new ConstantStringProvider(string);
    }

    static StringProvider fromSection(ConfigurationSection section, String key, String defaultValue) {
        if (section == null || key == null || key.isEmpty()) {
            if (defaultValue == null) {
                return null;
            } else {
                return new ConstantStringProvider(defaultValue);
            }
        }

        if (section.isList(key)) {
            List<String> stringList = section.getStringList(key);
            List<StringProvider> stringProviders = new ArrayList<>(stringList.size());
            for (String string : stringList)
                stringProviders.add(new ConstantStringProvider(string));
            return new ListStringProvider(stringProviders);
        } else {
            String stringValue = section.getString(key);
            if (stringValue != null)
                return new ConstantStringProvider(stringValue);
        }

        if (defaultValue == null) {
            return null;
        } else {
            return new ConstantStringProvider(defaultValue);
        }
    }

    class ConstantStringProvider implements StringProvider {

        private final String value;

        private ConstantStringProvider(String value) {
            this.value = value;
        }

        @Override
        public String get(LootContext context) {
            return context.applyPlaceholders(this.value);
        }

        @Override
        public List<String> getList(LootContext context) {
            return List.of(context.applyPlaceholders(this.value));
        }

    }

    class ListStringProvider implements StringProvider {

        private final List<StringProvider> values;

        private ListStringProvider(List<StringProvider> values) {
            this.values = values;
        }

        @Override
        public String get(LootContext context) {
            if (this.values.isEmpty())
                return "";

            StringProvider value = this.values.get(LootUtils.RANDOM.nextInt(this.values.size()));
            return context.applyPlaceholders(value.get(context));
        }

        @Override
        public List<String> getList(LootContext context) {
            return this.values.stream().map(x -> x.get(context)).map(context::applyPlaceholders).toList();
        }

    }

}
