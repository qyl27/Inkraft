package cx.rain.mc.inkraft.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TextStyleHelper {
    public static Map<Integer, Function<Style, Style>> COLOR_CODES = new HashMap<>();
    public static Map<Integer, Function<Style, Style>> STYLE_CODES = new HashMap<>();

    static {
        COLOR_CODES.put((int) '0', style -> style.withColor(ChatFormatting.BLACK));
        COLOR_CODES.put((int) '1', style -> style.withColor(ChatFormatting.DARK_BLUE));
        COLOR_CODES.put((int) '2', style -> style.withColor(ChatFormatting.DARK_GREEN));
        COLOR_CODES.put((int) '3', style -> style.withColor(ChatFormatting.DARK_AQUA));
        COLOR_CODES.put((int) '4', style -> style.withColor(ChatFormatting.DARK_RED));
        COLOR_CODES.put((int) '5', style -> style.withColor(ChatFormatting.DARK_PURPLE));
        COLOR_CODES.put((int) '6', style -> style.withColor(ChatFormatting.GOLD));
        COLOR_CODES.put((int) '7', style -> style.withColor(ChatFormatting.GRAY));
        COLOR_CODES.put((int) '8', style -> style.withColor(ChatFormatting.DARK_GRAY));
        COLOR_CODES.put((int) '9', style -> style.withColor(ChatFormatting.BLUE));
        COLOR_CODES.put((int) 'A', style -> style.withColor(ChatFormatting.GREEN));
        COLOR_CODES.put((int) 'B', style -> style.withColor(ChatFormatting.AQUA));
        COLOR_CODES.put((int) 'C', style -> style.withColor(ChatFormatting.RED));
        COLOR_CODES.put((int) 'D', style -> style.withColor(ChatFormatting.LIGHT_PURPLE));
        COLOR_CODES.put((int) 'E', style -> style.withColor(ChatFormatting.YELLOW));
        COLOR_CODES.put((int) 'F', style -> style.withColor(ChatFormatting.WHITE));
        COLOR_CODES.put((int) 'R', style -> Style.EMPTY);
        STYLE_CODES.put((int) 'K', style -> style.withObfuscated(true));
        STYLE_CODES.put((int) 'L', style -> style.withBold(true));
        STYLE_CODES.put((int) 'M', style -> style.withStrikethrough(true));
        STYLE_CODES.put((int) 'N', style -> style.withUnderlined(true));
        STYLE_CODES.put((int) 'O', style -> style.withItalic(true));
    }

    public static Component parseStyle(String literalText) {
        var component = Component.empty();

        var it = literalText.codePoints().iterator();
        var escaping = false;
        var builder = new StringBuilder();
        var style = Style.EMPTY;
        var buildingHexColor = false;
        var hexColorBuilder = new StringBuilder();
        while (it.hasNext()) {
            var codePoint = it.nextInt();
            var ch = Character.toChars(codePoint);
            if (codePoint == '&') {
                if (escaping) {
                    escaping = false;
                    builder.append('&');
                } else {
                    escaping = true;
                }
                continue;
            }

            if (escaping) {
                var u = Character.toUpperCase(codePoint);
                if (buildingHexColor) {
                    if (!COLOR_CODES.containsKey(u)) {
                        builder.append(hexColorBuilder);
                        buildingHexColor = false;
                        hexColorBuilder = new StringBuilder();
                        escaping = false;
                        continue;
                    }

                    hexColorBuilder.append(ch);
                    if (hexColorBuilder.length() == 6) {
                        var hex = TextColor.parseColor("#" + hexColorBuilder);
                        if (hex.isSuccess()) {
                            component.append(Component.literal(builder.toString()).withStyle(style));
                            builder = new StringBuilder();
                            style = Style.EMPTY.withColor(hex.getOrThrow());
                        }
                        buildingHexColor = false;
                        hexColorBuilder = new StringBuilder();
                        escaping = false;
                    }
                    continue;
                }

                if (COLOR_CODES.containsKey(u)) {
                    component.append(Component.literal(builder.toString()).withStyle(style));
                    builder = new StringBuilder();
                    style = COLOR_CODES.get(u).apply(Style.EMPTY);
                    hexColorBuilder = new StringBuilder();
                    escaping = false;
                    continue;
                } else if (STYLE_CODES.containsKey(u)) {
                    style = STYLE_CODES.get(u).apply(style);
                    escaping = false;
                    continue;
                } else if (u == 'H') {
                    buildingHexColor = true;
                    hexColorBuilder = new StringBuilder();
                    continue;
                }
            }

            builder.append(ch);
        }

        component.append(Component.literal(builder.toString()).withStyle(style));
        return component;
    }
}
