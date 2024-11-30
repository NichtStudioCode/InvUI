package xyz.xenondevs.invui.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.window.Window;

/**
 * An ui element for use in {@link Gui Guis}.
 */
public sealed interface Item permits AbstractItem, BoundItem {
    
    /**
     * Gets the {@link ItemProvider}.
     * This method gets called every time a {@link Window} is notified ({@link #notifyWindows()}).
     *
     * @return The {@link ItemProvider}
     */
    ItemProvider getItemProvider();
    
    /**
     * Calls a refresh method on every {@link Window} in which this {@link Item} is displayed,
     * notifying them that the {@link ItemProvider} has been updated,
     * thus the {@link ItemStack} inside the {@link Window}'s {@link Inventory} should
     * be replaced.
     */
    void notifyWindows();
    
    /**
     * A method called if the {@link ItemStack} associated to this {@link Item}
     * has been clicked by a player.
     *
     * @param clickType The {@link ClickType} the {@link Player} performed.
     * @param player    The {@link Player} who clicked on the {@link ItemStack}.
     * @param event     The {@link InventoryClickEvent} associated with this click.
     */
    void handleClick(ClickType clickType, Player player, InventoryClickEvent event);
    
}
