package cx.rain.mc.inkraft.fabric.mixins.mixin;

import cx.rain.mc.inkraft.fabric.mixins.interfaces.IPlayerMixin;
import cx.rain.mc.inkraft.fabric.platform.StoryStateHolder;
import cx.rain.mc.inkraft.story.IStoryVariable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin implements IPlayerMixin {
    private UUID inkraft$continueToken = UUID.randomUUID();
    private String inkraft$state = "";
    private String inkraft$lastMessage = "";
    private boolean inkraft$inStory = false;
    private final Map<String, Triple<String, Boolean, IStoryVariable.IStoryVariable>> inkraft$variables = new HashMap<>();

    @Override
    public String inkraft$getState() {
        return inkraft$state;
    }

    @Override
    public void inkraft$setState(String state) {
        inkraft$state = state;
    }

    public String inkraft$getLastMessage() {
        return inkraft$lastMessage;
    }

    public void inkraft$setLastMessage(String message) {
        this.inkraft$lastMessage = message;
    }

    @Override
    public UUID inkraft$getContinueToken() {
        return inkraft$continueToken;
    }

    @Override
    public void inkraft$setContinueToken(UUID token) {
        inkraft$continueToken = token;
    }

    @Override
    public boolean inkraft$isInStory() {
        return inkraft$inStory;
    }

    @Override
    public void inkraft$setInStory(boolean inStory) {
        inkraft$inStory = inStory;
    }

    @Override
    public Map<String, Triple<String, Boolean, IStoryVariable.IStoryVariable>> inkraft$getVariables() {
        return inkraft$variables;
    }

    @Override
    public void inkraft$putVariable(String name, String displayName, boolean isShow, IStoryVariable.IStoryVariable value) {
        inkraft$variables.put(name, new MutableTriple<>(displayName, isShow, value));
    }

    @Override
    public IStoryVariable.IStoryVariable inkraft$getVariable(String name) {
        return inkraft$variables.get(name).getRight();
    }

    @Override
    public void inkraft$hideVariables() {
        for (var variable : inkraft$variables.entrySet()) {
            if (variable.getValue().getMiddle()) {
                var value = variable.getValue();
                inkraft$putVariable(variable.getKey(), value.getLeft(), false, value.getRight());
            }
        }
    }

    @Override
    public void inkraft$clearVariables() {
        inkraft$variables.clear();
    }

    private ResourceLocation inkraft$currentStory = null;
    private boolean inkraft$doesAutoContinue = false;
    private long inkraft$autoContinueSpeedTicks = -1;

    @Override
    public ResourceLocation inkraft$getCurrentStory() {
        return inkraft$currentStory;
    }

    @Override
    public void inkraft$setCurrentStory(ResourceLocation story) {
        inkraft$currentStory = story;
    }

    @Override
    public boolean inkraft$getCurrentAutoContinue() {
        return inkraft$doesAutoContinue;
    }

    @Override
    public void inkraft$setCurrentAutoContinue(boolean autoContinue) {
        inkraft$doesAutoContinue = autoContinue;
    }

    @Override
    public long inkraft$getCurrentAutoContinueSpeed() {
        return inkraft$autoContinueSpeedTicks;
    }

    @Override
    public void inkraft$setCurrentAutoContinueSpeed(long autoContinueSpeed) {
        inkraft$autoContinueSpeedTicks = autoContinueSpeed;
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    private void inkraft$addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        var compound = new CompoundTag();
        compound.putString(StoryStateHolder.TAG_STATE_NAME, inkraft$getState());
        compound.putString(StoryStateHolder.TAG_LAST_MESSAGE_NAME, inkraft$getLastMessage());
        compound.putUUID(StoryStateHolder.TAG_TOKEN_NAME, inkraft$getContinueToken());
        compound.putBoolean(StoryStateHolder.TAG_IN_STORY_NAME, inkraft$isInStory());

        var list = new ListTag();
        for (var entry : inkraft$variables.entrySet()) {
            var compoundTag = new CompoundTag();
            compoundTag.putString(StoryStateHolder.TAG_VARIABLES_NAME_NAME, entry.getKey());

            compoundTag.putString(StoryStateHolder.TAG_VARIABLES_DISPLAY_NAME, entry.getValue().getLeft());
            compoundTag.putBoolean(StoryStateHolder.TAG_VARIABLES_SHOW_NAME, entry.getValue().getMiddle());
            compoundTag.putString(StoryStateHolder.TAG_VARIABLES_VALUE_NAME, entry.getValue().getRight().asString());
            list.add(compoundTag);
        }
        compound.put(StoryStateHolder.TAG_VARIABLES_NAME, list);

        if (inkraft$currentStory != null) {
            compound.putString(StoryStateHolder.TAG_CURRENT_STORY_NAME, inkraft$currentStory.toString());
            compound.putBoolean(StoryStateHolder.TAG_AUTO_CONTINUE_ENABLED, inkraft$doesAutoContinue);
            compound.putLong(StoryStateHolder.TAG_AUTO_CONTINUE_SPEED, inkraft$autoContinueSpeedTicks);
        }

        tag.put(StoryStateHolder.TAG_INKRAFT_NAME, compound);
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    private void inkraft$readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        var compound = tag.getCompound(StoryStateHolder.TAG_INKRAFT_NAME);
        inkraft$setState(compound.getString(StoryStateHolder.TAG_STATE_NAME));
        inkraft$setLastMessage(compound.getString(StoryStateHolder.TAG_LAST_MESSAGE_NAME));

        if (tag.contains(StoryStateHolder.TAG_TOKEN_NAME)) {
            inkraft$setContinueToken(compound.getUUID(StoryStateHolder.TAG_TOKEN_NAME));
        }

        inkraft$setInStory(compound.getBoolean(StoryStateHolder.TAG_IN_STORY_NAME));

        for (var entry : compound.getList(StoryStateHolder.TAG_VARIABLES_NAME, Tag.TAG_COMPOUND)) {
            var compoundTag = (CompoundTag) entry;
            var name = compoundTag.getString(StoryStateHolder.TAG_VARIABLES_NAME_NAME);
            var displayName = compoundTag.getString(StoryStateHolder.TAG_VARIABLES_DISPLAY_NAME);
            var isShow = compoundTag.getBoolean(StoryStateHolder.TAG_VARIABLES_SHOW_NAME);
            var value = compoundTag.getString(StoryStateHolder.TAG_VARIABLES_VALUE_NAME);
            inkraft$putVariable(name, displayName, isShow, IStoryVariable.IStoryVariable.fromString(value));
        }

        var story = compound.getString(StoryStateHolder.TAG_CURRENT_STORY_NAME);
        if (!story.isBlank()) {
            inkraft$setCurrentStory(ResourceLocation.parse(story));
            inkraft$setCurrentAutoContinue(compound.getBoolean(StoryStateHolder.TAG_AUTO_CONTINUE_ENABLED));
            inkraft$setCurrentAutoContinueSpeed(compound.getLong(StoryStateHolder.TAG_AUTO_CONTINUE_SPEED));
        }
    }
}
