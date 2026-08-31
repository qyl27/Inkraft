package cx.rain.mc.inkraft.utility.parser;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cx.rain.mc.inkraft.story.function.FunctionArgumentIllegalException;
import cx.rain.mc.inkraft.story.function.FunctionArgumentTypeException;
import cx.rain.mc.inkraft.story.value.IntStoryValue;
import cx.rain.mc.inkraft.story.value.StringStoryValue;
import net.minecraft.SharedConstants;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSetSupplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntitySelectorArgumentParserTest {
    private static final PermissionSetSupplier SELECTOR_SOURCE = () -> LevelBasedPermissionSet.OWNER;

    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void requiresEntitySelectorsWithFullConsumptionAndVanillaContracts() {
        assertNotNull(EntitySelectorArgumentParser.requireEntitySelector(
            new StringStoryValue("@s"), EntityArgument.entity(), SELECTOR_SOURCE));
        assertInvalidSelector("@e", EntityArgument.entity());

        assertNotNull(EntitySelectorArgumentParser.requireEntitySelector(
            new StringStoryValue("@e"), EntityArgument.entities(), SELECTOR_SOURCE));

        assertNotNull(EntitySelectorArgumentParser.requireEntitySelector(
            new StringStoryValue("@s"), EntityArgument.player(), SELECTOR_SOURCE));
        assertNotNull(EntitySelectorArgumentParser.requireEntitySelector(
            new StringStoryValue("@e[type=minecraft:player,limit=1]"),
            EntityArgument.player(), SELECTOR_SOURCE));
        assertInvalidSelector("@e[limit=1]", EntityArgument.player());

        assertNotNull(EntitySelectorArgumentParser.requireEntitySelector(
            new StringStoryValue("@a"), EntityArgument.players(), SELECTOR_SOURCE));
        assertInvalidSelector("@e", EntityArgument.players());

        assertInvalidSelector("@s trailing", EntityArgument.entity());
        var syntax = assertInvalidSelector("@e[", EntityArgument.entities());
        assertEquals("Expected a valid entity selector, got \"@e[\".", syntax.getMessage());
        assertThrows(FunctionArgumentTypeException.class, () -> EntitySelectorArgumentParser.requireEntitySelector(
            new IntStoryValue(1), EntityArgument.entities(), SELECTOR_SOURCE));
    }

    @Test
    void getsEntitySelectorsAsOptionalsForAllValidationFailures() {
        var fallback = EntitySelectorArgumentParser.requireEntitySelector(
            new StringStoryValue("@s"), EntityArgument.entity(), SELECTOR_SOURCE);
        assertTrue(EntitySelectorArgumentParser.getEntitySelector(
            new StringStoryValue("@s"), EntityArgument.entity(), SELECTOR_SOURCE).isPresent());
        assertEquals(Optional.empty(), EntitySelectorArgumentParser.getEntitySelector(
            new StringStoryValue("@e"), EntityArgument.entity(), SELECTOR_SOURCE));
        assertEquals(Optional.empty(), EntitySelectorArgumentParser.getEntitySelector(
            new StringStoryValue("@s trailing"), EntityArgument.entity(), SELECTOR_SOURCE));
        assertEquals(Optional.empty(), EntitySelectorArgumentParser.getEntitySelector(
            new StringStoryValue("@e["), EntityArgument.entities(), SELECTOR_SOURCE));
        assertEquals(Optional.empty(), EntitySelectorArgumentParser.getEntitySelector(
            new IntStoryValue(1), EntityArgument.entities(), SELECTOR_SOURCE));

        assertSame(fallback, EntitySelectorArgumentParser.getEntitySelector(
            new StringStoryValue("@e"), EntityArgument.entity(), SELECTOR_SOURCE).orElse(fallback));
        assertSame(fallback, EntitySelectorArgumentParser.getEntitySelector(
            new IntStoryValue(1), EntityArgument.entity(), SELECTOR_SOURCE).orElse(fallback));
    }

    @Test
    void rejectsNullValuesInsteadOfTreatingThemAsParseFailures() {
        assertThrows(NullPointerException.class, () -> EntitySelectorArgumentParser.getEntitySelector(
            null, EntityArgument.entity(), SELECTOR_SOURCE));
        assertThrows(NullPointerException.class, () -> EntitySelectorArgumentParser.requireEntitySelector(
            null, EntityArgument.entity(), SELECTOR_SOURCE));
    }

    private static FunctionArgumentIllegalException assertInvalidSelector(String selector,
                                                                          EntityArgument argument) {
        var exception = assertThrows(FunctionArgumentIllegalException.class,
            () -> EntitySelectorArgumentParser.requireEntitySelector(
                new StringStoryValue(selector), argument, SELECTOR_SOURCE));
        assertInstanceOf(CommandSyntaxException.class, exception.getCause());
        return exception;
    }
}
