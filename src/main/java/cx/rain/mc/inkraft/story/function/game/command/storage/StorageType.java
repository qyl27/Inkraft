package cx.rain.mc.inkraft.story.function.game.command.storage;

import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

import java.util.function.Function;

public enum StorageType {
    BYTE(StorageType::asInt),
    SHORT(StorageType::asInt),
    INT(StorageType::asInt),
    LONG(StorageType::asInt),
    FLOAT(StorageType::asFloat),
    DOUBLE(StorageType::asFloat);

    private final Function<Tag, IStoryVariable<?>> func;

    StorageType(Function<Tag, IStoryVariable<?>> asValue) {
        this.func = asValue;
    }

    public IStoryVariable<?> asValue(Tag tag) {
        return func.apply(tag);
    }

    public static IStoryVariable.Int asInt(Tag tag) {
        if (tag instanceof NumericTag n) {
            return new IStoryVariable.Int(n.getAsInt());
        }
        return IStoryVariable.Int.ZERO;
    }

    public static IStoryVariable.Float asFloat(Tag tag) {
        if (tag instanceof NumericTag n) {
            return new IStoryVariable.Float(n.getAsFloat());
        }
        return IStoryVariable.Float.ZERO;
    }

    public static StorageType from(String str) {
        return switch (str) {
            case "int" -> INT;
            case "float" -> FLOAT;
            case "short" -> SHORT;
            case "long" -> LONG;
            case "double" -> DOUBLE;
            case "byte" -> BYTE;
            default -> throw new IllegalStateException("Unexpected value: " + str);
        };
    }
}
