package xyz.xenondevs.invui.util;

import xyz.xenondevs.invui.Observer;

/**
 * A record combining the {@link Observer} and {@code how} value.
 *
 * @param observer The {@link Observer}
 * @param slot     The {@code how} value, i.e. what should be used to notify the {@link Observer} about updates.
 */
public record ObserverAtSlot(Observer observer, int slot) {
    
    public void notifyUpdate() {
        observer.notifyUpdate(slot);
    }
    
}
