package cx.rain.mc.inkraft.story.value;

import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;

public final class ArrayStoryValue extends JsonStoryValue<List<IStoryValue<?, ?>>, JsonArray> {
    private final List<IStoryValue<?, ?>> value;

    public ArrayStoryValue(List<IStoryValue<?, ?>> value) {
        var normalized = new ArrayList<IStoryValue<?, ?>>(value.size());
        for (var item : value) {
            normalized.add(normalize(item));
        }
        this.value = List.copyOf(normalized);
    }

    public static ArrayStoryValue empty() {
        return new ArrayStoryValue(List.of());
    }

    public static ArrayStoryValue parse(String source) {
        var element = parseJson(source);
        if (!element.isJsonArray()) {
            throw new IllegalArgumentException("Expected a JSON array.");
        }
        return fromJsonArray(element.getAsJsonArray());
    }

    static ArrayStoryValue fromJsonArray(JsonArray array) {
        var values = new ArrayList<IStoryValue<?, ?>>(array.size());
        for (var element : array) {
            values.add(fromJsonElement(element));
        }
        return new ArrayStoryValue(values);
    }

    public static boolean isValid(String source) {
        try {
            parse(source);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public List<IStoryValue<?, ?>> value() {
        return value;
    }

    @Override
    public List<IStoryValue<?, ?>> getValue() {
        return value;
    }

    @Override
    protected JsonArray toJson() {
        var array = new JsonArray(value.size());
        for (var item : value) {
            array.add(toJsonElement(item));
        }
        return array;
    }

    public int size() {
        return value.size();
    }

    public IStoryValue<?, ?> get(int index) {
        requireExistingIndex(index);
        return value.get(index);
    }

    public ArrayStoryValue set(int index, IStoryValue<?, ?> item) {
        requireExistingIndex(index);
        var values = new ArrayList<>(value);
        values.set(index, item);
        return new ArrayStoryValue(values);
    }

    public ArrayStoryValue add(IStoryValue<?, ?> item) {
        var values = new ArrayList<>(value);
        values.add(item);
        return new ArrayStoryValue(values);
    }

    public ArrayStoryValue remove(int index) {
        requireExistingIndex(index);
        var values = new ArrayList<>(value);
        values.remove(index);
        return new ArrayStoryValue(values);
    }

    public boolean contains(IStoryValue<?, ?> item) {
        return value.contains(normalize(item));
    }

    private void requireExistingIndex(int index) {
        if (index < 0 || index >= value.size()) {
            throw new IllegalArgumentException("Array index out of bounds: " + index);
        }
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ArrayStoryValue that && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "ArrayStoryValue[value=" + value + ']';
    }
}
