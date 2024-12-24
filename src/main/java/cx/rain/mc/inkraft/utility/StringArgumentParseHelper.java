package cx.rain.mc.inkraft.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class StringArgumentParseHelper {
    public static Holder<Item> parseItem(HolderLookup.Provider registries, String id) {
        var rl = ResourceLocation.parse(id);
        return registries.lookupOrThrow(Registries.ITEM).getOrThrow(ResourceKey.create(Registries.ITEM, rl));
    }

    public static TagKey<Item> parseItemTag(String id) {
        var rl = ResourceLocation.parse(id);
        return TagKey.create(Registries.ITEM, rl);
    }

    public static NbtPathArgument.NbtPath parseNbtPath(String value) {
        try {
            return NbtPathArgument.NbtPath.of(value);
        } catch (CommandSyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Tag parseNbt(String value) {
        try {
            return TagParser.parseTag(value);
        } catch (CommandSyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static int parseCount(String count) {
        if (!count.isEmpty()) {
            return Integer.parseInt(count);
        }
        return 1;
    }
}
