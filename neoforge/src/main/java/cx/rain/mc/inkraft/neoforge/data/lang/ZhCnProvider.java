package cx.rain.mc.inkraft.neoforge.data.lang;

import cx.rain.mc.inkraft.Inkraft;
import cx.rain.mc.inkraft.ModConstants;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ZhCnProvider extends LanguageProvider {
    public ZhCnProvider(PackOutput output) {
        super(output, Inkraft.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(ModConstants.Messages.COMMAND_VERSION, "Inkraft 版本：%1$s，构建于 %2$s。");
        add(ModConstants.Messages.COMMAND_SUCCESS, "操作成功完成。");
        add(ModConstants.Messages.COMMAND_VARIABLE_GET, "变量名：%1$s，值：%2$s。");
        add(ModConstants.Messages.COMMAND_VARIABLE_MISSING, "变量 %1$s 不存在！");
        add(ModConstants.Messages.COMMAND_VARIABLE_SET, "变量 %1$s 已设置为 %2$s。");
        add(ModConstants.Messages.COMMAND_VARIABLE_UNSET, "变量 %1$s 已删除。");

        add(ModConstants.Messages.STORY_ALREADY_END, "错误！剧情已结束。");
        add(ModConstants.Messages.STORY_OPTION_OUTDATED, "错误！选项已过期。");

        add(ModConstants.Messages.STORY_NEXT, "【继续】");
        add(ModConstants.Messages.STORY_NEXT_CHOICE, "【选项】%1$s");
        add(ModConstants.Messages.STORY_NEXT_HINT, "点击继续");
        add(ModConstants.Messages.STORY_NEXT_CHOICE_HINT, "点击选择");
        add(ModConstants.Messages.STORY_RESUME, "【你还在一段剧情中】");
        add(ModConstants.Messages.STORY_RESUME_HINT, "点击继续");
        add(ModConstants.Messages.STORY_END, "【剧情结束】");
    }
}
