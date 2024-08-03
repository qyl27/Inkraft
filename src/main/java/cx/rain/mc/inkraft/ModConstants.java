package cx.rain.mc.inkraft;

public class ModConstants {
    public static final String MESSAGE_COMMAND_NOT_PLAYER = "message.command.not_a_player";
    public static final String MESSAGE_COMMAND_SUCCESS = "message.command.success";

    public static final String MESSAGE_STORY_CONTINUE = "message.story.continue";
    public static final String MESSAGE_STORY_CONTINUE_CHOICE = "message.story.continue.choice";
    public static final String MESSAGE_STORY_HINT_CONTINUE = "message.story.hint.continue";
    public static final String MESSAGE_STORY_HINT_CONTINUE_CHOICE = "message.story.hint.continue.choice";
    public static final String MESSAGE_STORY_BAD_TOKEN = "message.story.bad_token";

    public static final String HUD_INFO = "hud.info";
    public static final String MESSAGE_STORY_LOGGED_IN_CONTINUE = "message.story.logged_in_continue";

    public static class Variables {
        public static final String LINE_PAUSE_TICKS = "line_pause_ticks";
    }

    public static class Values {
        public static final int DEFAULT_PAUSE_TICKS = 30;
    }

    public static class Tags {
        public static final String STORY = "story";
        public static final String STATE = "state";
        public static final String VARIABLES = "variables";
        public static final String VARIABLE_ITEM_NAME = "name";
        public static final String VARIABLE_ITEM_VALUE = "value";
        public static final String FABRIC_TAG_NAME = "inkraft";
    }
}
