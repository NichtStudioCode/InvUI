package xyz.xenondevs.invui.util;

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
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.StackSizeProvider;

public class InventoryUtils {
    
    public static StackSizeProvider stackSizeProvider = ItemStack::getMaxStackSize;
    
    public static int addItemCorrectly(@NotNull Inventory inventory, @NotNull ItemStack itemStack) {
        return addItemCorrectly(inventory, itemStack, new boolean[inventory.getSize()]);
    }
    
    public static int addItemCorrectly(@NotNull Inventory inventory, @NotNull ItemStack itemStack, boolean @NotNull [] blockedSlots) {
        int maxStackSize = Math.min(inventory.getMaxStackSize(), stackSizeProvider.getMaxStackSize(itemStack));
        int amountLeft = itemStack.getAmount();
        
        // add to partial stacks
        while (amountLeft > 0) {
            ItemStack partialStack = getFirstPartialStack(inventory, itemStack, blockedSlots);
            if (partialStack == null)
                break;
            
            int partialAmount = partialStack.getAmount();
            int addableAmount = Math.max(0, Math.min(amountLeft, maxStackSize - partialAmount));
            partialStack.setAmount(partialAmount + addableAmount);
            amountLeft -= addableAmount;
        }
        
        // add to empty slots
        while (amountLeft > 0) {
            int emptySlot = getFirstEmptySlot(inventory, blockedSlots);
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
    public static ItemStack getFirstPartialStack(@NotNull Inventory inventory, @NotNull ItemStack type) {
        return getFirstPartialStack(inventory, type, new boolean[inventory.getSize()]);
    }
    
    @Nullable
    public static ItemStack getFirstPartialStack(@NotNull Inventory inventory, @NotNull ItemStack type, boolean @NotNull [] blockedSlots) {
        int maxStackSize = stackSizeProvider.getMaxStackSize(type);
        
        ItemStack[] storageContents = inventory.getStorageContents();
        for (int i = 0; i < storageContents.length; i++) {
            if (blockedSlots[i] || isInvalidSlot(inventory, i))
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
    
    public static int getFirstEmptySlot(@NotNull Inventory inventory) {
        return getFirstEmptySlot(inventory, new boolean[inventory.getSize()]);
    }
    
    public static int getFirstEmptySlot(@NotNull Inventory inventory, boolean @NotNull [] blockedSlots) {
        ItemStack[] storageContents = inventory.getStorageContents();
        for (int i = 0; i < storageContents.length; i++) {
            if (blockedSlots[i] || isInvalidSlot(inventory, i))
                continue;
            
            ItemStack item = storageContents[i];
            if (ItemUtils.isEmpty(item))
                return i;
        }
        
        return -1;
    }
    
    private static boolean isInvalidSlot(@NotNull Inventory inventory, int slot) {
        if (inventory instanceof CraftingInventory) {
            // craft result slot
            return slot == 0;
        }
        
        return false;
    }
    
    public static Inventory createMatchingInventory(@NotNull Gui gui, @NotNull String title) {
        InventoryType type;
        
        if (gui.getWidth() == 9) type = null;
        else if (gui.getWidth() == 3 && gui.getHeight() == 3) type = InventoryType.DROPPER;
        else if (gui.getWidth() == 5 && gui.getHeight() == 1) type = InventoryType.HOPPER;
        else throw new UnsupportedOperationException("Invalid bounds of Gui");
        
        if (type == null) return Bukkit.createInventory(null, gui.getSize(), title);
        else return Bukkit.createInventory(null, type, title);
    }
    
    public static boolean containsSimilar(@NotNull Inventory inventory, @Nullable ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentStack = ItemUtils.takeUnlessEmpty(inventory.getItem(i));
            
            if ((currentStack == null && itemStack == null)
                || (currentStack != null && currentStack.isSimilar(itemStack))) return true;
        }
        
        return false;
    }
    
    public static void dropItemLikePlayer(@NotNull Player player, @NotNull ItemStack itemStack) {
        Location location = player.getLocation();
        location.add(0, 1.5, 0); // not the eye location
        Item item = location.getWorld().dropItem(location, itemStack);
        item.setPickupDelay(40);
        item.setVelocity(location.getDirection().multiply(0.35));
    }
    
}
