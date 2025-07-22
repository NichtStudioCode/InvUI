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
    
    /**
     * Sets the cook progress, i.e. how much of the arrow is white.
     *
     * @param progress The cook progress, between 0.0 and 1.0.
     * @throws IllegalArgumentException If the progress is not between 0.0 and 1.0.
     */
    public void setCookProgress(double progress) {
        setProgress(progress, 1, 2);
    }
    
    /**
     * Sets the burn progress, i.e. how much of the fire is white.
     *
     * @param progress The burn progress, between 0.0 and 1.0.
     * @throws IllegalArgumentException If the progress is not between 0.0 and 1.0.
     */
    public void setBurnProgress(double progress) {
        setProgress(progress, 0, 1);
    }
    
    private void setProgress(double progress, int ticksField, int durationField) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("Burn progress must be between 0 and 1, but was " + progress);
        
        int duration = 256;
        int ticks = (int) Math.round(progress * duration);
        
        dataSlots[ticksField] = ticks;
        dataSlots[durationField] = duration;
    }
    
}
