package xyz.xenondevs.invui.item;

import xyz.xenondevs.invui.Observer;
import xyz.xenondevs.invui.util.ObserverAtSlot;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An abstract implementation of {@link Item}.
 */
public abstract class AbstractItem implements Item {
    
    protected final Set<ObserverAtSlot> observers = ConcurrentHashMap.newKeySet();
    
    @Override
    public void addObserver(Observer who, int what, int how) {
        observers.add(new ObserverAtSlot(who, how));
    }
    
    @Override
    public void removeObserver(Observer who, int what, int how) {
        observers.remove(new ObserverAtSlot(who, how));
    }
    
    @Override
    public void notifyWindows() {
        for (var viewer : observers) {
            viewer.notifyUpdate();
        }
    }
    
}
