package cx.rain.mc.inkraft.story.value;

import com.bladecoder.ink.runtime.InkList;

public interface IStoryValue<Typed, Primitive> {
    SerializeValueType getSerializedType();

    Class<?> getValueType();

    Typed getValue();

    default Object asObject() {
        return toPrimitive();
    }

    Primitive toPrimitive();

    default String getString() {
        return toPrimitive().toString();
    }

    static IStoryValue<?, ?> fromObject(Object value) {
        return switch (value) {
            case IStoryValue<?, ?> storyValue -> storyValue;
            case Boolean bool -> BoolStoryValue.from(bool);
            case Integer integer -> new IntStoryValue(integer);
            case java.lang.Float floating -> new FloatStoryValue(floating);
            case String string -> new StringStoryValue(string);
            case InkList list -> new InkListStoryValue(list);
            case null -> throw new IllegalArgumentException("Ink value must not be null.");
            default -> throw new IllegalArgumentException(
                    "Unsupported Ink value type: " + value.getClass().getCanonicalName());
        };
    }

    static IStoryValue<?, ?> fromString(String value) {
        if (value.equalsIgnoreCase("false")) {
            return BoolStoryValue.FALSE;
        } else if (value.equalsIgnoreCase("true")) {
            return BoolStoryValue.TRUE;
        }

        try {
            return new IntStoryValue(Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
            try {
                return new FloatStoryValue(java.lang.Float.parseFloat(value));
            } catch (NumberFormatException ignored2) {
                return new StringStoryValue(value);
            }
        }
    }
}
