package de.studiocode.invui.item.impl;

import de.studiocode.invui.item.Item;
import de.studiocode.invui.window.AbstractWindow;
import de.studiocode.invui.window.Window;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The base for all {@link Item}s.
 */
public abstract class BaseItem implements Item {
    
    private final Set<AbstractWindow> windows = new HashSet<>();
    
    @Override
    public void addWindow(Window window) {
        if (!(window instanceof AbstractWindow))
            throw new IllegalArgumentException("Illegal window implementation");
        
        windows.add((AbstractWindow) window);
    }
    
    @Override
    public void removeWindow(Window window) {
        if (!(window instanceof AbstractWindow))
            throw new IllegalArgumentException("Illegal window implementation");
        
        windows.remove(window);
    }
    
    @Override
    public Set<Window> getWindows() {
        return Collections.unmodifiableSet(windows);
    }
    
    @Override
    public void notifyWindows() {
        windows.forEach(w -> w.handleItemProviderUpdate(this));
    }
    
}
