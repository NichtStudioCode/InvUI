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
 *
 * @param player       The player who clicked.
 * @param clickType    The type of click.
 * @param action       The action that would've happened normally.
 * @param slotType     The type of slot that was clicked.
 * @param slot         The slot number of the {@link Inventory} that was clicked.
 * @param rawSlot      The raw slot number of the {@link InventoryView} that was clicked.
 * @param cursor       The ItemStack on the cursor.
 * @param hotbarButton The hotbar key that was pressed (in [0; 8]),
 *                     or -1 if {@link #clickType()} is not {@link ClickType#NUMBER_KEY}.
 */
public record Click(
    Player player,
    ClickType clickType,
    InventoryAction action,
    InventoryType.SlotType slotType,
    int slot,
    int rawSlot,
    ItemStack cursor,
    int hotbarButton
)
{
    
    /**
     * Creates a new click from an {@link InventoryClickEvent}.
     *
     * @param event The {@link InventoryClickEvent} to create the click from.
     */
    public Click(InventoryClickEvent event) {
        this(
            (Player) event.getWhoClicked(),
            event.getClick(),
            event.getAction(),
            event.getSlotType(),
            event.getSlot(),
            event.getRawSlot(),
            event.getCursor().clone(),
            event.getHotbarButton()
        );
    }
    
}