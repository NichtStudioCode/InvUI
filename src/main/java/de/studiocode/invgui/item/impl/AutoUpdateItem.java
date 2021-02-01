package de.studiocode.invgui.item.impl;

import de.studiocode.invgui.InvGui;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Supplier;

/**
 * An {@link Item} that updates it's {@link ItemBuilder} every specified amount
 * of ticks.
 */
public class AutoUpdateItem extends SupplierItem {
    
    private final BukkitTask task;
    
    public AutoUpdateItem(int period, Supplier<ItemBuilder> builderSupplier) {
        super(builderSupplier);
        task = Bukkit.getScheduler().runTaskTimer(InvGui.getInstance().getPlugin(), this::notifyWindows, 0, period);
    }
    
    public void cancel() {
        task.cancel();
    }
    
}
