package cx.rain.mc.inkraft.fabric.listener;

import cx.rain.mc.inkraft.fabric.bridge.IInkraftServerPlayer;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

@Slf4j
public class PlayerCopyHandler {
    public static void copyPlayer(HolderLookup.Provider registries, ServerPlayer from, ServerPlayer to) {
        var fromData = ((IInkraftServerPlayer) from).inkraft$getPlayerData();
        var toData = ((IInkraftServerPlayer) to).inkraft$getPlayerData();

        var writeProblems = new ProblemReporter.Collector();
        var output = TagValueOutput.createWithContext(writeProblems, registries);
        fromData.serialize(output);
        if (!writeProblems.isEmpty()) {
            log.error("Error while serializing player data for {}: {}", from.getUUID(), writeProblems);
            return;
        }

        var tag = output.buildResult();
        var readProblems = new ProblemReporter.Collector();
        var input = TagValueInput.create(readProblems, registries, tag);
        toData.deserialize(input);
        if (!readProblems.isEmpty()) {
            log.error("Error while deserializing player data for {}: {}", from.getUUID(), readProblems);
        }
    }
}
