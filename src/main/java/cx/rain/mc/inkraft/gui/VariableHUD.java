package cx.rain.mc.inkraft.gui;

import cx.rain.mc.inkraft.Constants;
import cx.rain.mc.inkraft.InkraftPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class VariableHUD {

    public static void render(GuiGraphics graphics) {
        var font = Minecraft.getInstance().font;
        var player = Minecraft.getInstance().player;

        var maxKeyLen = 0;
        var maxValueLen = 0;
        var lineCount = 1;

        var entries = InkraftPlatform.getPlayerStoryStateHolder(player).getVariables().entrySet();
        for (var entry : entries) {
            var value = entry.getValue();

            if (!value.getMiddle()) {
                continue;
            }

            var displayName = value.getLeft();
            var displayValue = value.getRight().asString();

            var keyLen = font.width(displayName);
            var valueLen = font.width(displayValue);

            if (keyLen > maxKeyLen) {
                maxKeyLen = keyLen;
            }

            if (valueLen > maxValueLen) {
                maxValueLen = valueLen;
            }

            lineCount += 1;
        }

        var width = maxKeyLen + maxValueLen + 10;
        var title = Component.translatable(Constants.HUD_INFO);
        var titleWidth = font.width(title);
        if (titleWidth > width) {
            width = titleWidth;
        }

        var height = font.lineHeight * lineCount + 2 * (lineCount - 1);

        var x = 0;
        var y = (graphics.guiHeight() - height) / 2;

        var backgroundColor = ((int) (255.0 * Minecraft.getInstance().options.textBackgroundOpacity().get())) << 24;

        graphics.fill(x, y, x + width, y + height, backgroundColor);

        graphics.drawCenteredString(font, title, x + width / 2, y, 16777215);

        var i = 0;
        for (var entry : entries) {
            var value = entry.getValue();
            if (!value.getMiddle()) {
                continue;
            }

            var displayName = value.getLeft();
            var displayValue = value.getRight().asString();

            int newY = y + font.lineHeight * (i + 1) + 2 * (i + 1);
            graphics.drawString(font, displayName, x, newY, 16777215);
            graphics.drawString(font, displayValue, x + maxKeyLen + 10, newY, 16777215);

            i += 1;
        }
    }
}
