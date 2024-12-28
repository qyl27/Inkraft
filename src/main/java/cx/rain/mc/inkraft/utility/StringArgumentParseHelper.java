package cx.rain.mc.inkraft.utility;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.StringReader;
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

import java.util.Map;

public class StringArgumentParseHelper {
    private static final Map<Character, Character> ESCAPING_CHARS = ImmutableMap.<Character, Character>builder()
            .put('$', '$')
            .put('<', '{')
            .put('>', '}')
            .put('q', '"')
            .put('d', '\'')
            .put('s', '#')
            .build();

    public static String unescape(String str) {
        var builder = new StringBuilder();
        var it = str.codePoints().iterator();
        var escaping = false;
        while (it.hasNext()) {
            var codePoint = it.next();
            var ch = Character.toChars(codePoint);

            if (ch.length != 1) {
                continue;
            }

            if (codePoint == '$' && !escaping) {
                escaping = true;
                continue;
            }

            if (ESCAPING_CHARS.containsKey(ch[0]) && escaping) {
                builder.append(ESCAPING_CHARS.get(ch[0]));
                escaping = false;
                continue;
            }

            builder.append(ch);
        }
        return builder.toString();
    }

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
            return new TagParser(new StringReader(value)).readValue();
        } catch (CommandSyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static int parseCount(String count) {
        return parseInt(count, 1);
    }

    public static ResourceLocation parseId(String id) {
        return ResourceLocation.parse(id);
    }

    public static byte parseByte(String str, byte defaultValue) {
        if (!str.isEmpty()) {
            return Byte.parseByte(str);
        }
        return defaultValue;
    }

    public static short parseShort(String str, short defaultValue) {
        if (!str.isEmpty()) {
            return Short.parseShort(str);
        }
        return defaultValue;
    }

    public static int parseInt(String str, int defaultValue) {
        if (!str.isEmpty()) {
            return Integer.parseInt(str);
        }
        return defaultValue;
    }

    public static long parseLong(String str, long defaultValue) {
        if (!str.isEmpty()) {
            return Long.parseLong(str);
        }
        return defaultValue;
    }

    public static float parseFloat(String str, float defaultValue) {
        if (!str.isEmpty()) {
            return Float.parseFloat(str);
        }
        return defaultValue;
    }

    public static double parseDouble(String str, double defaultValue) {
        if (!str.isEmpty()) {
            return Double.parseDouble(str);
        }
        return defaultValue;
    }
}
