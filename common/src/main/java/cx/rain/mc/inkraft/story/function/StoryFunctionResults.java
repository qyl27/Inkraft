package cx.rain.mc.inkraft.story.function;

public class StoryFunctionResults {
    public interface IStoryFunctionResult {
    }

    public record StringResult(String stringResult) implements IStoryFunctionResult {
    }

    public record IntResult(int intResult) implements IStoryFunctionResult {
    }

    public record BoolResult(boolean boolResult) implements IStoryFunctionResult {
    }
}
