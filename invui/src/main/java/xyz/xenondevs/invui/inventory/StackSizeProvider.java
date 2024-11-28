package xyz.xenondevs.invui.inventory;

import org.bukkit.inventory.ItemStack;

public interface StackSizeProvider {
    
    int getMaxStackSize(ItemStack itemStack);
    
}
