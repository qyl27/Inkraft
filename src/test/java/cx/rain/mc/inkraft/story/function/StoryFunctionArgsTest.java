package cx.rain.mc.inkraft.story.function;

import cx.rain.mc.inkraft.story.function.game.RealTimeFunction;
import cx.rain.mc.inkraft.story.function.game.WorldTimeFunction;
import cx.rain.mc.inkraft.story.function.game.command.RunCommandFunction;
import cx.rain.mc.inkraft.story.function.game.command.ScoreboardFunction;
import cx.rain.mc.inkraft.story.function.game.command.ScoreboardValuedFunction;
import cx.rain.mc.inkraft.story.function.game.command.storage.GetStorageFunction;
import cx.rain.mc.inkraft.story.function.game.command.storage.SetStorageFunction;
import cx.rain.mc.inkraft.story.function.game.inventory.CountItemFunction;
import cx.rain.mc.inkraft.story.function.game.inventory.GiveItemFunction;
import cx.rain.mc.inkraft.story.function.game.inventory.HasItemFunction;
import cx.rain.mc.inkraft.story.function.game.inventory.TakeItemFunction;
import cx.rain.mc.inkraft.story.function.game.player.GetPlayerNameFunction;
import cx.rain.mc.inkraft.story.function.game.player.PlayerStatFunctions;
import cx.rain.mc.inkraft.story.function.lang.UuidFunctions;
import cx.rain.mc.inkraft.story.function.system.IsDebugFunction;
import cx.rain.mc.inkraft.story.function.system.LogFunction;
import cx.rain.mc.inkraft.story.function.system.flow.FlowToFunction;
import cx.rain.mc.inkraft.story.function.system.flow.HasFlowFunction;
import cx.rain.mc.inkraft.story.function.system.flow.IsFlowEndedFunction;
import cx.rain.mc.inkraft.story.function.system.flow.IsInFlowFunction;
import cx.rain.mc.inkraft.story.function.system.flow.NewFlowFunction;
import cx.rain.mc.inkraft.story.function.system.flow.RemoveFlowFunction;
import cx.rain.mc.inkraft.story.function.system.line.PauseFunction;
import cx.rain.mc.inkraft.story.function.system.line.SetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.line.UnsetLineTicksFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ParseBoolFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ParseFloatFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ParseIntFunction;
import cx.rain.mc.inkraft.story.function.system.parse.ToStringFunction;
import cx.rain.mc.inkraft.story.function.system.variable.ClearVariableFunction;
import cx.rain.mc.inkraft.story.function.system.variable.GetVariableFunction;
import cx.rain.mc.inkraft.story.function.system.variable.HasVariableFunction;
import cx.rain.mc.inkraft.story.function.system.variable.SetVariableFunction;
import cx.rain.mc.inkraft.story.function.system.variable.UnsetVariableFunction;
import cx.rain.mc.inkraft.story.value.ArrayStoryValue;
import cx.rain.mc.inkraft.story.value.BoolStoryValue;
import cx.rain.mc.inkraft.story.value.FloatStoryValue;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class StoryFunctionArgsTest {
    private static final StringStoryValue STRING = new StringStoryValue("value");
    private static final IntStoryValue INT = new IntStoryValue(1);

    @TestFactory
    Stream<DynamicTest> rejectsWrongArgumentCounts() {
        return contracts().flatMap(contract -> {
            var args = contract.validArgs();
            var tooMany = Arrays.copyOf(args, args.length + 1);
            tooMany[args.length] = STRING;
            var extra = dynamicTest(contract.function().getName() + " rejects an extra argument",
                () -> assertThrows(FunctionArgumentCountException.class,
                    () -> contract.function().apply(null, tooMany)));

            if (args.length == 0) {
                return Stream.of(extra);
            }

            var tooFew = Arrays.copyOf(args, args.length - 1);
            var missing = dynamicTest(contract.function().getName() + " rejects a missing argument",
                () -> assertThrows(FunctionArgumentCountException.class,
                    () -> contract.function().apply(null, tooFew)));
            return Stream.of(missing, extra);
        });
    }

    @TestFactory
    Stream<DynamicTest> rejectsWrongArgumentTypes() {
        return contracts().flatMap(StoryFunctionArgsTest::typedTests);
    }

    @Test
    void toStringAcceptsArbitraryValueArguments() {
        var toString = new ToStringFunction();
        assertEquals("true", toString.apply(null, BoolStoryValue.TRUE).getValue());
        assertEquals("12", toString.apply(null, new IntStoryValue(12)).getValue());
        assertEquals("2.5", toString.apply(null, new FloatStoryValue(2.5F)).getValue());
        assertEquals("value", toString.apply(null, STRING).getValue());
    }

    @Test
    void numericParseFunctionsUseValueTextAndFallbackToZero() {
        var parseBool = new ParseBoolFunction();
        assertEquals(true, parseBool.apply(null, BoolStoryValue.TRUE).getValue());
        assertEquals(false, parseBool.apply(null, new IntStoryValue(0)).getValue());
        assertEquals(true, parseBool.apply(null, new IntStoryValue(1)).getValue());

        var parseInt = new ParseIntFunction();
        assertEquals(12, parseInt.apply(null, new IntStoryValue(12)).getValue());
        assertEquals(12, parseInt.apply(null, new StringStoryValue("12")).getValue());
        assertEquals(0, parseInt.apply(null, BoolStoryValue.TRUE).getValue());
        assertEquals(0, parseInt.apply(null, new StringStoryValue("invalid")).getValue());
        assertEquals(0, parseInt.apply(null, new StringStoryValue("")).getValue());
        assertEquals(0, parseInt.apply(null, ArrayStoryValue.empty()).getValue());

        var parseFloat = new ParseFloatFunction();
        assertEquals(12.0F, parseFloat.apply(null, new IntStoryValue(12)).getValue());
        assertEquals(2.5F, parseFloat.apply(null, new StringStoryValue("2.5")).getValue());
        assertEquals(0.0F, parseFloat.apply(null, BoolStoryValue.TRUE).getValue());
        assertEquals(0.0F, parseFloat.apply(null, new StringStoryValue("invalid")).getValue());
        assertEquals(0.0F, parseFloat.apply(null, new StringStoryValue("")).getValue());
        assertEquals(0.0F, parseFloat.apply(null, ArrayStoryValue.empty()).getValue());
    }

    private static Stream<DynamicTest> typedTests(Contract contract) {
        return IntStream.of(contract.typedArgs()).mapToObj(index -> {
            var replacement = contract.validArgs()[index] instanceof StringStoryValue ? INT : STRING;
            return dynamicTest(contract.function().getName() + " rejects argument " + index
                + " with " + replacement.getValueType().getSimpleName(), () -> {
                var args = contract.validArgs().clone();
                args[index] = replacement;
                assertThrows(FunctionArgumentTypeException.class,
                    () -> contract.function().apply(null, args));
            });
        });
    }

    private static Stream<Contract> contracts() {
        return Stream.of(
            contract(new IsDebugFunction()),
            strings(new IsInFlowFunction(), 1, 0),
            strings(new FlowToFunction(), 1, 0),
            strings(new NewFlowFunction(), 2, 0, 1),
            strings(new RemoveFlowFunction(), 1, 0),
            strings(new HasFlowFunction(), 1, 0),
            strings(new IsFlowEndedFunction(), 1, 0),
            contract(new PauseFunction()),
            new Contract(new SetLineTicksFunction(), args(INT), indexes(0)),
            contract(new UnsetLineTicksFunction()),
            strings(new HasVariableFunction(), 1, 0),
            strings(new GetVariableFunction(), 1, 0),
            new Contract(new SetVariableFunction(), args(STRING, BoolStoryValue.TRUE), indexes(0)),
            strings(new UnsetVariableFunction(), 1, 0),
            contract(new ClearVariableFunction()),
            strings(new LogFunction("log", (_, _) -> {
            }), 1, 0),
            new Contract(new ParseBoolFunction(), args(STRING), indexes()),
            new Contract(new ParseIntFunction(), args(STRING), indexes()),
            new Contract(new ParseFloatFunction(), args(STRING), indexes()),
            new Contract(new ToStringFunction(), args(BoolStoryValue.TRUE), indexes()),
            contract(UuidFunctions.randomUuid()),
            strings(UuidFunctions.isUuid(), 1, 0),
            contract(new GetPlayerNameFunction()),
            strings(PlayerStatFunctions.value(), 2, 0, 1),
            strings(PlayerStatFunctions.formatted(), 2, 0, 1),
            strings(new WorldTimeFunction("worldTime", _ -> 0), 1, 0),
            strings(new RealTimeFunction(), 1, 0),
            strings(new RunCommandFunction("runCommand", _ -> null), 1, 0),
            strings(new ScoreboardFunction("getScoreboard", _ -> 0), 1, 0),
            new Contract(new ScoreboardValuedFunction("valuedScoreboard", (_, _) -> {
            }), args(STRING, INT), indexes(0, 1)),
            strings(new GetStorageFunction(), 2, 0, 1),
            strings(new SetStorageFunction(), 3, 0, 1, 2),
            inventory(new HasItemFunction()),
            new Contract(new CountItemFunction(), args(STRING, STRING, STRING), indexes(0, 1, 2)),
            inventory(new GiveItemFunction()),
            inventory(new TakeItemFunction())
        );
    }

    private static Contract contract(IStoryFunction function) {
        return new Contract(function, args(), indexes());
    }

    private static Contract strings(IStoryFunction function, int count, int... typedArgs) {
        var args = new IStoryValue<?, ?>[count];
        Arrays.fill(args, STRING);
        return new Contract(function, args, typedArgs);
    }

    private static Contract inventory(IStoryFunction function) {
        return new Contract(function, args(STRING, INT, STRING, STRING), indexes(0, 2, 3));
    }

    private static IStoryValue<?, ?>[] args(IStoryValue<?, ?>... values) {
        return values;
    }

    private static int[] indexes(int... values) {
        return values;
    }

    private record Contract(IStoryFunction function, IStoryValue<?, ?>[] validArgs, int[] typedArgs) {
    }
}
