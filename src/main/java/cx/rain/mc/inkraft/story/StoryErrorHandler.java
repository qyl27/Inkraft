package cx.rain.mc.inkraft.story;

import com.bladecoder.ink.runtime.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoryErrorHandler implements Error.ErrorHandler {
    public static StoryErrorHandler INSTANCE = new StoryErrorHandler();

    private static final Logger log = LoggerFactory.getLogger(StoryErrorHandler.class);

    @Override
    public void error(String message, Error.ErrorType type) {
        if (type == Error.ErrorType.Author) {
            log.trace("Ink script tip: {}", message);
            return;
        }

        if (type == Error.ErrorType.Warning) {
            log.warn("Ink script warn: {}", message);
            return;
        }

        log.error("Ink script error: {}", message);
    }
}
