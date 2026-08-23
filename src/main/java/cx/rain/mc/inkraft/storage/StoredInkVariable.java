package cx.rain.mc.inkraft.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cx.rain.mc.inkraft.ModConstants;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.SerializeValueType;

import java.util.List;

public record StoredInkVariable(String name, SerializeValueType type, IStoryValue<?, ?> value) {
    private static final Codec<SerializeValueType> TYPE_CODEC = SerializeValueType.CODEC;
    private static final Codec<IStoryValue<?, ?>> VALUE_CODEC = TYPE_CODEC.dispatch(IStoryValue::getSerializedType, type -> type.getCodec().fieldOf("value"));

    public static final Codec<StoredInkVariable> TYPED_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf(ModConstants.Tags.VARIABLE_ITEM_NAME).forGetter(StoredInkVariable::name),
        SerializeValueType.CODEC.fieldOf(ModConstants.Tags.VARIABLE_ITEM_TYPE).forGetter(StoredInkVariable::type),
        VALUE_CODEC.fieldOf(ModConstants.Tags.VARIABLE_ITEM_VALUE).forGetter(StoredInkVariable::value)
    ).apply(instance, StoredInkVariable::new));

    private static final Codec<StoredInkVariable> LEGACY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf(ModConstants.Tags.VARIABLE_ITEM_NAME).forGetter(StoredInkVariable::name),
        Codec.STRING.fieldOf(ModConstants.Tags.VARIABLE_ITEM_VALUE).forGetter(value -> value.value().getString())
    ).apply(instance,
        (name, value) -> new StoredInkVariable(name, IStoryValue.fromString(value))));

    public static final Codec<StoredInkVariable> CODEC = Codec.withAlternative(TYPED_CODEC, LEGACY_CODEC);

    public static final Codec<List<StoredInkVariable>> LIST_CODEC = CODEC.listOf();

    public StoredInkVariable(String name, IStoryValue<?, ?> value) {
        this(name, value.getSerializedType(), value);
    }
}
