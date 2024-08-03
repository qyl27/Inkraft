package cx.rain.mc.inkraft.data.story;

import java.util.Set;

public interface IDataRegistry<K, V> {
    void add(K resourceLocation, V json);

    void clear();

    V get(K path);

    Set<K> getAll();
}
