package cx.rain.mc.inkraft.story.value;

import com.mojang.serialization.Codec;

public record IntStoryValue(int value) implements IStoryValue<Integer, Integer> {
    public static final IntStoryValue ZERO = new IntStoryValue(0);

    public static final Codec<IntStoryValue> CODEC = Codec.INT.xmap(IntStoryValue::new, IntStoryValue::getValue);

    @Override
    public SerializeValueType getSerializedType() {
        return SerializeValueType.INT;
    }

    @Override
    public Class<?> getValueType() {
        return Integer.class;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public Integer toPrimitive() {
        return value;
    }
}
