package xyz.xenondevs.invui.inventory.event;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.InventorySlot;

import java.util.SequencedCollection;

/**
 * An {@link UpdateReason} that is used when a {@link Player} interacts with an {@link Inventory}
 * that is embedded in a {@link Gui}.
 */
public sealed interface PlayerUpdateReason extends UpdateReason {
    
    /**
     * The {@link Player} that interacted with the {@link Inventory}.
     *
     * @return The {@link Player} that interacted with the {@link Inventory}.
     */
    Player player();
    
    /**
     * A {@link PlayerUpdateReason} for clicking.
     *
     * @param player The player that clicked.
     * @param click  The click that was performed.
     */
    record Click(Player player, xyz.xenondevs.invui.Click click) implements PlayerUpdateReason {
        
        public Click(xyz.xenondevs.invui.Click click) {
            this(click.player(), click);
        }
        
    }
    
    /**
     * A {@link PlayerUpdateReason} for item-dragging.
     *
     * @param player    The player that dragged the items
     * @param clickType The button that was used to drag the items.
     *                  Can be either {@link ClickType#LEFT}, {@link ClickType#RIGHT} or {@link ClickType#MIDDLE}.
     * @param slots     The slots that were dragged over, in order.
     */
    record Drag(
        Player player,
        ClickType clickType,
        SequencedCollection<InventorySlot> slots
    ) implements PlayerUpdateReason {}
    
    /**
     * A {@link PlayerUpdateReason} for selecting an item in a bundle.
     *
     * @param player The player that selected the item.
     * @param bundleSlot The slot inside the bundle that was selected.
     */
    record BundleSelect(Player player, int bundleSlot) implements PlayerUpdateReason {}
    
}