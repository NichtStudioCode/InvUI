package xyz.xenondevs.invui.item;

import org.jetbrains.annotations.ApiStatus;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An abstract implementation of the {@link Item} interface.
 */
public non-sealed abstract class AbstractItem implements Item {
    
    private final Set<Window> windows = new HashSet<>();
    
    /**
     * @hidden
     */
    @ApiStatus.Internal
    public void addWindow(Window window) {
        windows.add(window);
    }
    
    /**
     * @hidden
     */
    @ApiStatus.Internal
    public void removeWindow(Window window) {
        windows.remove(window);
    }
    
    /**
     * @hidden
     */
    @ApiStatus.Internal
    public Set<Window> getWindows() {
        return Collections.unmodifiableSet(windows);
    }
    
    @Override
    public void notifyWindows() {
        windows.forEach(w -> ((AbstractWindow)w).handleItemProviderUpdate(this));
    }
    
}
