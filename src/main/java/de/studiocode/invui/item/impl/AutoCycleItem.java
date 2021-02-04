package de.studiocode.invui.item.impl;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.itembuilder.ItemBuilder;
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
    private final BukkitTask task;
    
    private int state;
    
    public AutoCycleItem(int period, ItemBuilder... itemBuilders) {
        this.itemBuilders = itemBuilders;
        task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::cycle, 0, period);
    }
    
    public void cancel() {
        task.cancel();
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
    public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        // empty
    }
    
}
