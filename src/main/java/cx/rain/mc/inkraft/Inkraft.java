package cx.rain.mc.inkraft;

import java.io.IOException;
import java.util.Properties;

public class Inkraft {
    public static final String ID = "inkraft";
    public static final String VERSION;

    static {
        var properties = new Properties();
        var version = "";
        try {
            properties.load(Inkraft.class.getResourceAsStream("/build.properties"));
            version = properties.getProperty("mod_version") + "+mc" + properties.getProperty("minecraft_version");
        } catch (IOException ex) {
            version = "Unknown";
        }
        VERSION = version;
    }

    private static Inkraft INSTANCE;

    public Inkraft() {
        INSTANCE = this;

    }

    public static Inkraft getInstance() {
        return INSTANCE;
    }

    public void init() {

    }
}
