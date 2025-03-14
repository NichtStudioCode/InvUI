package xyz.xenondevs.invui.internal;

import xyz.xenondevs.invui.window.AbstractWindow;

public record ViewerAtSlot(AbstractWindow<?> window, int slot) {
    
    public void notifyUpdate() {
        window.notifyUpdate(slot);
    }
    
}
