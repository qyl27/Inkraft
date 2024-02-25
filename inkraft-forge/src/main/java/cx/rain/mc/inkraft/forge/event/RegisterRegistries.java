package cx.rain.mc.inkraft.forge.event;

import cx.rain.mc.inkraft.Inkraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;

@Mod.EventBusSubscriber(modid = Inkraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterRegistries {
    @SubscribeEvent
    public static void onRegisterRegistries(NewRegistryEvent event) {
//        event.create(new RegistryBuilder<StoryCommand>().setName(StoryCommandsRegistry.REGISTRY_NAME));
//        event.create(new RegistryBuilder<StoryFunction>().setName(StoryFunctionsRegistry.REGISTRY_NAME));
    }
}
