package cx.rain.mc.inkraft.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextStyleHelper {
    public static Pattern HEX_COLOR = Pattern.compile("/&([0-9a-fklmnor])|#([0-9a-f]{6})/gmi");

    public static Component parseStyle(String literalText) {
        var component = Component.empty();
        var matcher = HEX_COLOR.matcher(literalText);

        var cursor = 0;

        if (!matcher.hasMatch()) {
            return Component.literal(literalText);
        }

        while (matcher.find()) {
            var index = matcher.start(0);
            var text = Component.literal(literalText.substring(cursor, index));
            cursor = matcher.end(0);

            var group1 = matcher.group(1);
            if (group1 != null) {
                var color = ChatFormatting.getByCode(group1.charAt(0));
                text.withStyle(color);
            }

            var group2 = matcher.group(2);
            if (group2 != null) {
                text.withColor(Integer.parseInt(group2, 16));
            }

            component.append(text);
        }

        return component;
    }
}
