package de.studiocode.invui.window;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.window.impl.merged.MergedWindow;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Manages all {@link Window}s and provides methods for searching them.
 */
public class WindowManager implements Listener {
    
    private static WindowManager instance;
    
    private final List<Window> windows = new CopyOnWriteArrayList<>();
    
    private WindowManager() {
        Bukkit.getPluginManager().registerEvents(this, InvUI.getInstance().getPlugin());
        InvUI.getInstance().addDisableHandler(() -> windows.forEach(window -> window.close(true)));
    }
    
    /**
     * Gets the {@link WindowManager} instance or creates a new one if there isn't one.
     *
     * @return The {@link WindowManager} instance
     */
    public static WindowManager getInstance() {
        return instance == null ? instance = new WindowManager() : instance;
    }
    
    /**
     * Adds a {@link Window} to the list of windows.
     * This method is usually called by the {@link Window} itself.
     *
     * @param window The {@link Window} to add
     */
    public void addWindow(Window window) {
        windows.add(window);
    }
    
    /**
     * Removes a {@link Window} from the list of windows.
     * This method is usually called by the {@link Window} itself.
     *
     * @param window The {@link Window} to remove
     */
    public void removeWindow(Window window) {
        windows.remove(window);
    }
    
    /**
     * Finds the {@link Window} to an {@link Inventory}.
     *
     * @param inventory The {@link Inventory}
     * @return The {@link Window} that belongs to that {@link Inventory}
     */
    public Optional<Window> findWindow(Inventory inventory) {
        return windows.stream()
            .filter(w -> Arrays.stream(w.getInventories()).anyMatch(inv -> inv == inventory))
            .findFirst();
    }
    
    /**
     * Finds all {@link Window}s to a {@link Player}
     *
     * @param player The {@link Player}
     * @return A list of {@link Window}s that have the {@link Player} as their viewer.
     */
    public List<Window> findWindows(Player player) {
        return windows.stream()
            .filter(w -> w.getViewer().getUniqueId().equals(player.getUniqueId()))
            .collect(Collectors.toCollection(ArrayList::new));
    }
    
    /**
     * Finds the {@link Window} the {@link Player} has currently open.
     *
     * @param player The {@link Player}
     * @return The {@link Window} the {@link Player} has currently open
     */
    public Optional<Window> findOpenWindow(Player player) {
        return windows.stream()
            .filter(w -> w.getCurrentViewer().equals(player))
            .findFirst();
    }
    
    /**
     * Gets a list of all currently active {@link Window}s.
     *
     * @return A list of all {@link Window}s
     */
    public List<Window> getWindows() {
        return windows;
    }
    
    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        Optional<Window> w = findWindow(event.getClickedInventory());
        if (w.isPresent()) { // player clicked window
            w.get().handleClick(event);
        } else {
            Optional<Window> w1 = findWindow(event.getView().getTopInventory());
            // player shift-clicked from lower inventory to window
            if (w1.isPresent() && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
                w1.get().handleItemShift(event);
        }
    }
    
    @EventHandler
    public void handleInventoryDrag(InventoryDragEvent event) {
        // currently, dragging items is not supported
        findWindow(event.getInventory()).ifPresent(w -> event.setCancelled(true));
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleInventoryClose(InventoryCloseEvent event) {
        findWindow(event.getInventory()).ifPresent(window -> window.handleClose((Player) event.getPlayer()));
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleInventoryOpen(InventoryOpenEvent event) {
        findWindow(event.getInventory()).ifPresent(window -> window.handleOpen(event));
    }
    
    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        findWindow(event.getPlayer().getOpenInventory().getTopInventory()).ifPresent(window -> window.handleClose(event.getPlayer()));
    }
    
    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent event) {
        findWindows(event.getEntity()).forEach(window -> window.handleViewerDeath(event));
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleItemPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Optional<Window> window = findOpenWindow(((Player) entity));
            if (window.isPresent() && window.get() instanceof MergedWindow)
                event.setCancelled(true);
        }
    }
    
}
