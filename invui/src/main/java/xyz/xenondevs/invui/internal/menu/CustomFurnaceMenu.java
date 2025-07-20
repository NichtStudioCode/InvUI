package xyz.xenondevs.invui.internal.menu;

import net.minecraft.world.inventory.MenuType;
import org.bukkit.entity.Player;

/**
 * A packet-based furnace menu.
 */
public class CustomFurnaceMenu extends CustomRecipeBookPoweredMenu {
    
    /**
     * Creates a new {@link CustomFurnaceMenu} for the specified viewer.
     *
     * @param player The player that will view the menu
     */
    public CustomFurnaceMenu(Player player) {
        super(MenuType.FURNACE, player);
    }
    
}
