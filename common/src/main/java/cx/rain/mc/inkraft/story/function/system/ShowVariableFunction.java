package cx.rain.mc.inkraft.story.function.system;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.networking.packet.S2CShowVariablePacket;
import cx.rain.mc.inkraft.story.StoryEngine;
import cx.rain.mc.inkraft.story.function.StoryFunction;
import cx.rain.mc.inkraft.story.function.StoryFunctionResults;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class ShowVariableFunction implements StoryFunction {

    @Override
    public String getName() {
        return "showVariable";
    }

    @Override
    public BiFunction<Object[], ServerPlayer, StoryFunctionResults.IStoryFunctionResult> func(StoryEngine engine) {
        return (args, player) -> {
            if (args.length != 3) {
                return StoryFunctionResults.BoolResult.FALSE;
            }

            var name = args[0].toString();
            var displayName = args[1].toString();
            var isShow = args[2].toString().equalsIgnoreCase("true");
            var value = engine.getVariablesState().get(name).toString();

            Inkraft.getInstance().getNetworking().sendToPlayer(player, new S2CShowVariablePacket(name, displayName, isShow, value));
            engine.removeVariableObserver(name);

            if (isShow) {
                engine.observerVariable(name, ((variableName, newValue) -> {
                    Inkraft.getInstance().getNetworking().sendToPlayer(player, new S2CShowVariablePacket(variableName, displayName, true, newValue.toString()));
                }));
            }

            return StoryFunctionResults.BoolResult.TRUE;
        };
    }
}
