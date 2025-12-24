package xyz.xenondevs.invui.item;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.Observer;
import xyz.xenondevs.invui.util.ObserverAtSlot;

import java.util.*;

sealed interface ObserverHolder {
    
    void addObserver(Observer who, int how);
    
    void removeObserver(Observer who, int how);
    
    void notifyWindows();
    
    final class NonTicking implements ObserverHolder {
        
        private final Set<ObserverAtSlot> observers = new HashSet<>();
        
        @Override
        public void addObserver(Observer who, int how) {
            synchronized (observers) {
                observers.add(new ObserverAtSlot(who, how));
            }
        }
        
        @Override
        public void removeObserver(Observer who, int how) {
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
    
    final class Ticking implements ObserverHolder {
        
        private final int updatePeriod;
        private final Map<Observer, Pair<IntSet, @Nullable ScheduledTask>> observers = Collections.synchronizedMap(new HashMap<>());
        
        public Ticking(int updatePeriod) {
            this.updatePeriod = updatePeriod;
        }
        
        @Override
        public void addObserver(Observer who, int how) {
            observers.compute(who, (x, pair) -> {
                IntSet slots;
                ScheduledTask task;
                if (pair != null) {
                    slots = pair.first();
                    task = pair.second();
                } else {
                    slots = new IntOpenHashSet();
                    if (updatePeriod > 0) {
                        task = who.getScheduler().runAtFixedRate(
                            InvUI.getInstance().getPlugin(),
                            x1 -> notify(who),
                            null,
                            1,
                            updatePeriod
                        );
                    } else {
                        task = null;
                    }
                }
                slots.add(how);
                return Pair.of(slots, task);
            });
        }
        
        @Override
        public void removeObserver(Observer who, int how) {
            observers.compute(who, (x, entry) -> {
                if (entry == null)
                    return null;
                
                IntSet slots = entry.first();
                slots.remove(how);
                
                if (slots.isEmpty()) {
                    ScheduledTask task = entry.second();
                    if (task != null)
                        task.cancel();
                    return null;
                }
                
                return entry;
            });
        }
        
        @Override
        public void notifyWindows() {
            synchronized (observers) {
                observers.forEach((viewer, pair) -> pair.first().forEach(viewer::notifyUpdate));
            }
        }
        
        private void notify(Observer observer) {
            synchronized (observers) {
                var pair = observers.get(observer);
                if (pair != null)
                    pair.first().forEach(observer::notifyUpdate);
            }
        }
        
    }
    
}
