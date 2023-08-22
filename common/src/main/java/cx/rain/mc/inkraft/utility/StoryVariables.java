package cx.rain.mc.inkraft.utility;

public class StoryVariables {
    public interface IStoryVariable {
        String asString();
        
        static IStoryVariable fromString(String str) {
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

    public record StrVar(String stringResult) implements IStoryVariable {
        @Override
        public String asString() {
            return stringResult;
        }
    }

    public record IntVar(int intResult) implements IStoryVariable {
        @Override
        public String asString() {
            return Integer.toString(intResult);
        }
    }

    public record DoubleVar(double doubleResult) implements IStoryVariable {
        @Override
        public String asString() {
            return Double.toString(doubleResult);
        }
    }

    public record BoolVar(boolean boolResult) implements IStoryVariable {
        public static final BoolVar TRUE = new BoolVar(true);
        public static final BoolVar FALSE = new BoolVar(false);

        @Override
        public String asString() {
            return Boolean.toString(boolResult);
        }
    }
}
