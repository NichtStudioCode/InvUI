package xyz.xenondevs.invui.item;

import xyz.xenondevs.invui.Observer;
import xyz.xenondevs.invui.util.ObserverAtSlot;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract implementation of {@link Item}.
 */
public abstract class AbstractItem implements Item {
    
    protected final Set<ObserverAtSlot> observers = new HashSet<>();
    
    @Override
    public void addObserver(Observer who, int what, int how) {
        synchronized (observers) {
            observers.add(new ObserverAtSlot(who, how));
        }
    }
    
    @Override
    public void removeObserver(Observer who, int what, int how) {
        synchronized (observers) {
            observers.remove(new ObserverAtSlot(who, how));
        }
    }
    
    @Override
    public void notifyWindows() {
        synchronized (observers) {
            for (var viewer : observers) {
                viewer.notifyUpdate();
            }
        }
    }
    
}
