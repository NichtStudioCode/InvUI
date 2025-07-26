package xyz.xenondevs.invui.window;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages all {@link Window Windows} and provides methods for searching them.
 */
public final class WindowManager implements Listener {
    
    private static final WindowManager INSTANCE = new WindowManager();
    
    private final Map<Player, AbstractWindow<?>> windowsByPlayer = new HashMap<>();
    
    private WindowManager() {
        Bukkit.getPluginManager().registerEvents(this, InvUI.getInstance().getPlugin());
        Bukkit.getScheduler().runTaskTimer(InvUI.getInstance().getPlugin(), this::handleTick, 1, 1);
        InvUI.getInstance().addDisableHandler(() -> new HashSet<>(windowsByPlayer.values()).forEach(AbstractWindow::close));
    }
    
    /**
     * Gets the {@link WindowManager} singleton instance.
     *
     * @return The {@link WindowManager} singleton instance
     */
    public static WindowManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Adds an {@link AbstractWindow} to the list of windows.
     * This method is usually called by the {@link Window} itself.
     *
     * @param window The {@link AbstractWindow} to add
     */
    void addWindow(AbstractWindow<?> window) {
        windowsByPlayer.put(window.getViewer(), window);
    }
    
    /**
     * Removes an {@link AbstractWindow} from the list of windows.
     * This method is usually called by the {@link Window} itself.
     *
     * @param window The {@link AbstractWindow} to remove
     */
    void removeWindow(AbstractWindow<?> window) {
        windowsByPlayer.remove(window.getViewer(), window);
    }
    
    /**
     * Gets the {@link Window} the {@link Player} has currently open.
     *
     * @param player The {@link Player}
     * @return The {@link Window} the {@link Player} has currently open
     */
    @Nullable
    public Window getOpenWindow(Player player) {
        return windowsByPlayer.get(player);
    }
    
    /**
     * Gets a set of all open {@link Window Windows}.
     *
     * @return A set of all {@link Window Windows}
     */
    public @Unmodifiable Set<Window> getWindows() {
        return Set.copyOf(windowsByPlayer.values());
    }
    
    private void handleTick() {
        for (AbstractWindow<?> window : windowsByPlayer.values()) {
            window.handleTick();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void handleInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        AbstractWindow<?> window = (AbstractWindow<?>) getOpenWindow(player);
        if (window != null) {
            window.handleClose(event.getReason());
        }
    }
    
}
