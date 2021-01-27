package de.studiocode.invgui.virtualinventory;

import de.studiocode.invgui.InvGui;
import de.studiocode.invgui.util.ArrayUtils;
import de.studiocode.invgui.util.Pair;
import de.studiocode.invgui.virtualinventory.event.ItemUpdateEvent;
import de.studiocode.invgui.window.Window;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VirtualInventory implements ConfigurationSerializable {
    
    private final Set<Window> windows = new HashSet<>();
    
    private final UUID uuid;
    private int size;
    private ItemStack[] items;
    
    /**
     * Creates a new {@link VirtualInventory}.
     *
     * @param uuid  The {@link UUID} this {@link VirtualInventory} should have.
     *              Can be null, only used for serialization.
     * @param size  The size of the {@link VirtualInventory} ( size > 0 )
     * @param items An array of {@link ItemStack} which reflects the contents of this
     *              {@link VirtualInventory}, therefore the length of that array has
     *              to be the same as <code>size</code>.
     */
    public VirtualInventory(@Nullable UUID uuid, int size, @NotNull ItemStack[] items) {
        if (size < 1) throw new IllegalArgumentException("size cannot be smaller than 1");
        if (items.length != size) throw new IllegalArgumentException("items length has to be the same as size");
        
        this.uuid = uuid;
        this.size = size;
        this.items = items;
    }
    
    /**
     * Creates a new {@link VirtualInventory}.
     *
     * @param uuid The {@link UUID} this {@link VirtualInventory} should have.
     *             Can be null, only used for serialization.
     * @param size The size of the {@link VirtualInventory}
     */
    public VirtualInventory(@Nullable UUID uuid, int size) {
        this(uuid, size, new ItemStack[size]);
    }
    
    /**
     * Deserializes to {@link VirtualInventory}
     *
     * @param args The args which contain the data to deserialize
     * @return The deserialized {@link VirtualInventory}
     */
    public static VirtualInventory deserialize(@NotNull Map<String, Object> args) {
        //noinspection unchecked
        return new VirtualInventory(UUID.fromString((String) args.get("uuid")),
            (int) args.get("size"), ((ArrayList<ItemStack>) args.get("items")).toArray(new ItemStack[0]));
    }
    
    /**
     * Gets the size of this {@link VirtualInventory}.
     *
     * @return The size of this {@link VirtualInventory}
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Changes the size of this {@link VirtualInventory}, removing
     * existing {@link ItemStack}s reduced.
     *
     * @param size The new size of this {@link VirtualInventory}
     */
    public void resize(int size) {
        this.size = size;
        this.items = Arrays.copyOf(items, size);
    }
    
    /**
     * Checks if the {@link ItemStack} on that slot index is the same
     * as the assumed {@link ItemStack} provided as parameter.
     *
     * @param index        The slot index
     * @param assumedStack The assumed {@link ItemStack}
     * @return If the {@link ItemStack} on that slot is the same as the assumed {@link ItemStack}
     */
    public boolean isSynced(int index, ItemStack assumedStack) {
        ItemStack actualStack = items[index];
        return (actualStack == null && assumedStack == null)
            || (actualStack != null && actualStack.equals(assumedStack));
    }
    
    /**
     * Sets an {@link ItemStack} on a specific slot.
     *
     * @param index     The slot index
     * @param itemStack The {@link ItemStack} that should be put on that slot
     */
    public void setItem(int index, ItemStack itemStack) {
        items[index] = itemStack.clone();
        notifyWindows();
    }
    
    /**
     * Gets the {@link ItemStack} on a specific slot.
     *
     * @param index The slot index
     * @return The {@link ItemStack} on that slot
     */
    public ItemStack getItemStack(int index) {
        return items[index];
    }
    
    /**
     * Checks if there is an {@link ItemStack} on a specific slot.
     *
     * @param index The slot index
     * @return If there is an {@link ItemStack} on that slot
     */
    public boolean hasItem(int index) {
        return items[index] != null;
    }
    
    /**
     * Sets an {@link ItemStack} on a specific slot or adds the amount
     * if there already is an {@link ItemStack} on that slot.
     *
     * @param player    The player that did this or <code>null</code> if it wasn't a player.
     * @param index     The slot index
     * @param itemStack The {@link ItemStack} to place
     * @return If the action has been cancelled
     */
    public boolean place(Player player, int index, ItemStack itemStack) {
        ItemStack there = items[index];
        int currentAmount = there == null ? 0 : there.getAmount();
        
        ItemUpdateEvent event = createAndCallEvent(player, itemStack, index, currentAmount,
            currentAmount + itemStack.getAmount());
        
        if (!event.isCancelled()) {
            if (there == null) {
                setItem(index, itemStack);
            } else {
                there.setAmount(currentAmount + itemStack.getAmount());
                notifyWindows();
            }
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Puts on of an {@link ItemStack} on a specific slots or adds one
     * if there is already an {@link ItemStack} on that slot.
     *
     * @param player    The player that did this or <code>null</code> if it wasn't a player.
     * @param index     The slot index
     * @param itemStack The {@link ItemStack} to place one of
     * @return If the action has been cancelled
     */
    public boolean placeOne(Player player, int index, ItemStack itemStack) {
        ItemStack there = items[index];
        int currentAmount = there == null ? 0 : there.getAmount();
        
        ItemUpdateEvent event = createAndCallEvent(player, itemStack, index,
            currentAmount, currentAmount + 1);
        
        if (!event.isCancelled()) {
            if (there == null) {
                ItemStack single = itemStack.clone();
                single.setAmount(1);
                setItem(index, single);
            } else {
                there.setAmount(currentAmount + 1);
                notifyWindows();
            }
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Changes the amount of an {@link ItemStack} on a specific slot without calling the {@link ItemUpdateEvent}
     *
     * @param index  The slot index
     * @param amount The new amount
     */
    public void setAmountSilently(int index, int amount) {
        ItemStack itemStack = items[index];
        if (itemStack != null) {
            if (amount == 0) items[index] = null;
            else itemStack.setAmount(amount);
            notifyWindows();
        }
    }
    
    /**
     * Changes the amount of an {@link ItemStack} on a specific slot
     * to the {@link ItemStack}'s {@link ItemStack#getMaxStackSize()}.
     *
     * @param player The player that did this or <code>null</code> if it wasn't a player.
     * @param index  The slot index
     * @return If the action has been cancelled
     */
    public boolean setToMaxAmount(Player player, int index) {
        ItemStack itemStack = items[index];
        if (itemStack != null) {
            int currentAmount = itemStack.getAmount();
            int newAmount = itemStack.getMaxStackSize();
            
            ItemUpdateEvent event = createAndCallEvent(player, itemStack, index,
                currentAmount, newAmount);
            
            if (!event.isCancelled()) {
                itemStack.setAmount(newAmount);
                notifyWindows();
            } else return true;
        }
        return false;
    }
    
    /**
     * Removes an {@link ItemStack} on a specific slot from
     * the {@link VirtualInventory}.
     *
     * @param player The player that did this or <code>null</code> if it wasn't a player.
     * @param index  The slot index
     * @return If the action has been cancelled
     */
    public boolean removeItem(Player player, int index) {
        ItemStack itemStack = items[index];
        if (itemStack != null) {
            
            ItemUpdateEvent event = createAndCallEvent(player, itemStack, index,
                itemStack.getAmount(), 0);
            
            if (!event.isCancelled()) {
                items[index] = null;
                notifyWindows();
            } else return true;
        }
        
        return false;
    }
    
    /**
     * Removes one from an {@link ItemStack} on a specific slot.
     *
     * @param player The player that did this or <code>null</code> if it wasn't a player.
     * @param index  The slot index
     * @return If the action has been cancelled
     */
    public boolean removeOne(Player player, int index) {
        ItemStack itemStack = items[index];
        if (itemStack != null) {
            int currentAmount = itemStack.getAmount();
            int newAmount = currentAmount - 1;
            
            ItemUpdateEvent event = createAndCallEvent(player, itemStack, index, currentAmount, newAmount);
            
            if (!event.isCancelled()) {
                if (newAmount > 0) itemStack.setAmount(newAmount);
                else items[index] = null;
                
                notifyWindows();
            } else return true;
        }
        
        return false;
    }
    
    /**
     * Removes half of the {@link ItemStack} on a specific slot.
     *
     * @param player The player that did this or <code>null</code> if it wasn't a player.
     * @param index  The slot index
     * @return If the action has been cancelled
     */
    public boolean removeHalf(Player player, int index) {
        ItemStack itemStack = items[index];
        if (itemStack != null) {
            int currentAmount = itemStack.getAmount();
            int newAmount = itemStack.getAmount() / 2;
            
            ItemUpdateEvent event = createAndCallEvent(player, itemStack, index, currentAmount, newAmount);
            
            if (!event.isCancelled()) {
                if (newAmount > 0) itemStack.setAmount(newAmount);
                else items[index] = null;
                notifyWindows();
            } else return true;
        }
        
        return false;
    }
    
    /**
     * Adds an {@link ItemStack} to the {@link VirtualInventory}.
     *
     * @param player    The player that did this or <code>null</code> if it wasn't a player.
     * @param itemStack The {@link ItemStack} to add
     * @return The amount of items that couldn't be added
     */
    public int addItem(Player player, ItemStack itemStack) {
        final int originalAmount = itemStack.getAmount();
        int amountLeft = originalAmount;
        
        // find all slots where the item partially fits and add it there
        Pair<Integer, ItemStack> partialSlot;
        while ((partialSlot = findPartialSlot(itemStack)) != null && amountLeft != 0)
            amountLeft = addTo(player, partialSlot, amountLeft);
        
        if (amountLeft != 0) {
            // there are still items left, put the rest on an empty slot
            int emptyIndex = ArrayUtils.findFirstEmptyIndex(items);
            if (emptyIndex != -1) {
                ItemStack leftover = itemStack.clone();
                leftover.setAmount(amountLeft);
                if (!place(player, emptyIndex, leftover))
                    amountLeft = 0;
            }
        }
        
        // if items have been added, notify windows
        if (originalAmount != amountLeft) notifyWindows();
        
        // return how many items couldn't be added
        return amountLeft;
    }
    
    private Pair<Integer, ItemStack> findPartialSlot(ItemStack itemStack) {
        for (int i = 0; i < items.length; i++) {
            ItemStack currentStack = items[i];
            if (currentStack != null && currentStack.getAmount() < currentStack.getMaxStackSize()
                && currentStack.isSimilar(itemStack)) return new Pair<>(i, currentStack);
        }
        
        return null;
    }
    
    private int addTo(Player player, Pair<Integer, ItemStack> partialSlot, int amount) {
        int index = partialSlot.getFirst();
        ItemStack itemStack = partialSlot.getSecond();
        
        int maxAddable = Math.min(itemStack.getMaxStackSize() - itemStack.getAmount(), amount);
        
        int currentAmount = itemStack.getAmount();
        int newAmount = currentAmount + maxAddable;
        
        ItemUpdateEvent event = createAndCallEvent(player, itemStack, index, currentAmount, newAmount);
        
        if (!event.isCancelled()) {
            itemStack.setAmount(itemStack.getAmount() + maxAddable);
            return amount - maxAddable;
        } else return amount;
    }
    
    /**
     * Adds a {@link Window} to the window set, telling the {@link VirtualInventory} that it is
     * currently being displayed in that {@link Window}.
     *
     * @param window The {@link Window} the {@link VirtualInventory} is currently displayed in.
     */
    public void addWindow(Window window) {
        windows.add(window);
    }
    
    /**
     * Removes an {@link Window} from the window set, telling the {@link VirtualInventory} that it
     * is no longer being displayed in that {@link Window}.
     *
     * @param window The {@link Window} the {@link VirtualInventory} is no longer displayed in.
     */
    public void removeWindow(Window window) {
        windows.remove(window);
    }
    
    private void notifyWindows() {
        Bukkit.getScheduler().runTask(InvGui.getInstance().getPlugin(), () ->
            windows.forEach(window -> window.handleVirtualInventoryUpdate(this)));
    }
    
    private ItemUpdateEvent createAndCallEvent(Player player, ItemStack itemStack, int index, int previousAmount, int newAmount) {
        ItemUpdateEvent event = new ItemUpdateEvent(this, player, itemStack, index, previousAmount, newAmount);
        Bukkit.getPluginManager().callEvent(event);
        
        return event;
    }
    
    /**
     * Gets the {@link UUID} of this {@link VirtualInventory}.
     *
     * @return The {@link UUID} of this {@link VirtualInventory}
     */
    public UUID getUuid() {
        return uuid;
    }
    
    /**
     * Serializes this {@link VirtualInventory} to a {@link Map}
     *
     * @return A {@link Map} that contains the serialized data of this {@link VirtualInventory}
     */
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
