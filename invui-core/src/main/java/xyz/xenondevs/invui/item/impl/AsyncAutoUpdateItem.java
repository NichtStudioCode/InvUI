package xyz.xenondevs.invui.item.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.time.Duration;
import java.util.function.Supplier;

public class AsyncAutoUpdateItem extends AbstractItem {

    private final Duration period;
    private final Supplier<ItemProvider> providerSupplier;

    private ItemProvider itemProvider;
    private BukkitTask task;

    public AsyncAutoUpdateItem(@Nullable ItemProvider placeholder, @NotNull Duration period, @NotNull Supplier<ItemProvider> providerSupplier) {
        this.itemProvider = placeholder == null ? new ItemWrapper(new ItemStack(Material.AIR)) : placeholder;
        this.period = period;
        this.providerSupplier = providerSupplier;
    }

    public void start() {
        if (task != null) task.cancel();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(InvUI.getInstance().getPlugin(), () -> {
            itemProvider = providerSupplier.get();
            Bukkit.getScheduler().runTask(InvUI.getInstance().getPlugin(), this::notifyWindows);
        }, 0, period.toMillis() / 50);
    }

    public void cancel() {
        task.cancel();
        task = null;
    }

    @Override
    public ItemProvider getItemProvider() {
        return itemProvider;
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
    }

}
