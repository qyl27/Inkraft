package cx.rain.mc.inkraft.story.value;

import com.mojang.serialization.Codec;

import java.util.Objects;

public final class BoolStoryValue implements IStoryValue<Boolean, Boolean> {
    public static final BoolStoryValue TRUE = new BoolStoryValue(true);
    public static final BoolStoryValue FALSE = new BoolStoryValue(false);

    public static final Codec<BoolStoryValue> CODEC = Codec.BOOL.xmap(BoolStoryValue::from, BoolStoryValue::getValue);

    private final boolean value;

    private BoolStoryValue(boolean value) {
        this.value = value;
    }

    public static BoolStoryValue from(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public SerializeValueType getSerializedType() {
        return SerializeValueType.BOOL;
    }

    @Override
    public Class<?> getValueType() {
        return Boolean.class;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public Boolean toPrimitive() {
        return value;
    }

    public boolean value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BoolStoryValue that) {
            return this.value == that.value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BoolStoryValue[" +
            "value=" + value + ']';
    }
}
