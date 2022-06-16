package dev.rosewood.roseloot.hook.conditions.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTType;
import dev.rosewood.roseloot.loot.condition.LootCondition;
import dev.rosewood.roseloot.loot.condition.Operator;
import dev.rosewood.roseloot.loot.context.LootContext;
import java.util.Arrays;

public abstract class NBTCondition extends LootCondition {

    private String path, right;
    private Operator operator;

    public NBTCondition(String tag) {
        super(tag);
    }

    @Override
    protected boolean checkInternal(LootContext context) {
        String left = this.getNBTValue(context, this.path);
        if (left == null)
            return false;
        return this.operator.evaluate(left, context.applyPlaceholders(this.right));
    }

    @Override
    public boolean parseValues(String[] values) {
        // Piece the expression back together in case it got split up
        String expression = String.join(",", values);
        if (expression.trim().isEmpty())
            return false;

        for (Operator operator : Operator.values()) {
            String symbol = operator.getSymbol();
            // Look for symbol in expression
            int operatorIndex = expression.indexOf(symbol);
            if (operatorIndex != -1) {
                this.path = expression.substring(0, operatorIndex).trim();
                this.operator = operator;
                this.right = expression.substring(operatorIndex + symbol.length()).trim();

                if (this.path.isEmpty() || this.right.isEmpty())
                    continue;

                return true;
            }
        }

        return false;
    }

    protected abstract NBTCompound getNBTCompound(LootContext context);

    private String getNBTValue(LootContext context, String path) {
        try {
            NBTCompound compound = this.getNBTCompound(context);
            if (compound == null)
                return null;

            String[] pathPieces = path.split("\\.");
            for (String pathPiece : pathPieces) {
                if (pathPiece.endsWith("]")) {
                    int index = Integer.parseInt(pathPiece.substring(pathPiece.indexOf("[") + 1, pathPiece.indexOf("]")));
                    pathPiece = pathPiece.substring(0, pathPiece.indexOf("["));
                    NBTType listType = compound.getListType(pathPiece);
                    if (listType == null)
                        return null;

                    if (listType == NBTType.NBTTagCompound) {
                        compound = compound.getCompoundList(pathPiece).get(index);
                    } else {
                        return this.getListValue(compound, listType, pathPiece, index);
                    }
                } else {
                    NBTType type = compound.getType(pathPiece);
                    if (type == null)
                        return null;

                    if (type == NBTType.NBTTagCompound) {
                        compound = compound.getCompound(pathPiece);
                    } else {
                        return this.getValue(compound, type, pathPiece);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getListValue(NBTCompound compound, NBTType type, String key, int index) {
        return switch (type) {
            case NBTTagInt -> compound.getIntegerList(key).get(index).toString();
            case NBTTagLong -> compound.getLongList(key).get(index).toString();
            case NBTTagFloat -> compound.getFloatList(key).get(index).toString();
            case NBTTagDouble -> compound.getDoubleList(key).get(index).toString();
            case NBTTagIntArray -> Arrays.toString(compound.getIntArrayList(key).get(index));
            case NBTTagString -> compound.getStringList(key).get(index);
            default -> null;
        };
    }

    private String getValue(NBTCompound compound, NBTType type, String key) {
        return switch (type) {
            case NBTTagByte -> compound.getByte(key).toString();
            case NBTTagShort -> compound.getShort(key).toString();
            case NBTTagInt -> compound.getInteger(key).toString();
            case NBTTagLong -> compound.getLong(key).toString();
            case NBTTagFloat -> compound.getFloat(key).toString();
            case NBTTagDouble -> compound.getDouble(key).toString();
            case NBTTagByteArray -> Arrays.toString(compound.getByteArray(key));
            case NBTTagIntArray -> Arrays.toString(compound.getIntArray(key));
            case NBTTagString -> compound.getString(key);
            default -> null;
        };
    }

}
