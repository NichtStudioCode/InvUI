package xyz.xenondevs.invui.internal.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * A packet-based smithing table menu.
 */
public class CustomSmithingTableMenu extends CustomContainerMenu {
    
    /**
     * Creates a new {@link CustomSmithingTableMenu} for the specified player.
     *
     * @param player The player that will view the menu
     */
    public CustomSmithingTableMenu(Player player) {
        super(MenuType.SMITHING, player);
    }
    
    @Override
    public void setItem(int slot, org.bukkit.inventory.@Nullable ItemStack item) {
        super.setItem(slot, item);
        
        // client-side prediction clears output slot when input slots are modified
        if (slot == 0 || slot == 1 || slot == 2) {
            forceRemoteItem(3, ItemStack.EMPTY);
        }
    }
    
}
