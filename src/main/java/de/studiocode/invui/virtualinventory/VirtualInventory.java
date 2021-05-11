package de.studiocode.invui.virtualinventory;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.util.ArrayUtils;
import de.studiocode.invui.virtualinventory.event.ItemUpdateEvent;
import de.studiocode.invui.virtualinventory.event.UpdateReason;
import de.studiocode.invui.window.Window;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

// TODO: clean up
public class VirtualInventory implements ConfigurationSerializable {
    
    private final Set<Window> windows = new HashSet<>();
    
    private final UUID uuid;
    private int size;
    private ItemStack[] items;
    private Consumer<ItemUpdateEvent> itemUpdateHandler;
    
    /**
     * Creates a new {@link VirtualInventory}.
     *
     * @param uuid  The {@link UUID} this {@link VirtualInventory} should have.
     *              Can be null, only used for serialization.
     * @param size  The size of the {@link VirtualInventory}
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
     * Gets a deep copy of the {@link ItemStack}s in this {@link VirtualInventory}
     *
     * @return A copy of the {@link ItemStack}s in this {@link VirtualInventory}
     */
    public ItemStack[] getItems() {
        return Arrays.stream(items)
            .map(itemStack -> itemStack != null ? itemStack.clone() : null)
            .toArray(ItemStack[]::new);
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
     * @param updateReason The reason for item update, can be null.
     * @param index        The slot index
     * @param itemStack    The {@link ItemStack} that should be put on that slot
     * @return If the action has been cancelled
     */
    public boolean setItemStack(@Nullable UpdateReason updateReason, int index, ItemStack itemStack) {
        ItemStack newStack = itemStack.clone();
        ItemUpdateEvent event = createAndCallEvent(index, updateReason, items[index], newStack);
        if (!event.isCancelled()) {
            items[index] = newStack;
            notifyWindows();
            
            return false;
        }
        
        return true;
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
    public boolean hasItemStack(int index) {
        return items[index] != null;
    }
    
    /**
     * Sets an {@link ItemStack} on a specific slot or adds the amount
     * if there already is an {@link ItemStack} on that slot.
     *
     * @param updateReason The reason for item update, can be null.
     * @param index        The slot index
     * @param itemStack    The {@link ItemStack} to place
     * @return If the action has been cancelled
     */
    public boolean place(@Nullable UpdateReason updateReason, int index, ItemStack itemStack) {
        ItemStack currentStack = items[index];
        
        ItemStack newStack;
        if (currentStack == null) {
            newStack = itemStack.clone();
        } else {
            newStack = currentStack.clone();
            newStack.setAmount(newStack.getAmount() + itemStack.getAmount());
        }
        
        ItemUpdateEvent event = createAndCallEvent(index, updateReason, currentStack, newStack);
        if (!event.isCancelled()) {
            items[index] = newStack;
            notifyWindows();
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Puts on of an {@link ItemStack} on a specific slots or adds one
     * if there is already an {@link ItemStack} on that slot.
     *
     * @param updateReason The reason for item update, can be null.
     * @param index        The slot index
     * @param itemStack    The {@link ItemStack} to place one of
     * @return If the action has been cancelled
     */
    public boolean placeOne(@Nullable UpdateReason updateReason, int index, ItemStack itemStack) {
        ItemStack currentStack = items[index];
        
        ItemStack newStack;
        if (currentStack == null) {
            newStack = itemStack.clone();
            newStack.setAmount(1);
        } else {
            newStack = currentStack.clone();
            newStack.setAmount(newStack.getAmount() + 1);
        }
        
        ItemUpdateEvent event = createAndCallEvent(index, updateReason, currentStack, newStack);
        if (!event.isCancelled()) {
            items[index] = newStack;
            notifyWindows();
            
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
        ItemStack currentStack = items[index];
        if (currentStack != null) {
            if (amount == 0) items[index] = null;
            else currentStack.setAmount(amount);
            notifyWindows();
        }
    }
    
    /**
     * Changes the amount of an {@link ItemStack} on a specific slot
     * to the {@link ItemStack}'s {@link ItemStack#getMaxStackSize()}.
     *
     * @param updateReason The reason for item update, can be null.
     * @param index        The slot index
     * @return If the action has been cancelled
     */
    public boolean setToMaxAmount(@Nullable UpdateReason updateReason, int index) {
        ItemStack currentStack = items[index];
        if (currentStack != null) {
            ItemStack newStack = currentStack.clone();
            newStack.setAmount(newStack.getMaxStackSize());
            
            ItemUpdateEvent event = createAndCallEvent(index, updateReason, currentStack, newStack);
            if (!event.isCancelled()) {
                items[index] = newStack;
                notifyWindows();
                
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Removes an {@link ItemStack} on a specific slot from
     * the {@link VirtualInventory}.
     *
     * @param updateReason The reason for item update, can be null.
     * @param index        The slot index
     * @return If the action has been cancelled
     */
    public boolean removeItem(@Nullable UpdateReason updateReason, int index) {
        ItemStack currentStack = items[index];
        if (currentStack != null) {
            
            ItemUpdateEvent event = createAndCallEvent(index, updateReason, currentStack, null);
            if (!event.isCancelled()) {
                items[index] = null;
                notifyWindows();
                
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Removes one from an {@link ItemStack} on a specific slot.
     *
     * @param updateReason The reason for item update, can be null.
     * @param index        The slot index
     * @return If the action has been cancelled
     */
    public boolean removeOne(@Nullable UpdateReason updateReason, int index) {
        ItemStack currentStack = items[index];
        if (currentStack != null) {
            int newAmount = currentStack.getAmount() - 1;
            
            if (newAmount > 0) {
                ItemStack newStack = currentStack.clone();
                newStack.setAmount(newAmount);
                
                ItemUpdateEvent event = createAndCallEvent(index, updateReason, currentStack, newStack);
                if (!event.isCancelled()) {
                    items[index] = newStack;
                    notifyWindows();
                    
                    return false;
                }
            } else return removeItem(updateReason, index);
        }
        
        return true;
    }
    
    /**
     * Removes half of the {@link ItemStack} on a specific slot.
     *
     * @param updateReason The reason for item update, can be null.
     * @param index        The slot index
     * @return If the action has been cancelled
     */
    public boolean removeHalf(@Nullable UpdateReason updateReason, int index) {
        ItemStack currentStack = items[index];
        if (currentStack != null) {
            int newAmount = currentStack.getAmount() / 2;
            
            if (newAmount > 0) {
                ItemStack newStack = currentStack.clone();
                newStack.setAmount(newAmount);
                
                ItemUpdateEvent event = createAndCallEvent(index, updateReason, currentStack, newStack);
                if (!event.isCancelled()) {
                    items[index] = newStack;
                    notifyWindows();
                    
                    return false;
                }
                
            } else return removeItem(updateReason, index);
        }
        
        return true;
    }
    
    /**
     * Adds an {@link ItemStack} to the {@link VirtualInventory}.
     * This method does not work the same way as Bukkit's addItem method
     * as it respects the max stack size of the item type.
     *
     * @param updateReason The reason for item update, can be null.
     * @param itemStack    The {@link ItemStack} to add
     * @return The amount of items that couldn't be added
     * @see #simulateAdd(ItemStack)
     * @see #simulateMultiAdd(List) 
     */
    public int addItem(@Nullable UpdateReason updateReason, ItemStack itemStack) {
        final int originalAmount = itemStack.getAmount();
        int amountLeft = originalAmount;
        
        // find all slots where the item partially fits and add it there
        for (int partialSlot : findPartialSlots(itemStack)) {
            amountLeft = addTo(updateReason, partialSlot, amountLeft);
            if (amountLeft == 0) break;
        }
        
        // find all empty slots and put the item there
        while (amountLeft > 0) {
            int emptySlot = ArrayUtils.findFirstEmptyIndex(items);
            if (emptySlot == -1) break;
            amountLeft = addToEmpty(updateReason, emptySlot, itemStack, amountLeft);
        }
        
        // if items have been added, notify windows
        if (originalAmount != amountLeft) notifyWindows();
        
        // return how many items couldn't be added
        return amountLeft;
    }
    
    /**
     * Returns the amount of items that wouldn't fit in the inventory when added.
     * <br>
     * <strong>Note: This method does not add any {@link ItemStack}s to the {@link VirtualInventory}.</strong>
     *
     * @param itemStack The {@link ItemStack} to use
     * @return How many items wouldn't fit in the inventory when added
     */
    public int simulateAdd(ItemStack itemStack) {
        int maxStackSize = itemStack.getMaxStackSize();
        int amountLeft = itemStack.getAmount();
        
        // find all slots where the item partially fits
        for (int partialSlot : findPartialSlots(itemStack)) {
            ItemStack partialItem = items[partialSlot];
            amountLeft = Math.max(0, amountLeft - (maxStackSize - partialItem.getAmount()));
            if (amountLeft == 0) break;
        }
        
        // remaining items would be added to empty slots
        for (int ignored : ArrayUtils.findEmptyIndices(items)) {
            amountLeft -= Math.min(amountLeft, maxStackSize);
        }
        
        return amountLeft;
    }
    
    /**
     * Simulates adding multiple {@link ItemStack}s to this {@link VirtualInventory}
     * and returns the amount of {@link ItemStack}s that did not fit.<br>
     * This method should only be used for simulating the addition of <strong>multiple</strong> {@link ItemStack}s.
     * For a single {@link ItemStack} use {@link #simulateAdd(ItemStack)}<br>
     * <strong>Note: This method does not add any {@link ItemStack}s to the {@link VirtualInventory}.</strong>
     *
     * @param itemStacks The {@link ItemStack} to be used in the simulation
     * @return An array of integers representing the leftover amount for each {@link ItemStack} provided.
     * The size of this array is always equal to the amount of {@link ItemStack}s provided as method parameters.
     */
    public int[] simulateMultiAdd(List<ItemStack> itemStacks) {
        if (itemStacks.size() < 2) throw new IllegalArgumentException("Illegal amount of ItemStacks in List");
        
        VirtualInventory copiedInv = new VirtualInventory(null, size, getItems());
        int[] result = new int[itemStacks.size()];
        for (int index = 0; index != itemStacks.size(); index++) {
            result[index] = copiedInv.addItem(null, itemStacks.get(index));
        }
        
        return result;
    }
    
    /**
     * Checks if the {@link VirtualInventory} could theoretically hold the
     * provided {@link ItemStack}.
     *
     * @param itemStacks The {@link ItemStack}s
     * @return If the {@link VirtualInventory} can fit all these items
     */
    public boolean canHold(List<ItemStack> itemStacks) {
        if (itemStacks.size() == 0) return true;
        else if (itemStacks.size() == 1) return simulateAdd(itemStacks.get(0)) == 0;
        else return Arrays.stream(simulateMultiAdd(itemStacks)).allMatch(i -> i == 0);
    }
    
    public int collectToCursor(@Nullable UpdateReason updateReason, ItemStack itemStack) {
        int amount = itemStack.getAmount();
        int maxStackSize = itemStack.getMaxStackSize();
        if (amount < itemStack.getMaxStackSize()) {
            // find partial slots and take items from there
            for (int partialSlot : findPartialSlots(itemStack)) {
                amount += takeFrom(updateReason, partialSlot, maxStackSize - amount);
                if (amount == maxStackSize) break;
            }
            
            // if only taking from partial stacks wasn't enough, take from a full slot
            if (amount < itemStack.getMaxStackSize()) {
                int fullSlot = findFullSlot(itemStack);
                if (fullSlot != -1) {
                    amount += takeFrom(updateReason, fullSlot, maxStackSize - amount);
                }
            }
        }
        
        return amount;
    }
    
    private List<Integer> findPartialSlots(ItemStack itemStack) {
        List<Integer> partialSlots = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            ItemStack currentStack = items[i];
            if (currentStack != null && currentStack.getAmount() < currentStack.getMaxStackSize()
                && currentStack.isSimilar(itemStack)) partialSlots.add(i);
        }
        
        return partialSlots;
    }
    
    private int findFullSlot(ItemStack itemStack) {
        for (int i = 0; i < items.length; i++) {
            ItemStack currentStack = items[i];
            if (currentStack != null
                && currentStack.getAmount() == currentStack.getMaxStackSize()
                && currentStack.isSimilar(itemStack)) return i;
        }
        
        return -1;
    }
    
    private int addTo(@Nullable UpdateReason updateReason, int index, int amount) {
        ItemStack itemStack = items[index];
        
        int maxAddable = Math.min(itemStack.getMaxStackSize() - itemStack.getAmount(), amount);
        
        int currentAmount = itemStack.getAmount();
        int newAmount = currentAmount + maxAddable;
        
        ItemStack newStack = itemStack.clone();
        newStack.setAmount(newAmount);
        
        ItemUpdateEvent event = createAndCallEvent(index, updateReason, itemStack, newStack);
        if (!event.isCancelled()) {
            items[index] = newStack;
            notifyWindows();
            return amount - maxAddable;
        } else return amount;
    }
    
    private int addToEmpty(@Nullable UpdateReason updateReason, int index, @NotNull ItemStack type, int amount) {
        int maxAddable = Math.min(type.getType().getMaxStackSize(), amount);
        ItemStack newStack = type.clone();
        newStack.setAmount(maxAddable);
        
        if (setItemStack(updateReason, index, newStack)) return amount;
        else return amount - maxAddable;
    }
    
    private int takeFrom(@Nullable UpdateReason updateReason, int index, int maxTake) {
        ItemStack itemStack = items[index];
        int amount = itemStack.getAmount();
        int take = Math.min(amount, maxTake);
        
        ItemStack newStack;
        if (take != amount) {
            newStack = itemStack.clone();
            newStack.setAmount(amount - take);
        } else newStack = null;
        
        ItemUpdateEvent event = createAndCallEvent(index, updateReason, itemStack, newStack);
        if (!event.isCancelled()) {
            items[index] = newStack;
            notifyWindows();
            return take;
        }
        
        return 0;
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
    
    /**
     * Notifies all {@link Window}s displaying this {@link VirtualInventory} to update their
     * representative {@link ItemStack}s.
     * This method should only be called manually in very specific cases like when the
     * {@link ItemMeta} of an {@link ItemStack} in this inventory has changed.
     */
    public void notifyWindows() {
        Bukkit.getScheduler().runTask(InvUI.getInstance().getPlugin(), () ->
            windows.forEach(window -> window.handleVirtualInventoryUpdate(this)));
    }
    
    /**
     * Creates an {@link ItemUpdateEvent} and calls the {@link #itemUpdateHandler} to handle it.
     *
     * @param index             The slot index of the affected {@link ItemStack}
     * @param updateReason      The {@link UpdateReason}
     * @param previousItemStack The {@link ItemStack} that was previously on that slot
     * @param newItemStack      The {@link ItemStack} that will be on that slot
     * @return The {@link ItemUpdateEvent} after it has been handled by the {@link #itemUpdateHandler}
     */
    public ItemUpdateEvent createAndCallEvent(int index, UpdateReason updateReason, ItemStack previousItemStack, ItemStack newItemStack) {
        ItemUpdateEvent event = new ItemUpdateEvent(this, index, updateReason, previousItemStack, newItemStack);
        if (itemUpdateHandler != null) itemUpdateHandler.accept(event);
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
     * Sets the item update handler which will get called every time
     * an item gets updated in this {@link VirtualInventory}.
     *
     * @param itemUpdateHandler The item update handler
     */
    public void setItemUpdateHandler(Consumer<ItemUpdateEvent> itemUpdateHandler) {
        this.itemUpdateHandler = itemUpdateHandler;
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
