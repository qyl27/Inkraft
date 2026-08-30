package cx.rain.mc.inkraft.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringArgumentParseHelper {
    private static final Logger log = LoggerFactory.getLogger(StringArgumentParseHelper.class);

    public static Holder<Item> parseItem(HolderLookup.Provider registries, String id) {
        var rl = parseId(id);
        return registries.lookupOrThrow(Registries.ITEM).getOrThrow(ResourceKey.create(Registries.ITEM, rl));
    }

    public static TagKey<Item> parseItemTag(String id) {
        var rl = parseId(id);
        return TagKey.create(Registries.ITEM, rl);
    }

    public static NbtPathArgument.NbtPath parseNbtPath(String value) {
        try {
            return NbtPathArgument.NbtPath.of(value);
        } catch (CommandSyntaxException ex) {
            log.warn("Malformed NbtPath: {}", value);
            throw new RuntimeException(ex);
        }
    }

    public static Tag parseNbt(String value) {
        try {
            return TagParser.create(NbtOps.INSTANCE).parseFully(value);
        } catch (CommandSyntaxException ex) {
            log.warn("Malformed Tag: {}", value);
            throw new RuntimeException(ex);
        }
    }

    public static Identifier parseId(String id) {
        return Identifier.parse(id);
    }
}
