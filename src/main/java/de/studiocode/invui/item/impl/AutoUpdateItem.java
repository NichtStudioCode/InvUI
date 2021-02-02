package de.studiocode.invui.item.impl;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.itembuilder.ItemBuilder;
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
        task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::notifyWindows, 0, period);
    }
    
    public void cancel() {
        task.cancel();
    }
    
}
