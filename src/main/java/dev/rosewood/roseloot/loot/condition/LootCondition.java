package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.loot.context.LootContext;

public abstract class LootCondition {

    private final String tag;
    private final boolean inverted;

    /**
     * @param tag The tag, including both prefix and values
     * @param parse Whether or not the tag should be parsed
     */
    protected LootCondition(String tag, boolean parse) {
        if (tag == null || tag.trim().isEmpty())
            throw new IllegalArgumentException("Empty or null tag");

        if (!parse) {
            this.tag = tag;
            this.inverted = false;
            return;
        }

        if (tag.startsWith("!")) {
            tag = tag.substring(1);
            this.inverted = true;
        } else {
            this.inverted = false;
        }

        this.tag = tag;

        String prefix;
        String[] values;
        int index = tag.indexOf(':');
        if (index == -1) {
            prefix = tag;
            values = new String[0];
        } else {
            String[] pieces = tag.split(":", 2);
            prefix = pieces[0];
            values = pieces[1].split(",");
            for (int i = 0; i < values.length; i++)
                values[i] = values[i].trim();
        }

        if (!this.parseValues(values))
            throw new IllegalArgumentException(String.format("Invalid tag arguments for %s", prefix));
    }

    /**
     * @param tag The tag, including both prefix and values
     */
    public LootCondition(String tag) {
        this(tag, true);
    }

    /**
     * Checks if the LootContext meets this tag's condition
     *
     * @param context The LootContext
     * @return true if the condition is met, otherwise false
     */
    public final boolean check(LootContext context) {
        return this.checkInternal(context) ^ this.inverted;
    }

    /**
     * Checks if the LootContext meets this tag's condition
     *
     * @param context The LootContext
     * @return true if the condition is met, otherwise false
     */
    protected abstract boolean checkInternal(LootContext context);

    /**
     * Parses the value portion of the tag
     *
     * @param values The values portion of the tag to parse
     * @return true if the tag is valid, otherwise false
     */
    public abstract boolean parseValues(String[] values);

    @Override
    public String toString() {
        return this.tag;
    }

}
