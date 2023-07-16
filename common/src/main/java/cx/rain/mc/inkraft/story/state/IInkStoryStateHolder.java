package cx.rain.mc.inkraft.story.state;

import java.util.UUID;

public interface IInkStoryStateHolder {
    String getState();
    void setState(String state);

    UUID getContinueToken();
    void setContinueToken(UUID token);

    boolean isInStory();
    void setInStory(boolean inStory);

    void clearState();
}
