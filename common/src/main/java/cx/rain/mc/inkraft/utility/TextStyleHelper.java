package cx.rain.mc.inkraft.utility;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextStyleHelper {
    public static final char STYLE_CHAR = '\u00A7';
    public static final List<Character> STYLE_CODES = new ArrayList<>();
    
    public static Pattern HEX_COLOR = Pattern.compile("§#(\\w{5}[0-9a-f])");

    static {
        STYLE_CODES.add('0');
        STYLE_CODES.add('1');
        STYLE_CODES.add('2');
        STYLE_CODES.add('3');
        STYLE_CODES.add('4');
        STYLE_CODES.add('5');
        STYLE_CODES.add('6');
        STYLE_CODES.add('7');
        STYLE_CODES.add('8');
        STYLE_CODES.add('9');
        STYLE_CODES.add('a');
        STYLE_CODES.add('A');
        STYLE_CODES.add('b');
        STYLE_CODES.add('B');
        STYLE_CODES.add('c');
        STYLE_CODES.add('C');
        STYLE_CODES.add('d');
        STYLE_CODES.add('D');
        STYLE_CODES.add('e');
        STYLE_CODES.add('E');
        STYLE_CODES.add('f');
        STYLE_CODES.add('F');
        STYLE_CODES.add('k');
        STYLE_CODES.add('K');
        STYLE_CODES.add('l');
        STYLE_CODES.add('L');
        STYLE_CODES.add('m');
        STYLE_CODES.add('M');
        STYLE_CODES.add('n');
        STYLE_CODES.add('N');
        STYLE_CODES.add('o');
        STYLE_CODES.add('O');
        STYLE_CODES.add('r');
        STYLE_CODES.add('R');
    }

    public static Component parseStyle(String literalText) {
//        var buffer = new StringBuffer();
//        var matcher = HEX_COLOR.matcher(literalText);
//
//        while(matcher.find()) {
//            var color = TextColor.parseColor("#" + matcher.group(1));
//            matcher.appendReplacement(buffer, color.toString());
//        }
//
//        var result = parseStyleChars('&', matcher.appendTail(buffer).toString());
        // Todo: qyl27: use hex color?
        var result = parseStyleChars('&', literalText);
        return Component.literal(result);
    }

    private static String parseStyleChars(char styleChar, String literalText) {
        char[] chars = literalText.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == styleChar && STYLE_CODES.contains(chars[i + 1])) {
                chars[i] = STYLE_CHAR;
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }
        return new String(chars);
    }
}
