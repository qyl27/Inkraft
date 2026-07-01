package cx.rain.mc.inkraft.story.value;

import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class MapStoryValue extends JsonStoryValue<Map<String, IStoryValue<?, ?>>, JsonObject> {
    private final Map<String, IStoryValue<?, ?>> value;

    public MapStoryValue(Map<String, IStoryValue<?, ?>> value) {
        var normalized = new TreeMap<String, IStoryValue<?, ?>>();
        for (var entry : value.entrySet()) {
            normalized.put(entry.getKey(), normalize(entry.getValue()));
        }
        this.value = Collections.unmodifiableMap(normalized);
    }

    public static MapStoryValue empty() {
        return new MapStoryValue(Map.of());
    }

    public static MapStoryValue parse(String source) {
        var element = parseJson(source);
        if (!element.isJsonObject()) {
            throw new IllegalArgumentException("Expected a JSON object.");
        }
        return fromJsonObject(element.getAsJsonObject());
    }

    static MapStoryValue fromJsonObject(JsonObject object) {
        var values = new TreeMap<String, IStoryValue<?, ?>>();
        for (var entry : object.entrySet()) {
            values.put(entry.getKey(), fromJsonElement(entry.getValue()));
        }
        return new MapStoryValue(values);
    }

    public static boolean isValid(String source) {
        try {
            parse(source);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public Map<String, IStoryValue<?, ?>> value() {
        return value;
    }

    @Override
    public Map<String, IStoryValue<?, ?>> getValue() {
        return value;
    }

    @Override
    protected JsonObject toJson() {
        var object = new JsonObject();
        for (var entry : value.entrySet()) {
            object.add(entry.getKey(), toJsonElement(entry.getValue()));
        }
        return object;
    }

    public int size() {
        return value.size();
    }

    public IStoryValue<?, ?> get(String key) {
        var item = value.get(key);
        if (item == null) {
            throw new IllegalArgumentException("Map key does not exist: " + key);
        }
        return item;
    }

    public MapStoryValue set(String key, IStoryValue<?, ?> item) {
        var values = new TreeMap<>(value);
        values.put(key, item);
        return new MapStoryValue(values);
    }

    public MapStoryValue remove(String key) {
        var values = new TreeMap<>(value);
        if (values.remove(key) == null) {
            throw new IllegalArgumentException("Map key does not exist: " + key);
        }
        return new MapStoryValue(values);
    }

    public boolean contains(String key) {
        return value.containsKey(key);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof MapStoryValue that && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "MapStoryValue[value=" + value + ']';
    }
}
