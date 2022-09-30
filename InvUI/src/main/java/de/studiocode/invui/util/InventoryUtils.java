package de.studiocode.invui.util;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.virtualinventory.StackSizeProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryUtils {
    
    public static StackSizeProvider stackSizeProvider = ItemStack::getMaxStackSize;
    
    public static int addItemCorrectly(Inventory inventory, ItemStack itemStack) {
        int maxStackSize = Math.min(inventory.getMaxStackSize(), stackSizeProvider.getMaxStackSize(itemStack));
        int amountLeft = itemStack.getAmount();
        
        // add to partial stacks
        while (amountLeft > 0) {
            ItemStack partialStack = getFirstPartialStack(inventory, itemStack);
            if (partialStack == null)
                break;
            
            int partialAmount = partialStack.getAmount();
            int addableAmount = Math.max(0, Math.min(amountLeft, maxStackSize - partialAmount));
            partialStack.setAmount(partialAmount + addableAmount);
            amountLeft -= addableAmount;
        }
        
        // add to empty slots
        while (amountLeft > 0) {
            int emptySlot = getFirstEmptySlot(inventory);
            if (emptySlot == -1)
                break;
            
            int addableAmount = Math.min(amountLeft, maxStackSize);
            
            ItemStack newStack = itemStack.clone();
            newStack.setAmount(addableAmount);
            inventory.setItem(emptySlot, newStack);
            
            amountLeft -= addableAmount;
        }
        
        return amountLeft;
    }
    
    @Nullable
    public static ItemStack getFirstPartialStack(Inventory inventory, @NotNull ItemStack type) {
        int maxStackSize = stackSizeProvider.getMaxStackSize(type);
        
        ItemStack[] storageContents = inventory.getStorageContents();
        for (int i = 0; i < storageContents.length; i++) {
            if (isInvalidSlot(inventory, i))
                continue;
            
            ItemStack item = storageContents[i];
            if (type.isSimilar(item)) {
                int amount = item.getAmount();
                if (amount < maxStackSize)
                    return item;
            }
        }
        
        return null;
    }
    
    public static int getFirstEmptySlot(Inventory inventory) {
        ItemStack[] storageContents = inventory.getStorageContents();
        for (int i = 0; i < storageContents.length; i++) {
            if (isInvalidSlot(inventory, i))
                continue;
            
            ItemStack item = storageContents[i];
            if (item == null || item.getType().isAir())
                return i;
        }
        
        return -1;
    }
    
    private static boolean isInvalidSlot(Inventory inventory, int slot) {
        if (inventory instanceof CraftingInventory) {
            // craft result slot
            return slot == 0;
        }
        
        return false;
    }
    
    public static Inventory createMatchingInventory(GUI gui, String title) {
        InventoryType type;
        
        if (gui.getWidth() == 9) type = null;
        else if (gui.getWidth() == 3 && gui.getHeight() == 3) type = InventoryType.DROPPER;
        else if (gui.getWidth() == 5 && gui.getHeight() == 1) type = InventoryType.HOPPER;
        else throw new UnsupportedOperationException("Invalid bounds of GUI");
        
        if (type == null) return Bukkit.createInventory(null, gui.getSize(), title);
        else return Bukkit.createInventory(null, type, title);
    }
    
    public static boolean containsSimilar(Inventory inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentStack = inventory.getItem(i);
            if (currentStack != null && currentStack.getType().isAir()) currentStack = null;
            
            if ((currentStack == null && itemStack == null)
                || (currentStack != null && currentStack.isSimilar(itemStack))) return true;
        }
        
        return false;
    }
    
    public static void dropItemLikePlayer(Player player, ItemStack itemStack) {
        Location location = player.getLocation();
        location.add(0, 1.5, 0); // not the eye location
        Item item = location.getWorld().dropItem(location, itemStack);
        item.setPickupDelay(40);
        item.setVelocity(location.getDirection().multiply(0.35));
    }
    
}
