package cx.rain.mc.inkraft.story.value;

import com.bladecoder.ink.runtime.InkList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.Strictness;

import java.util.Objects;

public abstract class JsonStoryValue<Typed, Json extends JsonElement> extends StringifyStoryValue<Typed> {
    private static final Gson GSON = new GsonBuilder()
            .setStrictness(Strictness.STRICT)
            .create();
    private static final float INK_FLOAT_MAX = 3.4E38F;

    protected abstract Json toJson();

    @Override
    public final String getString() {
        return toJson().toString();
    }

    protected static IStoryValue<?, ?> normalize(IStoryValue<?, ?> value) {
        Objects.requireNonNull(value, "Container value must not be null.");
        return switch (value.toPrimitive()) {
            case Boolean bool -> BoolStoryValue.from(bool);
            case Integer integer -> new IntStoryValue(integer);
            case Float floating -> normalizeFloat(floating);
            case String string -> new StringStoryValue(string);
            case InkList ignored -> throw new IllegalArgumentException(
                    "Ink lists are not supported as JSON container values.");
            case null -> throw new IllegalArgumentException("Null is not a supported container value.");
            default -> throw new IllegalArgumentException(
                    "Unsupported container value type: " + value.getClass().getCanonicalName());
        };
    }

    protected static JsonElement parseJson(String source) {
        if (source == null) {
            throw new IllegalArgumentException("JSON container must not be null.");
        }

        try {
            var element = GSON.fromJson(source, JsonElement.class);
            if (element == null) {
                throw new IllegalArgumentException("JSON container must not be empty.");
            }
            return element;
        } catch (JsonParseException ex) {
            throw new IllegalArgumentException("Invalid JSON container.", ex);
        }
    }

    protected static IStoryValue<?, ?> fromJsonElement(JsonElement element) {
        if (element.isJsonNull()) {
            return BoolStoryValue.FALSE;
        }
        if (element.isJsonArray()) {
            return new StringStoryValue(ArrayStoryValue.fromJsonArray(element.getAsJsonArray()).getString());
        }
        if (element.isJsonObject()) {
            return new StringStoryValue(MapStoryValue.fromJsonObject(element.getAsJsonObject()).getString());
        }

        var primitive = element.getAsJsonPrimitive();
        if (primitive.isBoolean()) {
            return BoolStoryValue.from(primitive.getAsBoolean());
        }
        if (primitive.isString()) {
            return new StringStoryValue(primitive.getAsString());
        }
        if (primitive.isNumber()) {
            return parseNumber(primitive);
        }
        throw new IllegalArgumentException("Unsupported JSON primitive.");
    }

    protected static JsonElement toJsonElement(IStoryValue<?, ?> value) {
        return switch (normalize(value).toPrimitive()) {
            case Boolean bool -> new JsonPrimitive(bool);
            case Integer integer -> new JsonPrimitive(integer);
            case java.lang.Float floating -> new JsonPrimitive(floating);
            case String string -> new JsonPrimitive(string);
            default -> throw new IllegalStateException("Unsupported normalized JSON scalar.");
        };
    }

    private static IStoryValue<?, ?> parseNumber(JsonPrimitive primitive) {
        var source = primitive.getAsString();
        if (source.indexOf('.') < 0 && source.indexOf('e') < 0 && source.indexOf('E') < 0) {
            return new IntStoryValue(primitive.getAsInt());
        }
        return normalizeFloat(primitive.getAsFloat());
    }

    private static FloatStoryValue normalizeFloat(float value) {
        if (Float.isNaN(value) || value == 0.0F) {
            return FloatStoryValue.ZERO;
        }
        if (value == Float.POSITIVE_INFINITY) {
            return new FloatStoryValue(INK_FLOAT_MAX);
        }
        if (value == Float.NEGATIVE_INFINITY) {
            return new FloatStoryValue(-INK_FLOAT_MAX);
        }
        return new FloatStoryValue(value);
    }
}
