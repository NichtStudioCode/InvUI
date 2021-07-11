package de.studiocode.invui.virtualinventory;

import de.studiocode.invui.InvUI;
import de.studiocode.invui.util.ArrayUtils;
import de.studiocode.invui.virtualinventory.event.ItemUpdateEvent;
import de.studiocode.invui.virtualinventory.event.UpdateReason;
import de.studiocode.invui.window.Window;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class VirtualInventory implements ConfigurationSerializable {
    
    private final UUID uuid;
    private int size;
    private ItemStack[] items;
    private int[] stackSizes;
    private final Set<Window> windows = new HashSet<>();
    private Consumer<ItemUpdateEvent> itemUpdateHandler;
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param uuid       The {@link UUID} of this {@link VirtualInventory}. Can be null, only used for serialization.
     * @param size       The amount of slots this {@link VirtualInventory} has.
     * @param items      A predefined array of content. Can be null. Will not get copied!
     * @param stackSizes An array of maximum allowed stack sizes for the each slot in the {@link VirtualInventory}.
     */
    public VirtualInventory(@Nullable UUID uuid, int size, @Nullable ItemStack[] items, int[] stackSizes) {
        this.uuid = uuid;
        this.size = size;
        this.items = items == null ? new ItemStack[size] : items;
        if (stackSizes == null) {
            this.stackSizes = new int[size];
            Arrays.fill(this.stackSizes, 64);
        } else this.stackSizes = stackSizes;
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param uuid The {@link UUID} of this {@link VirtualInventory}. Can be null, only used for serialization.
     * @param size The amount of slots this {@link VirtualInventory} has.
     */
    public VirtualInventory(@Nullable UUID uuid, int size) {
        this(uuid, size, null, null);
    }
    
    /**
     * Deserializes to {@link VirtualInventory}
     *
     * @param args The args which contain the data to deserialize
     * @return The deserialized {@link VirtualInventory}
     * @deprecated Use {@link VirtualInventoryManager#serializeInventory(VirtualInventory, OutputStream)}
     * and {@link VirtualInventoryManager#deserializeInventory(InputStream)} for serialization
     */
    @Deprecated
    public static VirtualInventory deserialize(@NotNull Map<String, Object> args) {
        //noinspection unchecked
        return new VirtualInventory(
            UUID.fromString((String) args.get("uuid")),
            (int) args.get("size"),
            ((ArrayList<ItemStack>) args.get("items")).toArray(new ItemStack[0]),
            ((ArrayList<Integer>) args.get("stackSizes")).stream().mapToInt(Integer::intValue).toArray()
        );
    }
    
    /**
     * Serializes this {@link VirtualInventory} to a {@link Map}
     *
     * @return A {@link Map} that contains the serialized data of this {@link VirtualInventory}
     * @deprecated Use {@link VirtualInventoryManager#serializeInventory(VirtualInventory, OutputStream)}
     * and {@link VirtualInventoryManager#deserializeInventory(InputStream)} for serialization
     */
    @Deprecated
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("uuid", uuid.toString());
        result.put("size", size);
        result.put("stackSizes", stackSizes);
        result.put("items", items);
        return result;
    }
    
    /**
     * Gets a set of {@link Window}s that display this {@link VirtualInventory}.
     *
     * @return An unmodifiable view of the set that contains all {@link Window}s that display
     * content of this {@link VirtualInventory}.
     */
    public Set<Window> getWindows() {
        return Collections.unmodifiableSet(windows);
    }
    
    /**
     * Adds a {@link Window} to the set of {@link Window}s, telling the {@link VirtualInventory} that
     * its contents are now being displayed in that {@link Window}.
     *
     * @param window The {@link Window} to be added.
     */
    public void addWindow(Window window) {
        windows.add(window);
    }
    
    /**
     * Removes a {@link Window} from the set of {@link Window}s, telling the {@link VirtualInventory} that
     * its contents are no longer being displayed in that {@link Window}.
     *
     * @param window The {@link Window} to be removed.
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
     * Changes the size of the {@link VirtualInventory}.
     * {@link ItemStack}s in slots which are no longer valid will be removed from the {@link VirtualInventory}.
     * This method does not call an event.
     *
     * @param size The new size of the {@link VirtualInventory}
     */
    public void resize(int size) {
        this.size = size;
        this.items = Arrays.copyOf(items, size);
        this.stackSizes = Arrays.copyOf(stackSizes, size);
    }
    
    /**
     * Sets a handler which is called every time something gets updated in the {@link VirtualInventory}.
     *
     * @param itemUpdateHandler The new item update handler
     */
    public void setItemUpdateHandler(Consumer<ItemUpdateEvent> itemUpdateHandler) {
        this.itemUpdateHandler = itemUpdateHandler;
    }
    
    /**
     * Gets the {@link UUID} of this {@link VirtualInventory}.
     *
     * @return The {@link UUID}
     */
    public UUID getUuid() {
        return uuid;
    }
    
    /**
     * Gets the size of this {@link VirtualInventory}.
     *
     * @return How many slots this {@link VirtualInventory} has.
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Gets the array of max stack sizes for this {@link VirtualInventory}.
     *
     * @return The array defining the max stack sizes for this {@link VirtualInventory}
     */
    public int[] getStackSizes() {
        return stackSizes;
    }
    
    /**
     * Gets a copy of the contents of this {@link VirtualInventory}.
     *
     * @return A deep copy of the {@link ItemStack}s this {@link VirtualInventory} contains.
     */
    public ItemStack[] getItems() {
        return Arrays.stream(items).map(item -> item != null ? item.clone() : null).toArray(ItemStack[]::new);
    }
    
    /**
     * Gets a clone of the {@link ItemStack} on that slot.
     *
     * @param slot The slot
     * @return The {@link ItemStack} on the given slot
     */
    public ItemStack getItemStack(int slot) {
        ItemStack itemStack = items[slot];
        return itemStack != null ? itemStack.clone() : null;
    }
    
    /**
     * Returns the actual {@link ItemStack} on that slot.
     * <br>
     * Not a clone, should be handled carefully as changes done on that item will not call any
     * Window updates (and create inconsistency between server and client),
     * in which case a manual call of {@link #notifyWindows} is needed.
     * <br>
     * Modifying this {@link ItemStack} will not call an {@link ItemUpdateEvent}.
     *
     * @param slot The slot
     * @return The actual {@link ItemStack} on that slot
     */
    public ItemStack getUnsafeItemStack(int slot) {
        return items[slot];
    }
    
    /**
     * Checks if there is an {@link ItemStack} on that slot.
     *
     * @param slot The Slot
     * @return If there is an {@link ItemStack} on that slot.
     */
    public boolean hasItem(int slot) {
        return items[slot] != null;
    }
    
    /**
     * Gets the amount of items on a slot.
     *
     * @param slot The slot
     * @return The amount of items on that slot
     */
    public int getAmount(int slot) {
        ItemStack currentStack = items[slot];
        return currentStack != null ? currentStack.getAmount() : 0;
    }
    
    /**
     * Checks if the {@link VirtualInventory} is empty.
     *
     * @return If there are no {@link ItemStack ItemStacks} in this {@link VirtualInventory}
     */
    public boolean isEmpty() {
        for (ItemStack itemStack : items)
            if (itemStack != null) return false;
        
        return true;
    }
    
    /**
     * Gets the maximum stack size for a specific slot. If there is an {@link ItemStack} on that
     * slot, the returned value will be the minimum of both the slot limit and {@link Material#getMaxStackSize()}.
     *
     * @param slot        The slot
     * @param alternative The alternative maximum stack size if no {@link ItemStack} is placed on that slot.
     *                    Should probably be the max stack size of the {@link Material} that will be added.
     * @return The current maximum allowed stack size on the specific slot.
     */
    public int getMaxStackSize(int slot, int alternative) {
        ItemStack currentItem = items[slot];
        int slotMaxStackSize = stackSizes == null ? 64 : stackSizes[slot];
        if (alternative != -1)
            return min(currentItem != null ? currentItem.getMaxStackSize() : alternative, slotMaxStackSize);
        else return slotMaxStackSize;
    }
    
    /**
     * Gets the maximum stack size for a specific slot while ignoring the {@link ItemStack} on it
     * and it's own maximum stack size.
     *
     * @param slot The slot
     * @return The maximum stack size on that slo
     */
    public int getMaxSlotStackSize(int slot) {
        return stackSizes == null ? 64 : stackSizes[slot];
    }
    
    /**
     * Sets all the maximum allowed stack sizes
     *
     * @param maxStackSizes All max stack sizes
     */
    public void setMaxStackSizes(int[] maxStackSizes) {
        this.stackSizes = maxStackSizes;
    }
    
    /**
     * Sets the maximum allowed stack size on a specific slot.
     *
     * @param slot         The slot
     * @param maxStackSize The max stack size
     */
    public void setMaxStackSize(int slot, int maxStackSize) {
        stackSizes[slot] = maxStackSize;
    }
    
    /**
     * Creates an {@link ItemUpdateEvent} and calls the {@link #itemUpdateHandler} to handle it.
     *
     * @param updateReason      The {@link UpdateReason}
     * @param slot              The slot of the affected {@link ItemStack}
     * @param previousItemStack The {@link ItemStack} that was previously on that slot
     * @param newItemStack      The {@link ItemStack} that will be on that slot
     * @return The {@link ItemUpdateEvent} after it has been handled by the {@link #itemUpdateHandler}
     */
    public ItemUpdateEvent callUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        ItemUpdateEvent event = new ItemUpdateEvent(this, slot, updateReason, previousItemStack, newItemStack);
        if (itemUpdateHandler != null) itemUpdateHandler.accept(event);
        return event;
    }
    
    /**
     * Checks if the {@link ItemStack} on that slot is the same
     * as the assumed {@link ItemStack} provided as parameter.
     *
     * @param slot         The slot
     * @param assumedStack The assumed {@link ItemStack}
     * @return If the {@link ItemStack} on that slot is the same as the assumed {@link ItemStack}
     */
    public boolean isSynced(int slot, ItemStack assumedStack) {
        ItemStack actualStack = items[slot];
        return (actualStack == null && assumedStack == null)
            || (actualStack != null && actualStack.equals(assumedStack));
    }
    
    /**
     * Changes the {@link ItemStack} on a specific slot to that one, regardless of what was
     * previously on that slot.
     * <br>
     * This method does not call an {@link ItemUpdateEvent} and ignores the maximum allowed stack size of
     * both the {@link Material} and the slot.
     * <br>
     * This method will always be successful.
     *
     * @param slot      The slot
     * @param itemStack The {@link ItemStack} to set.
     */
    public void setItemStackSilently(int slot, @Nullable ItemStack itemStack) {
        if (itemStack != null && itemStack.getAmount() == 0) items[slot] = null;
        else items[slot] = itemStack;
        notifyWindows();
    }
    
    /**
     * Changes the {@link ItemStack} on a specific slot to that one, regardless of what was
     * previously on that slot.
     * <br>
     * This method ignores the maximum allowed stack size of both the {@link Material} and the slot.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param slot         The slot
     * @param itemStack    The {@link ItemStack} to set.
     * @return If the action was successful
     */
    public boolean forceSetItemStack(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack itemStack) {
        ItemUpdateEvent event = callUpdateEvent(updateReason, slot, items[slot], itemStack);
        if (!event.isCancelled()) {
            setItemStackSilently(slot, event.getNewItemStack());
            return true;
        }
        return false;
    }
    
    /**
     * Changes the {@link ItemStack} on a specific slot to the given one, regardless of what previously was on
     * that slot.
     * <br>
     * This method will fail if the given {@link ItemStack} does not completely fit inside because of the
     * maximum allowed stack size.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param slot         The slot
     * @param itemStack    The {@link ItemStack} to set.
     * @return If the action was successful
     */
    public boolean setItemStack(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack itemStack) {
        int maxStackSize = min(getMaxSlotStackSize(slot), itemStack != null ? itemStack.getMaxStackSize() : 64);
        if (itemStack != null && itemStack.getAmount() > maxStackSize) return false;
        return forceSetItemStack(updateReason, slot, itemStack);
    }
    
    /**
     * Adds an {@link ItemStack} on a specific slot.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param slot         The slot
     * @param itemStack    The {@link ItemStack} to add.
     * @return The amount of items that did not fit on that slot.
     */
    public int putItemStack(@Nullable UpdateReason updateReason, int slot, @NotNull ItemStack itemStack) {
        ItemStack currentStack = items[slot];
        if (currentStack == null || currentStack.isSimilar(itemStack)) {
            int currentAmount = currentStack == null ? 0 : currentStack.getAmount();
            int maxStackSize = getMaxStackSize(slot, itemStack.getMaxStackSize());
            if (currentAmount < maxStackSize) {
                ItemStack newItemStack = itemStack.clone();
                newItemStack.setAmount(min(currentAmount + itemStack.getAmount(), maxStackSize));
                
                ItemUpdateEvent event = callUpdateEvent(updateReason, slot, currentStack, newItemStack);
                if (!event.isCancelled()) {
                    newItemStack = event.getNewItemStack();
                    items[slot] = newItemStack;
                    notifyWindows();
                    
                    return itemStack.getAmount() - (newItemStack.getAmount() - currentAmount);
                }
            }
        }
        
        return itemStack.getAmount();
    }
    
    /**
     * Sets the amount of an {@link ItemStack} on a slot to the given value
     * while respecting the max allowed stack size on that slot.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param slot         The slot
     * @param amount       The amount to change to.
     * @return The amount that it actually changed to.
     * @throws IllegalStateException If there is no {@link ItemStack} on that slot.
     */
    public int setItemAmount(@Nullable UpdateReason updateReason, int slot, int amount) {
        ItemStack currentStack = items[slot];
        if (currentStack == null) throw new IllegalStateException("There is no ItemStack on that slot");
        int maxStackSize = getMaxStackSize(slot, -1);
        
        ItemStack newItemStack;
        if (amount > 0) {
            newItemStack = currentStack.clone();
            newItemStack.setAmount(min(amount, maxStackSize));
        } else {
            newItemStack = null;
        }
        
        ItemUpdateEvent event = callUpdateEvent(updateReason, slot, currentStack, newItemStack);
        if (!event.isCancelled()) {
            newItemStack = event.getNewItemStack();
            items[slot] = newItemStack;
            notifyWindows();
            
            return newItemStack != null ? newItemStack.getAmount() : 0;
        }
        
        return amount;
    }
    
    /**
     * Adds a specific amount to an {@link ItemStack} on a slot while respecting
     * the maximum allowed stack size on that slot. Returns 0 if there is no
     * {@link ItemStack} on that slot.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param slot         The slot
     * @param amount       The amount to add
     * @return The amount that was actually added.
     */
    public int addItemAmount(@Nullable UpdateReason updateReason, int slot, int amount) {
        ItemStack currentStack = items[slot];
        if (currentStack == null) return 0;
        
        int currentAmount = currentStack.getAmount();
        return setItemAmount(updateReason, slot, currentAmount + amount) - currentAmount;
    }
    
    /**
     * Adds an {@link ItemStack} to the {@link VirtualInventory}.
     * This method does not work the same way as Bukkit's addItem method
     * as it respects the max stack size of the item type.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param itemStack    The {@link ItemStack} to add
     * @return The amount of items that didn't fit
     * @see #simulateAdd(ItemStack, ItemStack...)
     */
    public int addItem(@Nullable UpdateReason updateReason, ItemStack itemStack) {
        final int originalAmount = itemStack.getAmount();
        int amountLeft = originalAmount;
        
        // find all slots where the item partially fits and add it there
        for (int partialSlot : findPartialSlots(itemStack)) {
            if (amountLeft == 0) break;
            
            ItemStack stackToPut = itemStack.clone();
            stackToPut.setAmount(amountLeft);
            amountLeft = putItemStack(updateReason, partialSlot, stackToPut);
            
        }
        
        // find all empty slots and put the item there
        for (int emptySlot : ArrayUtils.findEmptyIndices(items)) {
            if (amountLeft == 0) break;
            
            ItemStack stackToPut = itemStack.clone();
            stackToPut.setAmount(amountLeft);
            amountLeft = putItemStack(updateReason, emptySlot, stackToPut);
        }
        
        // if items have been added, notify windows
        if (originalAmount != amountLeft) notifyWindows();
        
        // return how many items couldn't be added
        return amountLeft;
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link VirtualInventory}
     * and returns the amount of {@link ItemStack}s that did not fit.
     *
     * @return An array of integers representing the leftover amount for each {@link ItemStack} provided.
     * The size of this array is always equal to the amount of {@link ItemStack}s provided as method parameters.
     */
    public int[] simulateAdd(@NotNull ItemStack itemStack, @NotNull ItemStack... itemStacks) {
        if (itemStacks.length == 0) {
            return new int[] {simulateSingleAdd(itemStack)};
        } else {
            ItemStack[] allStacks = Stream.concat(Stream.of(itemStack), Arrays.stream(itemStacks)).toArray(ItemStack[]::new);
            return simulateMultiAdd(Arrays.asList(allStacks));
        }
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link VirtualInventory}
     * and returns the amount of {@link ItemStack}s that did not fit.
     *
     * @return An array of integers representing the leftover amount for each {@link ItemStack} provided.
     * The size of this array is always equal to the amount of {@link ItemStack}s provided as method parameters.
     */
    public int[] simulateAdd(@NotNull List<@NotNull ItemStack> itemStacks) {
        if (itemStacks.size() == 1) {
            return new int[] {simulateSingleAdd(itemStacks.get(0))};
        } else {
            return simulateMultiAdd(itemStacks);
        }
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link VirtualInventory}
     * and then returns if all {@link ItemStack}s would fit.
     *
     * @return If all provided {@link ItemStack}s would fit if added.
     */
    public boolean canHold(@NotNull ItemStack itemStack, @NotNull ItemStack... itemStacks) {
        if (itemStacks.length == 0) {
            return simulateSingleAdd(itemStack) == 0;
        } else {
            ItemStack[] allStacks = Stream.concat(Stream.of(itemStack), Arrays.stream(itemStacks)).toArray(ItemStack[]::new);
            return Arrays.stream(simulateMultiAdd(Arrays.asList(allStacks))).allMatch(i -> i == 0);
        }
    }
    
    /**
     * Returns the amount of items that wouldn't fit in the inventory when added.
     * <br>
     * <strong>Note: This method does not add any {@link ItemStack}s to the {@link VirtualInventory}.</strong>
     *
     * @param itemStack The {@link ItemStack} to use
     * @return How many items wouldn't fit in the inventory when added
     */
    private int simulateSingleAdd(@NotNull ItemStack itemStack) {
        int amountLeft = itemStack.getAmount();
        
        // find all slots where the item partially fits
        for (int partialSlot : findPartialSlots(itemStack)) {
            if (amountLeft == 0) break;
            
            ItemStack partialItem = items[partialSlot];
            int maxStackSize = getMaxStackSize(partialSlot, -1);
            amountLeft = max(0, amountLeft - (maxStackSize - partialItem.getAmount()));
        }
        
        // remaining items would be added to empty slots
        for (int emptySlot : ArrayUtils.findEmptyIndices(items)) {
            if (amountLeft == 0) break;
            
            int maxStackSize = getMaxStackSize(emptySlot, itemStack.getMaxStackSize());
            amountLeft -= min(amountLeft, maxStackSize);
        }
        
        return amountLeft;
    }
    
    /**
     * Simulates adding multiple {@link ItemStack}s to this {@link VirtualInventory}
     * and returns the amount of {@link ItemStack}s that did not fit.<br>
     *
     * @param itemStacks The {@link ItemStack} to be used in the simulation
     * @return An array of integers representing the leftover amount for each {@link ItemStack} provided.
     * The size of this array is always equal to the amount of {@link ItemStack}s provided as method parameters.
     */
    private int[] simulateMultiAdd(@NotNull List<@NotNull ItemStack> itemStacks) {
        VirtualInventory copiedInv = new VirtualInventory(null, size, getItems(), stackSizes.clone());
        int[] result = new int[itemStacks.size()];
        for (int index = 0; index != itemStacks.size(); index++) {
            result[index] = copiedInv.addItem(null, itemStacks.get(index));
        }
        
        return result;
    }
    
    /**
     * Finds all {@link ItemStack}s similar to the provided {@link ItemStack} and removes them from
     * their slot until the maximum stack size of the {@link Material} is reached.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param itemStack    The {@link ItemStack} to find matches to
     * @return The amount of collected items
     */
    public int collectToCursor(@Nullable UpdateReason updateReason, ItemStack itemStack) {
        int amount = itemStack.getAmount();
        int maxStackSize = itemStack.getMaxStackSize();
        if (amount < itemStack.getMaxStackSize()) {
            // find partial slots and take items from there
            for (int partialSlot : findPartialSlots(itemStack)) {
                amount += takeFrom(updateReason, partialSlot, maxStackSize - amount);
                if (amount == maxStackSize) return amount;
            }
            
            // only taking from partial stacks wasn't enough, take from a full slot
            for (int fullSlot : findFullSlots(itemStack)) {
                amount += takeFrom(updateReason, fullSlot, maxStackSize - amount);
                if (amount == maxStackSize) return amount;
            }
        }
        
        return amount;
    }
    
    private List<Integer> findPartialSlots(ItemStack itemStack) {
        List<Integer> partialSlots = new ArrayList<>();
        for (int slot = 0; slot < size; slot++) {
            ItemStack currentStack = items[slot];
            if (itemStack.isSimilar(currentStack)) {
                int maxStackSize = getMaxStackSize(slot, -1);
                if (currentStack.getAmount() < maxStackSize) partialSlots.add(slot);
            }
        }
        
        return partialSlots;
    }
    
    private List<Integer> findFullSlots(ItemStack itemStack) {
        List<Integer> fullSlots = new ArrayList<>();
        for (int slot = 0; slot < size; slot++) {
            ItemStack currentStack = items[slot];
            if (itemStack.isSimilar(currentStack)) {
                int maxStackSize = getMaxStackSize(slot, -1);
                if (currentStack.getAmount() == maxStackSize) fullSlots.add(slot);
            }
        }
        
        return fullSlots;
    }
    
    private int takeFrom(@Nullable UpdateReason updateReason, int index, int maxTake) {
        ItemStack itemStack = items[index];
        int amount = itemStack.getAmount();
        int take = min(amount, maxTake);
        
        ItemStack newStack;
        if (take != amount) {
            newStack = itemStack.clone();
            newStack.setAmount(amount - take);
        } else newStack = null;
        
        ItemUpdateEvent event = callUpdateEvent(updateReason, index, itemStack, newStack);
        if (!event.isCancelled()) {
            items[index] = newStack;
            notifyWindows();
            return take;
        }
        
        return 0;
    }
    
}
