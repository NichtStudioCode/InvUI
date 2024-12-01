package xyz.xenondevs.invui.item;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.function.Supplier;

/**
 * An {@link Item} that updates its {@link ItemProvider} every specified amount
 * of ticks.
 */
public class AutoUpdateItem extends SuppliedItem {
    
    private final int period;
    private @Nullable BukkitTask task;
    
    public AutoUpdateItem(int period, Supplier<? extends ItemProvider> builderSupplier) {
        super(builderSupplier, null);
        this.period = period;
    }
    
    public void start() {
        if (task != null)
            task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::notifyWindows, 0, period);
    }
    
    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
    
    @Override
    public void addViewer(AbstractWindow who, int how) {
        super.addViewer(who, how);
        if (task == null)
            start();
    }
    
    @Override
    public void removeViewer(AbstractWindow who, int how) {
        super.removeViewer(who, how);
        if (viewers.isEmpty() && task != null)
            cancel();
    }
    
}
