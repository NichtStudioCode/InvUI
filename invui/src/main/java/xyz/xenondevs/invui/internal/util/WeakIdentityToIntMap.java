package xyz.xenondevs.invui.internal.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jspecify.annotations.Nullable;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public final class WeakIdentityToIntMap<K> {
    
    private final Int2ObjectMap<@Nullable List<Entry<K>>> buckets = new Int2ObjectOpenHashMap<>();
    private final ReferenceQueue<K> queue = new ReferenceQueue<>();
    
    public OptionalInt get(K key) {
        expunge();
        int hash = System.identityHashCode(key);
        List<Entry<K>> bucket = buckets.get(hash);
        if (bucket == null)
            return OptionalInt.empty();
        
        for (Entry<K> e : bucket) {
            if (e.get() == key)
                return OptionalInt.of(e.value);
        }
        
        return OptionalInt.empty();
    }
    
    public void putAssertAbsent(K key, int value) {
        expunge();
        int hash = System.identityHashCode(key);
        List<Entry<K>> bucket = buckets.computeIfAbsent(hash, _ -> new ArrayList<>(2));
        assert bucket.stream().noneMatch(e -> e.get() == key) : "Key already present in map";
        bucket.add(new Entry<>(key, value, hash, queue));
    }
    
    private void expunge() {
        Entry<?> e;
        while ((e = (Entry<?>) queue.poll()) != null) {
            List<Entry<K>> bucket = buckets.get(e.keyIdentityHash);
            if (bucket != null) {
                bucket.remove(e);
                if (bucket.isEmpty())
                    buckets.remove(e.keyIdentityHash);
            }
        }
    }
    
    private static class Entry<K> extends WeakReference<K> {
        
        final int value;
        final int keyIdentityHash;
        
        Entry(K key, int value, int keyIdentityHash, ReferenceQueue<K> queue) {
            super(key, queue);
            this.value = value;
            this.keyIdentityHash = keyIdentityHash;
        }
        
    }
    
}