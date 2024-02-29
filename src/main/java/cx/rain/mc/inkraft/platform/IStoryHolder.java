package cx.rain.mc.inkraft.platform;

import cx.rain.mc.inkraft.story.IPlayerStoryState;

import java.util.Map;

public interface IStoryHolder {
    boolean hasCurrentStory();
    void setCurrentStory(String name);
    void isCurrentStory(String name);

    Map<String, IPlayerStoryState> getStories(String id);
//    IPlayerStoryState getStory(int index);
    IPlayerStoryState getStory(String name);
    void addStory(IPlayerStoryState story);
    void addStory(String nane, IPlayerStoryState story);
//    void removeStory(int index);
    void removeStory(String nane);
    void clearStories();
}
