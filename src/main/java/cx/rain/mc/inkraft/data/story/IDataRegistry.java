package cx.rain.mc.inkraft.data.story;

import java.util.Set;

public interface IDataRegistry<K, V> {
    void add(K key, V value);

    void clear();

    V get(K key);

    boolean has(K key);

    Set<K> getAll();
}
