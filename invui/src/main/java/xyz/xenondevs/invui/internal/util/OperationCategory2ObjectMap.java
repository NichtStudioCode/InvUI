package xyz.xenondevs.invui.internal.util;

import org.jspecify.annotations.NullUnmarked;
import xyz.xenondevs.invui.inventory.OperationCategory;

@NullUnmarked
public class OperationCategory2ObjectMap<V> {
    
    private V add;
    private V collect;
    private V other;
    
    public OperationCategory2ObjectMap(V def) {
        this.add = def;
        this.collect = def;
        this.other = def;
    }
    
    public V get(OperationCategory category) {
        return switch (category) {
            case ADD -> add;
            case COLLECT -> collect;
            case OTHER -> other;
        };
    }
    
    public void put(OperationCategory category, V value) {
        switch (category) {
            case ADD -> add = value;
            case COLLECT -> collect = value;
            case OTHER -> other = value;
        }
    }
    
}
