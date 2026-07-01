package cx.rain.mc.inkraft.story.value;

import com.bladecoder.ink.runtime.InkList;

import java.util.Objects;

public final class InkListStoryValue extends StringifyStoryValue<InkList> {
    private final InkList value;

    public InkListStoryValue(InkList value) {
        this.value = value;
    }

    @Override
    public InkList getValue() {
        return value;
    }

    public InkList value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (InkListStoryValue) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "InkListStoryValue[" +
            "value=" + value + ']';
    }

}
