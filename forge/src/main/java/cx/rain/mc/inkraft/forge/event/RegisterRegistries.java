package cx.rain.mc.inkraft.forge.event;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.command.InkraftCommand;
import cx.rain.mc.inkraft.story.command.StoryCommand;
import cx.rain.mc.inkraft.story.command.StoryCommandsRegistry;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.function.StoryFunctionsRegistry;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = Inkraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterRegistries {
    @SubscribeEvent
    public static void onRegisterRegistries(NewRegistryEvent event) {
//        event.create(new RegistryBuilder<StoryCommand>().setName(StoryCommandsRegistry.REGISTRY_NAME));
//        event.create(new RegistryBuilder<StoryFunction>().setName(StoryFunctionsRegistry.REGISTRY_NAME));
    }
}
