package xyz.xenondevs.invui.virtualinventory;

import org.bukkit.inventory.ItemStack;

public interface StackSizeProvider {
    
    int getMaxStackSize(ItemStack itemStack);
    
}
