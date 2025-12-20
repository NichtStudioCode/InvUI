package xyz.xenondevs.invui.inventory;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

class InventoryAdapter implements org.bukkit.inventory.Inventory {
    
    private final Inventory inventory;
    
    public InventoryAdapter(Inventory inventory) {
        this.inventory = inventory;
    }
    
    @Override
    public int getSize() {
        return inventory.getSize();
    }
    
    @Override
    public int getMaxStackSize() {
        return Arrays.stream(inventory.getMaxStackSizes()).max().orElse(64);
    }
    
    @Override
    public void setMaxStackSize(int size) {
        if (inventory instanceof VirtualInventory vi) {
            vi.setMaxStackSizes(ArrayUtils.newIntArray(getSize(), size));
        }
    }
    
    @Override
    public @Nullable ItemStack getItem(int index) {
        return inventory.getItem(index);
    }
    
    @Override
    public void setItem(int index, @Nullable ItemStack item) {
        inventory.setItem(null, index, item);
    }
    
    @Override
    public HashMap<Integer, ItemStack> addItem(@Nullable ItemStack @Nullable ... items) {
        if (items == null)
            throw new IllegalArgumentException("items cannot be null");
        
        var result = new HashMap<Integer, ItemStack>();
        
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null)
                throw new IllegalArgumentException("Item at index " + i + " is null");
            
            int leftOver = inventory.addItem(null, item);
            if (leftOver > 0) {
                ItemStack remaining = item.clone();
                remaining.setAmount(leftOver);
                result.put(i, remaining);
            }
        }
        
        return result;
    }
    
    @Override
    public HashMap<Integer, ItemStack> removeItem(@Nullable ItemStack @Nullable ... items) {
        if (items == null)
            throw new IllegalArgumentException("items cannot be null");
        
        var result = new HashMap<Integer, ItemStack>();
        
        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null)
                continue;
            
            int toRemove = item.getAmount();
            int removed = inventory.removeFirstSimilar(null, toRemove, item);
            int leftOver = toRemove - removed;
            
            if (leftOver > 0) {
                ItemStack remaining = item.clone();
                remaining.setAmount(leftOver);
                result.put(i, remaining);
            }
        }
        
        return result;
    }
    
    @Override
    public HashMap<Integer, ItemStack> removeItemAnySlot(ItemStack... items) {
        return removeItem(items);
    }
    
    @Override
    public @Nullable ItemStack[] getContents() {
        return inventory.getItems();
    }
    
    @Override
    public void setContents(@Nullable ItemStack[] items) {
        if (items.length > getSize())
            throw new IllegalArgumentException("Array size (" + items.length + ") exceeds inventory size (" + getSize() + ")");
        for (int i = 0; i < items.length; i++) {
            inventory.setItem(null, i, items[i]);
        }
    }
    
    @Override
    public @Nullable ItemStack[] getStorageContents() {
        return getContents();
    }
    
    @Override
    public void setStorageContents(@Nullable ItemStack[] items) {
        setContents(items);
    }
    
    @Override
    public boolean contains(Material material) throws IllegalArgumentException {
        return inventory.contains(i -> i.getType() == material);
    }
    
    @Override
    public boolean contains(@Nullable ItemStack item) {
        return inventory.contains(i -> i.equals(item));
    }
    
    @Override
    public boolean contains(Material material, int amount) throws IllegalArgumentException {
        return inventory.count(i -> i.getType() == material) >= amount;
    }
    
    @Override
    public boolean contains(@Nullable ItemStack item, int amount) {
        return inventory.count(i -> i.equals(item)) >= amount;
    }
    
    @Override
    public boolean containsAtLeast(@Nullable ItemStack item, int amount) {
        if (item == null)
            return false;
        for (var itemInInv : inventory.getItems()) {
            if (itemInInv == null || !item.isSimilar(itemInInv))
                continue;
            if ((amount -= itemInInv.getAmount()) <= 0)
                return true;
        }
        return false;
    }
    
    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        var map = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < getSize(); i++) {
            ItemStack itemInInv = inventory.getItem(i);
            if (itemInInv != null && itemInInv.getType() == material) {
                map.put(i, itemInInv);
            }
        }
        return map;
    }
    
    @Override
    public HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item) {
        var map = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < getSize(); i++) {
            ItemStack itemInInv = inventory.getItem(i);
            if (itemInInv != null && itemInInv.equals(item)) {
                map.put(i, itemInInv);
            }
        }
        return map;
    }
    
    @Override
    public int first(Material material) {
        for (int i = 0; i < getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == material)
                return i;
        }
        return -1;
    }
    
    @Override
    public int first(ItemStack item) {
        for (int i = 0; i < getSize(); i++) {
            if (item.equals(inventory.getItem(i)))
                return i;
        }
        return -1;
    }
    
    @Override
    public int firstEmpty() {
        for (int i = 0; i < getSize(); i++) {
            if (getItem(i) == null)
                return i;
        }
        return -1;
    }
    
    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }
    
    @Override
    public void remove(Material material) throws IllegalArgumentException {
        inventory.removeIf(null, i -> i.getType() == material);
    }
    
    @Override
    public void remove(ItemStack item) {
        inventory.removeIf(null, i -> i.equals(item));
    }
    
    @Override
    public void clear(int index) {
        setItem(index, null);
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < getSize(); i++) {
            setItem(i, null);
        }
    }
    
    @Override
    public int close() {
        return 0;
    }
    
    @Override
    public List<HumanEntity> getViewers() {
        return List.of();
    }
    
    @Override
    public InventoryType getType() {
        return InventoryType.CHEST;
    }
    
    @Override
    public @Nullable InventoryHolder getHolder() {
        return null;
    }
    
    @Override
    public @Nullable InventoryHolder getHolder(boolean useSnapshot) {
        return null;
    }
    
    @Override
    public ListIterator<@Nullable ItemStack> iterator() {
        return iterator(0);
    }
    
    @Override
    public ListIterator<@Nullable ItemStack> iterator(int index) {
        return Arrays.asList(inventory.getItems()).listIterator(index);
    }
    
    @Override
    public @Nullable Location getLocation() {
        return null;
    }
}
