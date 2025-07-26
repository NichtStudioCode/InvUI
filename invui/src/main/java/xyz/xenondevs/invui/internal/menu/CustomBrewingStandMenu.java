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
    
    /**
     * Sets the brew progress, i.e. how much of the arrow is white.
     *
     * @param progress The brew progress, between 0.0 and 1.0.
     * @throws IllegalArgumentException If the progress is not between 0.0 and 1.0.
     */
    public void setBrewProgress(double progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("Brew progress must be between 0 and 1, but was " + progress);
        
        int brewTicks = progress == 0.0 ? 0 : (int) Math.round((1 - progress) * 400);
        dataSlots[0] = brewTicks;
    }
    
    /**
     * Sets the fuel progress, i.e. how much of the blaze bar is filled in.
     *
     * @param progress The fuel progress, between 0.0 and 1.0.
     * @throws IllegalArgumentException If the progress is not between 0.0 and 1.0.
     */
    public void setFuelProgress(double progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("Fuel progress must be between 0 and 1, but was " + progress);
        
        int fuelUses = (int) Math.round(progress * 20);
        dataSlots[1] = fuelUses;
    }
    
}
