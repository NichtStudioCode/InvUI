package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import xyz.xenondevs.invui.internal.ViewerAtSlot;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract implementation of {@link Item}.
 */
public non-sealed abstract class AbstractItem implements Item {
    
    /**
     * @hidden
     */
    @ApiStatus.Internal
    protected final Set<ViewerAtSlot> viewers = new HashSet<>();
    
    /**
     * @hidden
     */
    @ApiStatus.Internal
    public void addViewer(AbstractWindow<?> who, int how) {
        synchronized (viewers) {
            viewers.add(new ViewerAtSlot(who, how));
        }
    }
    
    /**
     * @hidden
     */
    @ApiStatus.Internal
    public void removeViewer(AbstractWindow<?> who, int how) {
        synchronized (viewers) {
            viewers.remove(new ViewerAtSlot(who, how));
        }
    }
    
    @Override
    public void handleBundleSelect(Player player, int bundleSlot) {
        // empty
    }
    
    @Override
    public void notifyWindows() {
        synchronized (viewers) {
            for (var viewer : viewers) {
                viewer.notifyUpdate();
            }
        }
    }
    
}
