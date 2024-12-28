package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.InkList;

public interface IStoryVariable<T> {
    T getValue();

    Object asObject();

    static IStoryVariable<?> fromString(String str) {
        if (str.equalsIgnoreCase("false")) {
            return Bool.FALSE;
        } else if (str.equalsIgnoreCase("true")) {
            return Bool.TRUE;
        }

        try {
            return new Int(Integer.parseInt(str));
        } catch (NumberFormatException ignored) {
            try {
                return new Float(java.lang.Float.parseFloat(str));
            } catch (NumberFormatException ignored2) {
                return new Str(str);
            }
        }
    }

    record Bool(boolean value) implements IStoryVariable<Boolean> {
        public static final Bool TRUE = new Bool(true);
        public static final Bool FALSE = new Bool(false);

        public static Bool from(boolean value) {
            if (value) {
                return TRUE;
            } else {
                return FALSE;
            }
        }

        @Override
        public Boolean getValue() {
            return value;
        }

        @Override
        public Object asObject() {
            return value;
        }
    }

    record Int(int value) implements IStoryVariable<Integer> {
        public static final Int ZERO = new Int(0);

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public Object asObject() {
            return value;
        }
    }

    record Float(float value) implements IStoryVariable<java.lang.Float> {
        public static final Float ZERO = new Float(0);

        @Override
        public java.lang.Float getValue() {
            return value;
        }

        @Override
        public Object asObject() {
            return value;
        }
    }

    record Str(String value) implements IStoryVariable<String> {
        @Override
        public String getValue() {
            return value;
        }

        @Override
        public Object asObject() {
            return value;
        }
    }

    record List(InkList value) implements IStoryVariable<InkList> {
        @Override
        public InkList getValue() {
            return value;
        }

        @Override
        public Object asObject() {
            return value;
        }
    }
}
