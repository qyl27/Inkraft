package cx.rain.mc.inkraft.utility;

import cx.rain.mc.inkraft.Inkraft;
import net.minecraft.resources.Identifier;

public class IdHelper {
    public static Identifier modLoc(String path) {
        return Identifier.fromNamespaceAndPath(Inkraft.MODID, path);
    }

    public static Identifier of(String id) {
        return Identifier.parse(id);
    }
}
