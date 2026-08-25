package cx.rain.mc.inkraft.story.value;

import com.bladecoder.ink.runtime.InkList;
import com.bladecoder.ink.runtime.InkListItem;
import cx.rain.mc.inkraft.storage.StoredInkVariable;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IStoryValueTest {
    @Test
    void convertsBladeRuntimeValuesWithoutLosingTypes() {
        assertEquals(new IntStoryValue(1), IStoryValue.fromObject(1));
        assertEquals(new FloatStoryValue(1.0F), IStoryValue.fromObject(1.0F));
        assertSame(BoolStoryValue.TRUE, IStoryValue.fromObject(true));
        assertEquals(new StringStoryValue("1"), IStoryValue.fromObject("1"));
    }

    @Test
    void exposesTypedPrimitiveObjectAndStringForms() {
        IStoryValue<Integer, Integer> value = new IntStoryValue(1);

        assertEquals(1, value.getValue());
        assertEquals(1, value.toPrimitive());
        assertEquals(1, value.asObject());
        assertEquals("1", value.getString());
    }

    @Test
    void rejectsValuesOutsideTheInkRuntimeBoundary() {
        assertThrows(IllegalArgumentException.class, () -> IStoryValue.fromObject(1.0D));
        assertThrows(IllegalArgumentException.class, () -> IStoryValue.fromObject(null));
    }

    @Test
    void codecRoundTripsTypedValuesThroughNbt() {
        var values = List.<IStoryValue<?, ?>>of(
            BoolStoryValue.TRUE,
            new IntStoryValue(12),
            new FloatStoryValue(2.5F),
            new StringStoryValue("true"),
            new StringStoryValue("1"),
            new StringStoryValue("1.0"),
            new StringStoryValue("[1]")
        );

        for (var value : values) {
            var tag = assertInstanceOf(CompoundTag.class,
                StoredInkVariable.TYPED_CODEC.encodeStart(
                    NbtOps.INSTANCE, new StoredInkVariable("test", value)).getOrThrow());
            var restored = StoredInkVariable.TYPED_CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
            assertEquals(value, restored.value());
        }
    }

    @Test
    void codecUsesExplicitTypesAndNativeNbtValues() {
        assertEncodedType(BoolStoryValue.TRUE, "bool", ByteTag.class);
        assertEncodedType(new IntStoryValue(12), "int", IntTag.class);
        assertEncodedType(new FloatStoryValue(2.5F), "float", FloatTag.class);
        assertEncodedType(new StringStoryValue("12"), "string", StringTag.class);
    }

    @Test
    void readsLegacyStringValuesWithExistingInference() {
        assertSame(BoolStoryValue.TRUE, IStoryValue.fromString("true"));
        assertEquals(new IntStoryValue(1), IStoryValue.fromString("1"));
        assertEquals(new FloatStoryValue(1.0F), IStoryValue.fromString("1.0"));
        assertEquals(new StringStoryValue("value"), IStoryValue.fromString("value"));
    }

    @Test
    void codecWritesInkListsAsStrings() {
        var list = new InkList();
        list.put(new InkListItem("mood", "happy"), 1);
        list.put(new InkListItem("mood", "sad"), 2);

        var tag = assertInstanceOf(CompoundTag.class,
            StoredInkVariable.TYPED_CODEC.encodeStart(
                NbtOps.INSTANCE, new StoredInkVariable("test", new InkListStoryValue(list))).getOrThrow());
        var encodedValue = tag.getCompoundOrEmpty("value");
        assertEquals("string", encodedValue.getStringOr("type", ""));
        assertEquals("happy, sad", encodedValue.getStringOr("value", ""));
        assertEquals(new StringStoryValue("happy, sad"),
            StoredInkVariable.TYPED_CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow().value());
    }

    private static void assertEncodedType(IStoryValue<?, ?> value, String type, Class<?> tagType) {
        var tag = assertInstanceOf(CompoundTag.class,
            StoredInkVariable.TYPED_CODEC.encodeStart(
                NbtOps.INSTANCE, new StoredInkVariable("test", value)).getOrThrow());
        assertEquals(type, tag.getStringOr("type", ""));
        var encodedValue = tag.getCompoundOrEmpty("value");
        assertEquals(type, encodedValue.getStringOr("type", ""));
        assertInstanceOf(tagType, encodedValue.get("value"));
    }
}
