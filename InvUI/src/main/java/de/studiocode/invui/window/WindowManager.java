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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages all {@link Window Windows} and provides methods for searching them.
 */
public class WindowManager implements Listener {
    
    private static WindowManager instance;
    
    private final Map<Inventory, Window> windows = new HashMap<>();
    private final Map<Player, Window> openWindows = new HashMap<>();
    
    private WindowManager() {
        Bukkit.getPluginManager().registerEvents(this, InvUI.getInstance().getPlugin());
        InvUI.getInstance().addDisableHandler(() -> getWindows().forEach(window -> window.close(true)));
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
        windows.put(window.getInventories()[0], window);
    }
    
    /**
     * Removes a {@link Window} from the list of windows.
     * This method is usually called by the {@link Window} itself.
     *
     * @param window The {@link Window} to remove
     */
    public void removeWindow(Window window) {
        windows.remove(window.getInventories()[0]);
    }
    
    /**
     * Finds the {@link Window} to an {@link Inventory}.
     *
     * @param inventory The {@link Inventory}
     * @return The {@link Window} that belongs to that {@link Inventory}
     */
    @Nullable
    public Window getWindow(Inventory inventory) {
        return windows.get(inventory);
    }
    
    /**
     * Gets the {@link Window} the {@link Player} has currently open.
     *
     * @param player The {@link Player}
     * @return The {@link Window} the {@link Player} has currently open
     */
    @Nullable
    public Window getOpenWindow(Player player) {
        return openWindows.get(player);
    }
    
    /**
     * Gets a set of all registered {@link Window Windows}.
     *
     * @return A set of all {@link Window Windows}
     */
    public Set<Window> getWindows() {
        return new HashSet<>(windows.values());
    }
    
    /**
     * Gets a set of all currently opened {@link Window Windows}.
     *
     * @return A set of all opened {@link Window Windows}
     */
    public Set<Window> getOpenWindows() {
        return new HashSet<>(openWindows.values());
    }
    
    @EventHandler
    private void handleInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Window window = getOpenWindow(player);
        
        if (window != null) {
            Inventory clicked = event.getClickedInventory();
            
            if (Arrays.asList(window.getInventories()).contains(clicked)) {
                // The inventory that was clicked is part of the open window
                window.handleClick(event);
            } else {
                // The inventory that was clicked is not part of the open window, so it is the player inventory
                switch (event.getAction()) {
                    case MOVE_TO_OTHER_INVENTORY:
                        window.handleItemShift(event);
                        break;
                    
                    // items have been collected by clicking a slot in the player inv
                    case COLLECT_TO_CURSOR:
                        window.handleCursorCollect(event);
                        break;
                }
            }
        }
    }
    
    @EventHandler
    private void handleInventoryDrag(InventoryDragEvent event) {
        Window window = getOpenWindow((Player) event.getWhoClicked());
        if (window != null) {
            window.handleDrag(event);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void handleInventoryClose(InventoryCloseEvent event) {
        Window window = getWindow(event.getInventory());
        if (window != null) {
            Player player = (Player) event.getPlayer();
            window.handleClose(player);
            openWindows.remove(player);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void handleInventoryOpen(InventoryOpenEvent event) {
        Window window = getWindow(event.getInventory());
        if (window != null) {
            window.handleOpen(event);
            openWindows.put((Player) event.getPlayer(), window);
        }
    }
    
    @EventHandler
    private void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Window window = getOpenWindow(player);
        if (window != null) {
            window.handleClose(player);
            openWindows.remove(player);
        }
    }
    
    @EventHandler
    private void handlePlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Window window = getOpenWindow(player);
        if (window != null) {
            window.handleViewerDeath(event);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void handleItemPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Window window = getOpenWindow((Player) entity);
            if (window instanceof MergedWindow)
                event.setCancelled(true);
        }
    }
    
}
