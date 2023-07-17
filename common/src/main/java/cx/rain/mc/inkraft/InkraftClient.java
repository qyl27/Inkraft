package cx.rain.mc.inkraft;

import com.mojang.datafixers.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class InkraftClient {
    private static InkraftClient INSTANCE;

    private final Map<String, Pair<String, String>> variables = new HashMap<>();

    // Todo: use capability instead.
    public InkraftClient() {
    }

    public static InkraftClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InkraftClient();
        }

        return INSTANCE;
    }

    public Map<String, Pair<String, String>> getVariables() {
        return variables;
    }

    public void putVariable(String name, String displayName, boolean isShow, String value) {
        if (isShow) {
            variables.put(name, new Pair<>(displayName, value));
        } else {
            variables.remove(name);
        }
    }

    public void clear() {
        variables.clear();
    }
}
