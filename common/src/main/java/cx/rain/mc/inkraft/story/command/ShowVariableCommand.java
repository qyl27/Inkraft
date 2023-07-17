package cx.rain.mc.inkraft.story.command;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.InkraftClient;
import cx.rain.mc.inkraft.networking.packet.S2CShowVariablePacket;
import cx.rain.mc.inkraft.story.StoryEngine;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public class ShowVariableCommand implements StoryCommand {
    @Override
    public String getName() {
        return "SHOW_VARIABLE";
    }

    @Override
    public int getArgumentsCount() {
        return 3;
    }

    @Override
    public BiConsumer<String[], ServerPlayer> getConsumer(StoryEngine story) {
        return (args, player) -> {
            if (args.length != 3) {
                return;
            }

            var name = args[0];
            var displayName = args[1];
            var isShow = args[2].equalsIgnoreCase("true");
            var value = story.getVariablesState().get(name).toString();

            Inkraft.getInstance().getNetworking().sendToPlayer(player, new S2CShowVariablePacket(name, displayName, isShow, value));
            story.removeVariableObserver(name);

            if (isShow) {
                story.observerVariable(name, ((variableName, newValue) -> {
                    Inkraft.getInstance().getNetworking().sendToPlayer(player, new S2CShowVariablePacket(variableName, displayName, isShow, newValue.toString()));
                }));
            }
        };
    }
}
