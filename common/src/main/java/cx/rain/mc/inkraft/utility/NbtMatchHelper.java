package cx.rain.mc.inkraft.utility;

import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class NbtMatchHelper {
    public static boolean match(Tag toMatch, Tag pattern) {
        if (pattern == null) {
            return true;
        }

        if (toMatch == null) {
            return false;
        }

        if (toMatch.getId() != pattern.getId()) {
            return false;
        }

        if (toMatch.getId() == Tag.TAG_COMPOUND) {
            var toMatchCompound = (CompoundTag) toMatch;
            var patternCompound = (CompoundTag) pattern;

            for (var entry : patternCompound.tags.entrySet()) {
                if (toMatchCompound.contains(entry.getKey())) {
                    if (!match(toMatchCompound.get(entry.getKey()), entry.getValue())) {
                        return false;
                    }
                }
            }
        } else if (toMatch.getId() == Tag.TAG_LIST
                || toMatch.getId() == Tag.TAG_BYTE_ARRAY
                || toMatch.getId() == Tag.TAG_INT_ARRAY
                || toMatch.getId() == Tag.TAG_LONG_ARRAY) {
            var toMatchList = (CollectionTag<Tag>) toMatch;
            var patternList = (CollectionTag<Tag>) pattern;

            for (var i = 0; i < patternList.size(); i++) {
                var patternItem = patternList.get(i);

                var hasMatch = false;

                for (var j = 0; j < toMatchList.size(); j++) {
                    var toMatchItem = toMatchList.get(j);

                    if (match(toMatchItem, patternItem)) {
                        hasMatch = true;
                    }
                }

                if (!hasMatch) {
                    return false;
                }
            }
        } else if (!toMatch.getAsString().equals(pattern.getAsString())) {
            return false;
        }

        return true;
    }
}
