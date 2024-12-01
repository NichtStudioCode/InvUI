package xyz.xenondevs.invui.internal;

public record ViewerAtSlot<V extends Viewer>(V viewer, int slot) {
    
    public void notifyUpdate() {
        viewer.notifyUpdate(slot);
    }
    
}
