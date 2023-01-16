package dev.rosewood.roseloot.provider;

import dev.rosewood.roseloot.loot.context.LootContext;
import dev.rosewood.roseloot.util.LootUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;

public interface StringProvider {

    String get(LootContext context);

    default List<String> getList(LootContext context) {
        return new ArrayList<>(List.of(this.get(context)));
    }

    static StringProvider fromString(String string) {
        if (string.startsWith("%") && string.endsWith("%")) {
            return new PlaceholderStringProvider(string);
        } else {
            return new ConstantStringProvider(string);
        }
    }

    static StringProvider fromSection(ConfigurationSection section, String key, String defaultValue) {
        if (section == null || key == null || key.isEmpty()) {
            if (defaultValue == null) {
                return null;
            } else {
                return new ConstantStringProvider(defaultValue);
            }
        }

        if (section.isString(key)) {
            String stringValue = section.getString(key, "");
            if (stringValue.startsWith("%") && stringValue.endsWith("%")) {
                // Placeholder!
                return new PlaceholderStringProvider(stringValue);
            } else {
                return new ConstantStringProvider(stringValue);
            }
        } else if (section.isList(key)) {
            List<String> stringList = section.getStringList(key);
            List<StringProvider> stringProviders = new ArrayList<>(stringList.size());
            for (String string : stringList) {
                if (string.startsWith("%") && string.endsWith("%")) {
                    stringProviders.add(new PlaceholderStringProvider(string));
                } else {
                    stringProviders.add(new ConstantStringProvider(string));
                }
            }
            return new ListStringProvider(stringProviders);
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
            return this.value;
        }

    }

    class ListStringProvider implements StringProvider {

        private final List<StringProvider> values;

        private ListStringProvider(List<StringProvider> values) {
            this.values = values;
        }

        @Override
        public String get(LootContext context) {
            return this.values.get(LootUtils.RANDOM.nextInt(this.values.size())).get(context);
        }

        @Override
        public List<String> getList(LootContext context) {
            return this.values.stream().map(x -> x.get(context)).toList();
        }

    }

    class PlaceholderStringProvider implements StringProvider {

        private final String placeholder;

        private PlaceholderStringProvider(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public String get(LootContext context) {
            return context.applyPlaceholders(this.placeholder);
        }

    }

}
