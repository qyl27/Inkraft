package cx.rain.mc.inkraft.story.value;

public abstract class StringifyStoryValue<Typed> implements IStoryValue<Typed, String> {
    @Override
    public SerializeValueType getSerializedType() {
        return SerializeValueType.STRING;
    }

    @Override
    public Class<?> getValueType() {
        return String.class;
    }

    @Override
    public String toPrimitive() {
        return getString();
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
