package cx.rain.mc.inkraft.story;

import com.google.gson.Gson;

import java.util.UUID;

public interface IInkStoryStateHolder {
    Gson GSON = new Gson();

    String getState();
    void setState(String state);

    UUID getContinueToken();
    void setContinueToken(UUID token);
}
