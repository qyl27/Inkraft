package cx.rain.mc.inkraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Inkraft {
    public static final String MODID = "inkraft";
    public static final String VERSION = "1.20.1-1.0.0";

    private static Inkraft INSTANCE;

    private Logger logger = LoggerFactory.getLogger(MODID);

    public Inkraft() {
        INSTANCE = this;

        logger.info("Initializing Inkraft. Ver: " + VERSION);


    }

    public static Inkraft getInstance() {
        return INSTANCE;
    }

    public Logger getLogger() {
        return logger;
    }

    public void init() {

    }
}
