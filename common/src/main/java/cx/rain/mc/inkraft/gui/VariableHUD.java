package cx.rain.mc.inkraft.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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

//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        graphics.fill(x, y, x + width, y + height, backgroundColor);

        graphics.drawCenteredString(font, title, x + width / 2, y, 16777215);

        var i = 0;
        for (var entry : InkraftClient.getInstance().getVariables().entrySet()) {
            var value = entry.getValue();
            var displayName = value.getFirst();
            var displayValue = value.getSecond();

            int newY = y + font.lineHeight * (i + 1) + 2 * (i + 1);
            graphics.drawString(font, displayName, x, newY, 16777215);
            graphics.drawString(font, displayValue, x + maxKeyLen + 10, newY, 16777215);

            i += 1;
        }

//        RenderSystem.disableBlend();
    }
}