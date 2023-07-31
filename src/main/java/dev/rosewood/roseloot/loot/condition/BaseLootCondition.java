package dev.rosewood.roseloot.loot.condition;

import dev.rosewood.roseloot.RoseLoot;

public abstract class BaseLootCondition implements LootCondition {

    private String tag;
    private boolean printedDeprecationWarning;

    /**
     * @param tag The tag, including both prefix and values
     * @param parse Whether or not the tag should be parsed
     * @throws IllegalArgumentException if the tag is empty, null, or malformed
     */
    protected BaseLootCondition(String tag, boolean parse) throws IllegalArgumentException {
        this.init(tag, parse);
    }

    /**
     * @param tag The tag, including both prefix and values
     */
    public BaseLootCondition(String tag) {
        this(tag, true);
    }

    protected void init(String tag, boolean parse) throws IllegalArgumentException {
        if (tag == null || tag.trim().isEmpty())
            throw new IllegalArgumentException("Empty or null tag");

        if (!parse) {
            this.tag = tag;
            return;
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

        String replacement = this.getDeprecationReplacement();
        if (replacement != null && !this.printedDeprecationWarning) {
            this.printedDeprecationWarning = true;
            RoseLoot.getInstance().getLogger().warning(String.format("Loot condition deprecation warning: [%s] will be removed in the future, use [%s] instead", prefix, replacement));
        }
    }

    /**
     * Parses the value portion of the tag
     *
     * @param values The values portion of the tag to parse
     * @return true if the tag is valid, otherwise false
     */
    protected abstract boolean parseValues(String[] values);

    @Override
    public String toString() {
        return this.tag;
    }

    /**
     * @return The deprecation replacement condition, or null if there is no replacement
     */
    protected String getDeprecationReplacement() {
        return null;
    }

}
