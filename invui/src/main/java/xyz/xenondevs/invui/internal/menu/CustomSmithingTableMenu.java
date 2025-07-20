package xyz.xenondevs.invui.internal.menu;

import net.minecraft.network.HashedStack;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    public void setItem(int slot, @Nullable ItemStack item) {
        super.setItem(slot, item);
        
        // client-side prediction clears output slot when input slots are modified
        if (slot == 0 || slot == 1 || slot == 2) {
            remoteItems.set(3, HashedStack.EMPTY);
        }
    }
    
}
