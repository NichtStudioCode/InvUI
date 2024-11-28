package xyz.xenondevs.invui.item.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.window.AbstractWindow;

/**
 * An {@link Item} that automatically cycles through a predefined array of
 * {@link ItemProvider} at a predefined speed.
 */
public class AutoCycleItem extends AbstractItem {
    
    private final ItemProvider[] itemProviders;
    private final int period;
    private BukkitTask task;
    
    private int state;
    
    public AutoCycleItem(int period, ItemProvider... itemProviders) {
        this.itemProviders = itemProviders;
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
        if (state == itemProviders.length) state = 0;
        notifyWindows();
    }
    
    @Override
    public ItemProvider getItemProvider() {
        return itemProviders[state];
    }
    
    @Override
    public void addWindow(AbstractWindow window) {
        super.addWindow(window);
        if (task == null) start();
    }
    
    @Override
    public void removeWindow(AbstractWindow window) {
        super.removeWindow(window);
        if (getWindows().isEmpty() && task != null) cancel();
    }
    
    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        // empty
    }
    
}
