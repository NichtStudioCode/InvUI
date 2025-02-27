package xyz.xenondevs.invui.internal.menu;

import org.bukkit.entity.Player;
import xyz.xenondevs.invui.internal.util.InventoryUtils;

/**
 * A packet-based generic menu.
 */
public class CustomPlainMenu extends CustomContainerMenu {
    
    /**
     * Creates a new {@link CustomPlainMenu} for the specified viewer by choosing
     * a generic menu type matching the specified width and height.
     *
     * @param width  The width of the menu
     * @param height The height of the menu
     * @param player The player that will view the menu
     * @throws IllegalArgumentException if there is no matching generic menu type
     */
    public CustomPlainMenu(int width, int height, Player player) {
        super(InventoryUtils.getMatchingGenericMenuType(width, height), player);
    }
    
}
