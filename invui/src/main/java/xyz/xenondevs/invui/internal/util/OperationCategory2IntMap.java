package xyz.xenondevs.invui.internal.util;

import xyz.xenondevs.invui.inventory.OperationCategory;

public class OperationCategory2IntMap {
    
    private int add;
    private int collect;
    private int other;
    
    public OperationCategory2IntMap(int def) {
        this.add = def;
        this.collect = def;
        this.other = def;
    }
    
    public int get(OperationCategory category) {
        return switch (category) {
            case ADD -> add;
            case COLLECT -> collect;
            case OTHER -> other;
        };
    }
    
    public void put(OperationCategory category, int value) {
        switch (category) {
            case ADD -> add = value;
            case COLLECT -> collect = value;
            case OTHER -> other = value;
        }
    }
    
}
