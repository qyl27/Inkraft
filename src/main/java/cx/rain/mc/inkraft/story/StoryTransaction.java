package cx.rain.mc.inkraft.story;

import net.minecraft.resources.Identifier;

import java.util.Objects;

sealed interface StoryTransaction {
    enum Pause implements StoryTransaction {
        INSTANCE
    }

    sealed interface Flow extends StoryTransaction {
    }

    record NewFlow(String name, String knot) implements Flow {
        public NewFlow {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(knot, "knot");
        }
    }

    record FlowTo(String name) implements Flow {
        public FlowTo {
            Objects.requireNonNull(name, "name");
        }
    }

    record RemoveFlow(String name) implements Flow {
        public RemoveFlow {
            Objects.requireNonNull(name, "name");
        }
    }

    sealed interface Lifecycle extends StoryTransaction {
    }

    record Replace(Identifier storyId, StoryRuntime runtime) implements Lifecycle {
        public Replace {
            Objects.requireNonNull(storyId, "storyId");
            Objects.requireNonNull(runtime, "runtime");
        }
    }

    enum Reset implements Lifecycle {
        INSTANCE
    }
}
