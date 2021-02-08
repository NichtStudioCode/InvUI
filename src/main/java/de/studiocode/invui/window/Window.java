package de.studiocode.invui.window;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.GUIParent;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.itembuilder.ItemBuilder;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import de.studiocode.invui.window.impl.merged.combined.SimpleCombinedWindow;
import de.studiocode.invui.window.impl.merged.split.SimpleSplitWindow;
import de.studiocode.invui.window.impl.single.SimpleWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * A window is the way to show a player a GUI.
 * Windows can only have one viewer.
 *
 * @see SimpleWindow
 * @see SimpleCombinedWindow
 * @see SimpleSplitWindow
 */
public interface Window extends GUIParent {
    
    /**
     * Gets the underlying {@link Inventory}s.
     *
     * @return The underlying {@link Inventory}s.
     */
    Inventory[] getInventories();
    
    /**
     * Gets the underlying {@link GUI}s.
     *
     * @return The underlying {@link GUI}s.
     */
    GUI[] getGuis();
    
    /**
     * A method called by the {@link WindowManager} to notify the Window
     * that one of its {@link Item}s has been clicked.
     *
     * @param event The {@link InventoryClickEvent} associated with this action.
     */
    void handleClick(InventoryClickEvent event);
    
    /**
     * A method called by the {@link WindowManager} to notify the Window
     * that {@link ItemStack}s have been shift-clicked from the lower
     * {@link Inventory} to this {@link Window}
     * 
     * @param event The {@link InventoryClickEvent} associated with this action.
     */
    void handleItemShift(InventoryClickEvent event);
    
    /**
     * A method called by the {@link WindowManager} to notify the Window
     * that its underlying {@link Inventory} is being opened.
     *
     * @param event The {@link InventoryOpenEvent} associated with this action.
     */
    void handleOpen(InventoryOpenEvent event);
    
    /**
     * A method called by the {@link WindowManager} to notify the Window
     * that its underlying {@link Inventory} is being closed.
     *
     * @param player The {@link Player} who closed this inventory.
     */
    void handleClose(Player player);
    
    /**
     * A method called by the {@link WindowManager} to notify the Window
     * that it's viewer has died.
     *
     * @param event The {@link PlayerDeathEvent} associated with this action.
     */
    void handleViewerDeath(PlayerDeathEvent event);
    
    /**
     * A method called by the {@link Item} itself to notify the Window
     * that its {@link ItemBuilder} has been updated and the {@link ItemStack}
     * in the {@link Inventory} should be replaced.
     *
     * @param item The {@link Item} whose {@link ItemBuilder} has been updated.
     */
    void handleItemBuilderUpdate(Item item);
    
    /**
     * A method called by the {@link VirtualInventory} to notify the
     * Window that one if it's contents has been updated and the {@link ItemStack}'s
     * displayed in the {@link Inventory} should be replaced.
     *
     * @param virtualInventory The {@link VirtualInventory}
     */
    void handleVirtualInventoryUpdate(VirtualInventory virtualInventory);
    
    /**
     * Removes the {@link Window} from the {@link WindowManager} list.
     * If this method is called, the {@link Window} can't be shown again.
     *
     * @param closeForViewer If the underlying {@link Inventory} should be closed for the viewer.
     */
    void close(boolean closeForViewer);
    
    /**
     * Gets if the {@link Window} is closed and can't be shown again.
     *
     * @return If the {@link Window} is closed.
     */
    boolean isClosed();
    
    /**
     * Closes the underlying {@link Inventory} for its viewer.
     */
    void closeForViewer();
    
    /**
     * Shows the window to a the player.
     */
    void show();
    
    /**
     * Gets if the player is able to close the {@link Inventory}.
     *
     * @return If the player is able to close the {@link Inventory}.
     */
    boolean isCloseable();
    
    /**
     * Sets if the player should be able to close the {@link Inventory}.
     *
     * @param closeable If the player should be able to close the {@link Inventory}.
     */
    void setCloseable(boolean closeable);
    
    /**
     * Gets the viewer of this {@link Window}
     *
     * @return The viewer of this window.
     */
    Player getViewer();
    
    /**
     * Gets a the current {@link Player} that is viewing this
     * {@link Window} or null of there isn't one.
     *
     * @return The current viewer of this {@link Window} (can be null)
     */
    Player getCurrentViewer();
    
    /**
     * Gets the viewer's UUID or null if there is no viewer.
     *
     * @return The viewer's UUID or null if there is now viewer.
     */
    UUID getViewerUUID();
    
}
