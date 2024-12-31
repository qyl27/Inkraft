package cx.rain.mc.inkraft.neoforge.data.lang;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.ModConstants;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnUsProvider extends LanguageProvider {
    public EnUsProvider(PackOutput output) {
        super(output, Inkraft.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModConstants.Messages.COMMAND_VERSION, "Inkraft ver: %1$s, build at %2$s.");
        add(ModConstants.Messages.COMMAND_SUCCESS, "Command successful.");
        add(ModConstants.Messages.COMMAND_VARIABLE_GET, "Variable name: %1$s, value: %2$s.");
        add(ModConstants.Messages.COMMAND_VARIABLE_MISSING, "Variable %1$s is missing.");
        add(ModConstants.Messages.COMMAND_VARIABLE_SET, "Variable %1$s set to %2$s.");
        add(ModConstants.Messages.COMMAND_VARIABLE_UNSET, "Variable %1$s unsetted.");

        add(ModConstants.Messages.STORY_ALREADY_END, "Error! The story is over.");
        add(ModConstants.Messages.STORY_OPTION_OUTDATED, "Error! This option is outdated.");

        add(ModConstants.Messages.STORY_NEXT, "[Continue]");
        add(ModConstants.Messages.STORY_NEXT_CHOICE, "[Option] 1$s");
        add(ModConstants.Messages.STORY_NEXT_HINT, "Click to continue");
        add(ModConstants.Messages.STORY_NEXT_CHOICE_HINT, "Click to choose this option");
        add(ModConstants.Messages.STORY_RESUME, "[You have unfinished story]");
        add(ModConstants.Messages.STORY_RESUME_HINT, "Click to continue");
        add(ModConstants.Messages.STORY_END, "[Story over]");
    }
}
