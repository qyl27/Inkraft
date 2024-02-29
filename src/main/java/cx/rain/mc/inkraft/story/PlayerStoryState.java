package cx.rain.mc.inkraft.story;

import cx.rain.mc.inkraft.utility.StoryVariable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public class PlayerStoryState implements IPlayerStoryState {

    @Override
    public ResourceLocation getStoryPath() {
        return null;
    }

    @Override
    public void setStoryPath(ResourceLocation path) {

    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public void setState(String state) {

    }

    @Override
    public UUID getStepToken() {
        return null;
    }

    @Override
    public void setStepToken(UUID token) {

    }

    @Override
    public int getNextStep() {
        return 0;
    }

    @Override
    public void addNextStep(int next) {

    }

    @Override
    public boolean hasNextStep() {
        return false;
    }

    @Override
    public String getPreviousMessage() {
        return null;
    }

    @Override
    public void setPreviousMessage(String message) {

    }

    @Override
    public boolean isEnd() {
        return false;
    }

    @Override
    public void setEnd(boolean end) {

    }

    @Override
    public Map<String, StoryVariable.IValue> getVariables() {
        return null;
    }

    @Override
    public void putVariable(String name, StoryVariable.IValue value) {

    }

    @Override
    public StoryVariable.IValue getVariable(String name) {
        return null;
    }

    @Override
    public void removeVariable(String name) {

    }

    @Override
    public CompoundTag serialize() {
        return null;
    }

    @Override
    public void deserialize(CompoundTag tag) {

    }
}
