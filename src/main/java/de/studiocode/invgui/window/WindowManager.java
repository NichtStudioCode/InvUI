package de.studiocode.invgui.window;

import de.studiocode.invgui.InvGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
    }
    
    /**
     * Gets the {@link WindowManager} instance or creates a new one if there isn't one.
     * 
     * @return The {@link WindowManager} instance
     */
    public static WindowManager getInstance() {
        return hasInstance() ? instance = new WindowManager() : instance;
    }
    
    /**
     * Gets if the {@link WindowManager} already has an instance.
     * 
     * @return if the {@link WindowManager} already has an instance
     */
    public static boolean hasInstance() {
        return instance != null;
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
        findWindow(event.getClickedInventory()).ifPresent(window -> window.handleClick(event));
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
