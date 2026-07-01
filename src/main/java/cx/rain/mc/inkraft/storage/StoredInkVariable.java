package cx.rain.mc.inkraft.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cx.rain.mc.inkraft.story.value.IStoryValue;
import cx.rain.mc.inkraft.story.value.SerializeValueType;

import java.util.List;

public record StoredInkVariable(String name, SerializeValueType type, IStoryValue<?, ?> value) {
    private static final Codec<SerializeValueType> TYPE_CODEC = SerializeValueType.CODEC;
    private static final Codec<IStoryValue<?, ?>> VALUE_CODEC = TYPE_CODEC.dispatch(IStoryValue::getSerializedType, SerializeValueType::getMapCodec);

    public static final Codec<StoredInkVariable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(StoredInkVariable::name),
        SerializeValueType.CODEC.fieldOf("type").forGetter(StoredInkVariable::type),
        VALUE_CODEC.fieldOf("value").forGetter(StoredInkVariable::value)
    ).apply(instance, StoredInkVariable::new));

    public static final Codec<List<StoredInkVariable>> LIST_CODEC = CODEC.listOf();

    public StoredInkVariable(String name, IStoryValue<?, ?> value) {
        this(name, value.getSerializedType(), value);
    }
}
