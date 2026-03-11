package xyz.xenondevs.invui.internal.menu;

public enum UpdateType {
    
    NONE,
    DIRTY,
    FULL;
    
    public UpdateType or(UpdateType other) {
        return values()[Math.max(ordinal(), other.ordinal())];
    }
    
}
