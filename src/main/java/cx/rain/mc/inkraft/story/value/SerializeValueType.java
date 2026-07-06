package cx.rain.mc.inkraft.story.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import lombok.Getter;

public enum SerializeValueType {
    BOOL("bool", BoolStoryValue.CODEC),
    INT("int", IntStoryValue.CODEC),
    FLOAT("float", FloatStoryValue.CODEC),
    STRING("string", StringStoryValue.CODEC),
    ;

    public static final Codec<SerializeValueType> CODEC = Codec.STRING.xmap(SerializeValueType::from, SerializeValueType::getName);

    @Getter
    private final String name;

    @Getter
    private final Codec<? extends IStoryValue<?, ?>> codec;

    SerializeValueType(String name, Codec<? extends IStoryValue<?, ?>> codec) {
        this.name = name;
        this.codec = codec;
    }

    public static SerializeValueType from(String name) {
        return switch (name) {
            case "bool" -> BOOL;
            case "int" -> INT;
            case "float" -> FLOAT;
            default -> STRING;
        };
    }
}
