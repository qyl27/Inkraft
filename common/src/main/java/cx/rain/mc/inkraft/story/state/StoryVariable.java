package cx.rain.mc.inkraft.story.state;

import java.util.Objects;

public final class StoryVariable<T> {
    private String name;
    private T value;

    public StoryVariable(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
