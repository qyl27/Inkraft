package cx.rain.mc.inkraft.utility;

public class StoryVariables {
    // Todo: qyl27: variable name, display name, etc.

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
