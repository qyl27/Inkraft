package cx.rain.mc.inkraft.story.value;

import com.mojang.serialization.Codec;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public final class StringStoryValue extends StringifyStoryValue<String> {
    public static final Codec<? extends StringifyStoryValue<?>> CODEC = Codec.STRING.xmap(
        s -> (StringifyStoryValue<?>) new StringStoryValue(s),
        StringifyStoryValue::getString);

    private final String value;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof StringStoryValue that && Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "StringStoryValue[value=" + value + ']';
    }
}
