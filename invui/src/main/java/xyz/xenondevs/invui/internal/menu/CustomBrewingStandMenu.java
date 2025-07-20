package xyz.xenondevs.invui.internal.menu;

import net.minecraft.world.inventory.MenuType;
import org.bukkit.entity.Player;

/**
 * A packet-based smithing table menu.
 */
public class CustomBrewingStandMenu extends CustomContainerMenu {
    
    /**
     * Creates a new {@link CustomBrewingStandMenu} for the specified player.
     *
     * @param player The player that will view the menu
     */
    public CustomBrewingStandMenu(Player player) {
        super(MenuType.BREWING_STAND, player);
    }
    
}
