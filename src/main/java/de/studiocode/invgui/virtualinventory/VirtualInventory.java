package de.studiocode.invgui.virtualinventory;

import de.studiocode.invgui.InvGui;
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
    
    public int getSize() {
        return size;
    }
    
    public void resize(int size) {
        this.size = size;
        this.items = Arrays.copyOf(items, size);
    }
    
    // TODO
    public void addItem(ItemStack... itemStacks) {
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public void setItem(int index, ItemStack itemStack) {
        items[index] = itemStack.clone();
        notifyWindows();
    }
    
    public ItemStack getItemStack(int index) {
        return items[index];
    }
    
    public boolean removeItem(int index) {
        if (hasItem(index)) {
            items[index] = null;
            notifyWindows();
            return true;
        }
        
        return false;
    }
    
    public boolean removeOne(int index) {
        if (!hasItem(index)) return false;
        
        int amount = items[index].getAmount() - 1;
        if (amount > 0) {
            items[index].setAmount(amount);
            notifyWindows();
        } else removeItem(index);
        
        return true;
    }
    
    public boolean removeHalf(int index, int expectedCurrentAmount) {
        if (!hasItem(index) || items[index].getAmount() != expectedCurrentAmount) return false;
        
        int amount = items[index].getAmount() / 2;
        
        if (amount > 0) {
            items[index].setAmount(amount);
            notifyWindows();
        } else removeItem(index);
        
        return true;
    }
    
    public boolean place(int index, ItemStack itemStack, int expectedCurrentAmount) {
        int currentAmount = items[index] == null ? 0 : items[index].getAmount();
        if (currentAmount != expectedCurrentAmount) return false;
        
        if (items[index] == null) {
            setItem(index, itemStack);
        } else {
            items[index].setAmount(expectedCurrentAmount + itemStack.getAmount());
            notifyWindows();
        }
        return true;
    }
    
    public boolean placeOne(int index, ItemStack itemStack, int expectedCurrentAmount) {
        int currentAmount = items[index] == null ? 0 : items[index].getAmount();
        if (currentAmount != expectedCurrentAmount) return false;
        
        if (items[index] == null) {
            ItemStack single = itemStack.clone();
            single.setAmount(1);
            setItem(index, single);
        } else {
            items[index].setAmount(expectedCurrentAmount + 1);
        }
        
        notifyWindows();
        return true;
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
    
    public static VirtualInventory deserialize(@NotNull Map<String, Object> args) {
        //noinspection unchecked
        return new VirtualInventory(UUID.fromString((String) args.get("uuid")),
            (int) args.get("size"), ((ArrayList<ItemStack>) args.get("items")).toArray(new ItemStack[0]));
    }
    
}
