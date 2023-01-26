package de.studiocode.invui.item.impl;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemProvider;
import de.studiocode.invui.window.Window;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Supplier;

/**
 * An {@link Item} that updates its {@link ItemProvider} every specified amount
 * of ticks.
 */
public class AutoUpdateItem extends SuppliedItem {
    
    private final int period;
    private BukkitTask task;
    
    public AutoUpdateItem(int period, Supplier<? extends ItemProvider> builderSupplier) {
        super(builderSupplier, null);
        this.period = period;
    }
    
    public void start() {
        if (task != null) task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::notifyWindows, 0, period);
    }
    
    public void cancel() {
        task.cancel();
        task = null;
    }
    
    @Override
    public void addWindow(Window window) {
        super.addWindow(window);
        if (task == null) start();
    }
    
    @Override
    public void removeWindow(Window window) {
        super.removeWindow(window);
        if (getWindows().isEmpty() && task != null) cancel();
    }
    
}
