package de.studiocode.invgui.virtualinventory;

import de.studiocode.invgui.InvGui;
import de.studiocode.invgui.util.ArrayUtils;
import de.studiocode.invgui.window.Window;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VirtualInventory implements ConfigurationSerializable {
    
    private final Set<Window> windows = new HashSet<>();
    
    private final UUID uuid;
    private int size;
    private ItemStack[] items;
    
    public VirtualInventory(@Nullable UUID uuid, int size, @NotNull ItemStack[] items) {
        this.uuid = uuid;
        this.size = size;
        this.items = items;
    }
    
    public VirtualInventory(@Nullable UUID uuid, int size) {
        this(uuid, size, new ItemStack[size]);
    }
    
    public static VirtualInventory deserialize(@NotNull Map<String, Object> args) {
        //noinspection unchecked
        return new VirtualInventory(UUID.fromString((String) args.get("uuid")),
            (int) args.get("size"), ((ArrayList<ItemStack>) args.get("items")).toArray(new ItemStack[0]));
    }
    
    public int getSize() {
        return size;
    }
    
    public void resize(int size) {
        this.size = size;
        this.items = Arrays.copyOf(items, size);
    }
    
    public boolean isSynced(int index, ItemStack assumedStack) {
        ItemStack actualStack = items[index];
        return (actualStack == null && assumedStack == null)
            || (actualStack != null && actualStack.equals(assumedStack));
    }
    
    public int addItem(ItemStack itemStack) {
        final int originalAmount = itemStack.getAmount();
        int amountLeft = originalAmount;
        
        addItems:
        {
            // find all slots where the item partially fits and add it there
            ItemStack partialStack;
            while ((partialStack = findPartialSlot(itemStack)) != null) {
                amountLeft = addTo(partialStack, amountLeft);
                if (amountLeft == 0) break addItems;
            }
            
            // there are still items left, put the rest on an empty slot
            int emptyIndex = ArrayUtils.findFirstEmptyIndex(items);
            if (emptyIndex != -1) {
                ItemStack leftover = itemStack.clone();
                leftover.setAmount(amountLeft);
                items[emptyIndex] = leftover;
                amountLeft = 0;
            }
        }
        
        // if items have been added, notify windows
        if (originalAmount != amountLeft) notifyWindows();
        
        // return how many items couldn't be added
        return amountLeft;
    }
    
    private ItemStack findPartialSlot(ItemStack itemStack) {
        for (ItemStack currentStack : items) {
            if (currentStack != null && currentStack.getAmount() < currentStack.getMaxStackSize()
                && currentStack.isSimilar(itemStack)) return currentStack;
        }
        
        return null;
    }
    
    private int addTo(ItemStack itemStack, int amount) {
        int maxAddable = Math.min(itemStack.getMaxStackSize() - itemStack.getAmount(), amount);
        itemStack.setAmount(itemStack.getAmount() + maxAddable);
        return amount - maxAddable;
    }
    
    public void setItem(int index, ItemStack itemStack) {
        items[index] = itemStack.clone();
        notifyWindows();
    }
    
    public void setAmount(int index, int amount) {
        ItemStack itemStack = items[index];
        if (itemStack != null) {
            if (amount == 0) items[index] = null;
            else itemStack.setAmount(amount);
            
            notifyWindows();
        }
    }
    
    public void setMaxAmount(int index) {
        ItemStack itemStack = items[index];
        if (itemStack != null) {
            itemStack.setAmount(itemStack.getMaxStackSize());
            notifyWindows();
        }
    }
    
    public ItemStack getItemStack(int index) {
        return items[index];
    }
    
    public void removeItem(int index) {
        if (items[index] != null) {
            items[index] = null;
            notifyWindows();
        }
    }
    
    public void removeOne(int index) {
        ItemStack itemStack = items[index];
        if (itemStack != null) {
            int amount = itemStack.getAmount() - 1;
            if (amount > 0) itemStack.setAmount(amount);
            else items[index] = null;
            
            notifyWindows();
        }
    }
    
    public void removeHalf(int index) {
        ItemStack itemStack = items[index];
        if (itemStack != null) {
            int amount = itemStack.getAmount() / 2;
            if (amount > 0) itemStack.setAmount(amount);
            else items[index] = null;
            
            notifyWindows();
        }
    }
    
    public void place(int index, ItemStack itemStack) {
        ItemStack there = items[index];
        int currentAmount = there == null ? 0 : there.getAmount();
        
        if (there == null) {
            setItem(index, itemStack);
        } else {
            there.setAmount(currentAmount + itemStack.getAmount());
            notifyWindows();
        }
    }
    
    public void placeOne(int index, ItemStack itemStack) {
        ItemStack there = items[index];
        int currentAmount = there == null ? 0 : there.getAmount();
        
        if (there == null) {
            ItemStack single = itemStack.clone();
            single.setAmount(1);
            setItem(index, single);
        } else {
            there.setAmount(currentAmount + 1);
            notifyWindows();
        }
    }
    
    public boolean hasItem(int index) {
        return items[index] != null;
    }
    
    public void addWindow(Window window) {
        windows.add(window);
    }
    
    public void removeWindow(Window window) {
        windows.remove(window);
    }
    
    private void notifyWindows() {
        Bukkit.getScheduler().runTask(InvGui.getInstance().getPlugin(), () ->
            windows.forEach(window -> window.handleVirtualInventoryUpdate(this)));
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("uuid", uuid.toString());
        result.put("size", size);
        result.put("items", items);
        return result;
    }
    
}
