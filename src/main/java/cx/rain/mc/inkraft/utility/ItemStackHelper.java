package cx.rain.mc.inkraft.utility;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class ItemStackHelper {
    public static Predicate<ItemStack> predicateIdOrTag(HolderLookup.Provider registries, String id) {
        if (id.startsWith("#")) {
            var tk = StringArgumentParseHelper.parseItemTag(id);
            return item -> item.is(tk);
        } else {
            var i = StringArgumentParseHelper.parseItem(registries, id);
            return item -> item.is(i);
        }
    }

    public static Predicate<ItemStack> predicateNbt(HolderLookup.Provider registries, String path, String value) {
        if (path.isEmpty() || value.isEmpty()) {
            return item -> true;
        }

        var n = StringArgumentParseHelper.parseNbt(value);
        var p = StringArgumentParseHelper.parseNbtPath(path);
        return item -> {
            try {
                var l = p.get(item.save(registries));
                return !l.isEmpty() && l.stream().allMatch(e -> e.equals(n));
            } catch (CommandSyntaxException ignored) {
            }

            return false;
        };
    }

    public static Predicate<ItemStack> createPredicate(HolderLookup.Provider registries, String id, String tagPath, String tagValue) {
        return predicateIdOrTag(registries, id)
                .and(predicateNbt(registries, tagPath, tagValue));
    }

    public static List<ItemStack> match(ServerPlayer player, Predicate<ItemStack> predicate) {
        return player.getInventory()
                .items
                .stream()
                .filter(predicate)
                .toList();
    }

    public static ItemStack createItemStack(HolderLookup.Provider registries, String id, String count, String tagPath, String tagValue) {
        var item = StringArgumentParseHelper.parseItem(registries, id);
        var result = new ItemStack(item);

        if (!count.isEmpty()) {
            var c = Integer.parseInt(count);
            result.setCount(c);
        }

        if (!tagPath.isEmpty() && !tagValue.isEmpty()) {
            var p = StringArgumentParseHelper.parseNbtPath(tagPath);
            var t = StringArgumentParseHelper.parseNbt(tagValue);
            var n = result.save(registries);
            try {
                p.set(n, t);
                result = ItemStack.parse(registries, n).orElseThrow();
            } catch (CommandSyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }

        return result;
    }
}
