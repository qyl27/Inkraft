package cx.rain.mc.inkraft.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class ItemStackHelper {
    public static Predicate<ItemStack> id(HolderLookup.Provider registries, String id) {
        if (id.startsWith("#")) {
            var tk = ArgumentParseHelper.parseItemTag(registries, id);
            return item -> item.is(tk);
        } else {
            var i = ArgumentParseHelper.parseItem(registries, id);
            return item -> item.is(i);
        }
    }

    public static Predicate<ItemStack> nbt(HolderLookup.Provider registries, String path, String value) {
        var n = ArgumentParseHelper.parseTag(registries, value);
        var p = ArgumentParseHelper.parseNbtPath(path);
        return item -> {
            try {
                var l = p.get(item.save(registries));
                return !l.isEmpty() && l.stream().allMatch(e -> e.equals(n));
            } catch (CommandSyntaxException ignored) {
            }

            return false;
        };
    }

    public static Predicate<ItemStack> parsePredicate(HolderLookup.Provider registries, Object[] args, int idIndex, int nbtIndex) {
        assert args.length >= idIndex + 1;
        var predicate = id(registries, args[idIndex].toString());

        if (args.length >= nbtIndex + 2) {
            predicate = predicate.and(nbt(registries, args[nbtIndex].toString(), args[nbtIndex + 1].toString()));
        }

        return predicate;
    }

    public static List<ItemStack> match(ServerPlayer player, Predicate<ItemStack> predicate) {
        return player.getInventory()
                .items
                .stream()
                .filter(predicate)
                .toList();
    }

    public static ItemStack parseItemStack(HolderLookup.Provider registries, Object[] args, int startIndex) {
        assert args.length >= startIndex + 1;
        var item = ArgumentParseHelper.parseItem(registries, args[startIndex].toString());

        var result = new ItemStack(item);

        if (args.length >= startIndex + 4) {
            var p = ArgumentParseHelper.parseNbtPath(args[startIndex + 2].toString());
            var t = ArgumentParseHelper.parseTag(registries, args[startIndex + 3].toString());
            var n = result.save(registries);
            try {
                p.set(n, t);
            } catch (CommandSyntaxException ex) {
                throw new RuntimeException(ex);
            }
            result = ItemStack.parse(registries, n).orElseThrow();
        }

        return result;
    }
}
