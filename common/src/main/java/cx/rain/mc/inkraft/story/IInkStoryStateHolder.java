package cx.rain.mc.inkraft.story;

import com.google.gson.Gson;

public interface IInkStoryStateHolder {
    Gson GSON = new Gson();

    String getState();
    void setState(String state);
}
