package cx.rain.mc.inkraft.gui;

import cx.rain.mc.inkraft.InkraftClient;
import cx.rain.mc.inkraft.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class VariableHUD {

    public static void render(GuiGraphics graphics) {
        var font = Minecraft.getInstance().font;

        int maxKeyLen = 0;
        int maxValueLen = 0;
        int lineCount = 1;

        for (var entry : InkraftClient.getInstance().getVariables().entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            var displayName = value.getFirst();
            var displayValue = value.getSecond();

            var keyLen = font.width(displayName);
            var valueLen = font.width(displayValue);

            if (keyLen > maxKeyLen) {
                maxValueLen = keyLen;
            }

            if (valueLen > maxValueLen) {
                maxValueLen = valueLen;
            }

            lineCount += 1;
        }

        var width = maxKeyLen + maxValueLen;
        var title = Component.translatable(Constants.HUD_INFO);
        var titleWidth = font.width(title);
        if (titleWidth > width) {
            width = titleWidth;
        }

        var height = font.lineHeight * lineCount + 2 * (lineCount - 1);

        var x = 0;
        var y = (graphics.guiHeight() - height) / 2;

        var backgroundColor = ((int) (255.0 * Minecraft.getInstance().options.textBackgroundOpacity().get())) << 24;
        graphics.fill(x, y, width, height, backgroundColor);
    }
}
