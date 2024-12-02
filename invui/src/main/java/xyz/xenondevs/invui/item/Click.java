package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * Contains information about a click on an item by a player.
 */
public class Click {
    
    private final Player player;
    private final ClickType clickType;
    private final InventoryAction action;
    private final InventoryType.SlotType slotType;
    private final int slot;
    private final int rawSlot;
    private final ItemStack cursor;
    private final int hotbarKey;
    
    /**
     * Creates a new click from an {@link InventoryClickEvent}.
     *
     * @param event The {@link InventoryClickEvent} to create the click from.
     */
    public Click(InventoryClickEvent event) {
        this.player = (Player) event.getWhoClicked();
        this.clickType = event.getClick();
        this.slotType = event.getSlotType();
        this.cursor = event.getCursor().clone();
        this.slot = event.getSlot();
        this.rawSlot = event.getRawSlot();
        this.hotbarKey = event.getHotbarButton();
        this.action = event.getAction();
    }
    
    /**
     * Gets the player who clicked.
     *
     * @return The player who clicked.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets the click type.
     *
     * @return The click type.
     */
    public ClickType getClickType() {
        return clickType;
    }
    
    /**
     * Gets the InventoryAction of this click.
     * This represents what the normal outcome of the click would have been.
     *
     * @return The InventoryAction the click.
     */
    public InventoryAction getAction() {
        return action;
    }
    
    /**
     * Gets the type of slot that was clicked.
     *
     * @return The type of slot that was clicked.
     */
    public InventoryType.SlotType getSlotType() {
        return slotType;
    }
    
    /**
     * Gets slot number of the {@link Inventory} that was clicked.
     *
     * @return The number of the slot that was clicked.
     */
    public int getSlot() {
        return slot;
    }
    
    /**
     * Gets the slot number of the {@link InventoryView} that was clicked.
     *
     * @return The number of the raw slot that was clicked.
     */
    public int getRawSlot() {
        return rawSlot;
    }
    
    /**
     * Gets the ItemStack on the cursor.
     *
     * @return The ItemStack on the cursor.
     */
    public ItemStack getCursor() {
        return cursor;
    }
    
    /**
     * Gets the hotbar key that was pressed (in [0; 8]),
     * or -1 if {@link #getClickType()} is not {@link ClickType#NUMBER_KEY}.
     *
     * @return The hotbar key that was pressed.
     */
    public int getHotbarKey() {
        return hotbarKey;
    }
    
}