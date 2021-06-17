package de.studiocode.invui.item.impl;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemBuilder;
import de.studiocode.invui.window.Window;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 * An {@link Item} that automatically cycles through a predefined array of
 * {@link ItemBuilder} at a predefined speed.
 */
public class AutoCycleItem extends BaseItem {
    
    private final ItemBuilder[] itemBuilders;
    private final int period;
    private BukkitTask task;
    
    private int state;
    
    public AutoCycleItem(int period, ItemBuilder... itemBuilders) {
        this.itemBuilders = itemBuilders;
        this.period = period;
    }
    
    public void start() {
        if (task != null) task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::cycle, 0, period);
    }
    
    public void cancel() {
        task.cancel();
        task = null;
    }
    
    private void cycle() {
        state++;
        if (state == itemBuilders.length) state = 0;
        notifyWindows();
    }
    
    @Override
    public ItemBuilder getItemBuilder() {
        return itemBuilders[state];
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
    
    @Override
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // empty
    }
    
}
