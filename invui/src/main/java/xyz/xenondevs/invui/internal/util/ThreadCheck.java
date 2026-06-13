package xyz.xenondevs.invui.internal.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public final class ThreadCheck {
    
    private ThreadCheck() {}
    
    public static void checkOwnedBy(Entity entity) {
        if (Bukkit.isOwnedByCurrentRegion(entity))
            return;
        
        throw new IllegalStateException(
            "A method was called from an incorrect thread. On Paper the correct thread is the server thread. " +
            "On Folia, the correct thread is the thread that owns the viewer (use the viewer's entity scheduler). " +
            "Note that the absence of this exception does not imply correctness. In general and unless otherwise documented, " +
            "nothing in InvUI is thread-safe and all methods should be called from the corresponding viewer's thread."
        );
    }
    
}
