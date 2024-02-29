package cx.rain.mc.inkraft.utility;

import net.minecraft.network.chat.Component;

import java.util.Objects;

public final class StoryVariable {
    private String name;
    private Component display;
    private IValue value;

    public StoryVariable(String name, Component display, IValue value) {
        this.name = name;
        this.display = display;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Component getDisplay() {
        return display;
    }

    public void setDisplay(Component display) {
        this.display = display;
    }

    public IValue getValue() {
        return value;
    }

    public void setValue(IValue value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StoryVariable) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.display, that.display) &&
                Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, display, value);
    }

    @Override
    public String toString() {
        return "StoryVariable[" +
                "name=" + name + ", " +
                "display=" + display + ", " +
                "value=" + value + ']';
    }

    public interface IValue {
        String asStringValue();

        static IValue fromString(String str) {
            if (str.equalsIgnoreCase("false")) {
                return BoolVar.FALSE;
            } else if (str.equalsIgnoreCase("true")) {
                return BoolVar.TRUE;
            }

            try {
                return new IntVar(Integer.parseInt(str));
            } catch (NumberFormatException ignored) {
                try {
                    return new DoubleVar(Double.parseDouble(str));
                } catch (NumberFormatException ignored2) {
                    return new StrVar(str);
                }
            }
        }
    }

    public record StrVar(String stringValue) implements IValue {
        @Override
        public String asStringValue() {
            return stringValue;
        }
    }

    public record IntVar(int intValue) implements IValue {
        @Override
        public String asStringValue() {
            return Integer.toString(intValue);
        }
    }

    public record DoubleVar(double doubleValue) implements IValue {
        @Override
        public String asStringValue() {
            return Double.toString(doubleValue);
        }
    }

    public record BoolVar(boolean boolValue) implements IValue {
        public static final BoolVar TRUE = new BoolVar(true);
        public static final BoolVar FALSE = new BoolVar(false);

        @Override
        public String asStringValue() {
            return Boolean.toString(boolValue);
        }
    }
}
