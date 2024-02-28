package cx.rain.mc.inkraft.mod.platform;

import cx.rain.mc.inkraft.story.PlayerStory;

public interface IStoryHolder {
    PlayerStory getStory(String id);
    void addStory(PlayerStory story);
    void clearStories();

//    String getState();
//    void setState(String state);
//
//    String getLastMessage();
//    void setLastMessage(String message);
//
//    UUID getContinueToken();
//    void setContinueToken(UUID token);
//
//    boolean isInStory();
//    void setInStory(boolean inStory);
//
//    void clearState();
//
//    Map<String, Triple<String, Boolean, StoryVariables.IStoryVariable>> getVariables();
//    void putVariable(String name, String displayName, boolean isShow, StoryVariables.IStoryVariable value);
//    StoryVariables.IStoryVariable getVariable(String name);
//    void hideVariables();
//    void clearVariables();
//
//    ResourceLocation getCurrentStory();
//    void setCurrentStory(ResourceLocation story);
//    boolean getCurrentAutoContinue();
//    void setCurrentAutoContinue(boolean autoContinue);
//    long getCurrentAutoContinueSpeed();
//    void setCurrentAutoContinueSpeed(long autoContinueSpeed);
}
