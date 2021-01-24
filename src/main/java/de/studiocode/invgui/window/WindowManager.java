package de.studiocode.invgui.window;

import de.studiocode.invgui.InvGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class WindowManager implements Listener {
    
    private static WindowManager instance;
    
    private final List<Window> windows = new CopyOnWriteArrayList<>();
    
    private WindowManager() {
        Bukkit.getPluginManager().registerEvents(this, InvGui.getInstance().getPlugin());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(InvGui.getInstance().getPlugin(),
            () -> windows.forEach(Window::handleTick), 0, 1);
        InvGui.getInstance().addDisableHandler(() -> windows.forEach(w -> w.close(true)));
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
     * Finds the window to an inventory.
     *
     * @param inventory The inventory
     * @return The window that belongs to that inventory
     */
    public Optional<Window> findWindow(Inventory inventory) {
        return windows.stream()
            .filter(w -> w.getInventory() == inventory)
            .findFirst();
    }
    
    /**
     * Finds the window to a player.
     *
     * @param player The player
     * @return The window that the player has open
     */
    public Optional<Window> findWindow(Player player) {
        return windows.stream()
            .filter(w -> w.getInventory().getViewers().stream().anyMatch(player::equals))
            .findFirst();
    }
    
    /**
     * Gets a list of all currently active windows.
     *
     * @return A list of all windows
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
    
}
