package xyz.xenondevs.invui.internal.util;

import org.junit.jupiter.api.Test;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeakIdentityToIntMapTest {
    
    @Test
    public void testPutAndGet() {
        var map = new WeakIdentityToIntMap<>();
        var key = new Object();
        map.putAssertAbsent(key, 42);
        assertEquals(OptionalInt.of(42), map.get(key));
    }
    
    @Test
    public void testGetMissing() {
        var map = new WeakIdentityToIntMap<>();
        assertEquals(OptionalInt.empty(), map.get(new Object()));
    }
    
    @Test
    public void testIdentitySemantics() {
        var map = new WeakIdentityToIntMap<Key>();
        var key1 = new Key(1);
        var key2 = new Key(1);
        map.putAssertAbsent(key1, 1);
        map.putAssertAbsent(key2, 2);
        assertEquals(OptionalInt.of(1), map.get(key1));
        assertEquals(OptionalInt.of(2), map.get(key2));
        assertEquals(OptionalInt.empty(), map.get(new Key(1)));
    }
    
    private record Key(int value) {}
    
}
