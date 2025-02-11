package cx.rain.mc.inkraft;

public class ModConstants {
    public static class Messages {
        public static final String COMMAND_VERSION = "message.command.version";
        public static final String COMMAND_SUCCESS = "message.command.success";
        public static final String COMMAND_VARIABLE_GET = "message.command.variable.get";
        public static final String COMMAND_VARIABLE_MISSING = "message.command.variable.missing";
        public static final String COMMAND_VARIABLE_SET = "message.command.variable.set";
        public static final String COMMAND_VARIABLE_UNSET = "message.command.variable.unset";

        public static final String STORY_ALREADY_END = "message.story.already_end";
        public static final String STORY_OPTION_OUTDATED = "message.story.option_outdated";

        public static final String STORY_NEXT = "message.story.next";
        public static final String STORY_NEXT_CHOICE = "message.story.next.choice";
        public static final String STORY_NEXT_HINT = "message.story.next.hint";
        public static final String STORY_NEXT_CHOICE_HINT = "message.story.next.choice.hint";
        public static final String STORY_RESUME = "message.story.resume";
        public static final String STORY_RESUME_HINT = "message.story.resume.hint";

        public static final String STORY_END = "message.story.end";
    }

    public static class Variables {
        public static final String LINE_PAUSE_TICKS = "line_pause_ticks";
    }

    public static class Values {
        public static final int DEFAULT_PAUSE_TICKS = 10;
    }

    public static class Tags {
        public static final String STORY = "story";
        public static final String STATE = "state";
        public static final String ENDED = "ended";
        public static final String VARIABLES = "variables";
        public static final String VARIABLE_ITEM_NAME = "name";
        public static final String VARIABLE_ITEM_VALUE = "value";
        public static final String FABRIC_TAG_NAME = "inkraft";
    }
}
