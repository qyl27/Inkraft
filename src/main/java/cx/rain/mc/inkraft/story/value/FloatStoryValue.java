package cx.rain.mc.inkraft.story.value;

import com.mojang.serialization.Codec;

public record FloatStoryValue(float value) implements IStoryValue<Float, Float> {
    public static final FloatStoryValue ZERO = new FloatStoryValue(0);

    public static final Codec<FloatStoryValue> CODEC = Codec.FLOAT.xmap(FloatStoryValue::new, FloatStoryValue::getValue);

    @Override
    public SerializeValueType getSerializedType() {
        return SerializeValueType.FLOAT;
    }

    @Override
    public Class<?> getValueType() {
        return Float.class;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public Float toPrimitive() {
        return value;
    }
}
