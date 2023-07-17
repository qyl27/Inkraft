package cx.rain.mc.inkraft.story.command;

import cx.rain.mc.inkraft.Inkraft;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class StoryCommands {
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(Inkraft.MODID, "story_commands");
    public static final ResourceKey<Registry<StoryCommand>> REGISTRY_KEY = ResourceKey.createRegistryKey(REGISTRY_NAME);
    public static final Registrar<StoryCommand> REGISTRAR = RegistrarManager.get(Inkraft.MODID).<StoryCommand>builder(REGISTRY_NAME).build();

    public static final DeferredRegister<StoryCommand> COMMANDS = DeferredRegister.create(Inkraft.MODID, REGISTRY_KEY);

    public static void register() {
        COMMANDS.register();
    }

//    public static final RegistrySupplier<StoryCommand> COMMAND = COMMANDS.register("command", CommandCommand::new);
//    public static final RegistrySupplier<StoryCommand> GIVE_ITEM = COMMANDS.register("give_item", GiveItemCommand::new);
//    public static final RegistrySupplier<StoryCommand> AUTO_CONTINUE = COMMANDS.register("auto_continue", AutoContinueCommand::new);
//    public static final RegistrySupplier<StoryCommand> AUTO_CONTINUE_SPEED = COMMANDS.register("auto_continue_speed", AutoContinueSpeedCommand::new);
//    public static final RegistrySupplier<StoryCommand> SHOW_VARIABLE = COMMANDS.register("show_variable", ShowVariableCommand::new);
}
