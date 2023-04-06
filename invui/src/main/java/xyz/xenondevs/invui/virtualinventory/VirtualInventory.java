package xyz.xenondevs.invui.virtualinventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.util.ArrayUtils;
import xyz.xenondevs.invui.util.InventoryUtils;
import xyz.xenondevs.invui.virtualinventory.event.InventoryUpdatedEvent;
import xyz.xenondevs.invui.virtualinventory.event.ItemUpdateEvent;
import xyz.xenondevs.invui.virtualinventory.event.UpdateReason;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

import java.io.OutputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class VirtualInventory {
    
    private final UUID uuid;
    private final Set<AbstractWindow> windows = new HashSet<>();
    private int size;
    private ItemStack[] items;
    private int[] stackSizes;
    private Consumer<ItemUpdateEvent> itemUpdateHandler;
    private Consumer<InventoryUpdatedEvent> inventoryUpdatedHandler;
    private int guiPriority = 0;
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param uuid       The {@link UUID} of this {@link VirtualInventory}. Can be null, only used for serialization.
     * @param size       The amount of slots this {@link VirtualInventory} has.
     * @param items      A predefined array of content. Can be null. Will not get copied!
     * @param stackSizes An array of maximum allowed stack sizes for each slot in the {@link VirtualInventory}. Can be null for 64.
     */
    public VirtualInventory(@Nullable UUID uuid, int size, @Nullable ItemStack @Nullable [] items, int @Nullable [] stackSizes) {
        this.uuid = uuid == null ? new UUID(0L, 0L) : uuid;
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
    
    public byte[] toByteArray() {
        return VirtualInventoryManager.getInstance().serializeInventory(this);
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
     * Adds an {@link AbstractWindow} to the set of {@link AbstractWindow AbstractWindows}, telling the {@link VirtualInventory} that
     * its contents are now being displayed in that {@link AbstractWindow}.
     *
     * @param window The {@link Window} to be added.
     */
    public void addWindow(AbstractWindow window) {
        windows.add(window);
    }
    
    /**
     * Removes an {@link AbstractWindow} from the set of {@link AbstractWindow AbstractWindows}, telling the {@link VirtualInventory} that
     * its contents are no longer being displayed in that {@link AbstractWindow}.
     *
     * @param window The {@link AbstractWindow} to be removed.
     */
    public void removeWindow(AbstractWindow window) {
        windows.remove(window);
    }
    
    /**
     * Notifies all {@link Window}s displaying this {@link VirtualInventory} to update their
     * representative {@link ItemStack}s.
     * This method should only be called manually in very specific cases like when the
     * {@link ItemMeta} of an {@link ItemStack} in this inventory has changed.
     */
    public void notifyWindows() {
        windows.forEach(window -> window.handleVirtualInventoryUpdate(this));
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
     * Sets a handler which is called every time after something has been updated in the {@link VirtualInventory}.
     *
     * @param inventoryUpdatedHandler The new handler
     */
    public void setInventoryUpdatedHandler(Consumer<InventoryUpdatedEvent> inventoryUpdatedHandler) {
        this.inventoryUpdatedHandler = inventoryUpdatedHandler;
    }
    
    /**
     * Gets the priority for click actions in a {@link Gui}, such as shift clicking or cursor collection
     * with multiple {@link VirtualInventory VirtualInventories}.
     *
     * @return The priority for click actions, {@link VirtualInventory VirtualInventories} with
     * a higher priority get prioritized.
     */
    public int getGuiPriority() {
        return guiPriority;
    }
    
    /**
     * Sets the priority for click actions in a {@link Gui}, such as shift-clicking or cursor collection
     * with multiple {@link VirtualInventory VirtualInventories}.
     * <p>
     * Not serialized with {@link VirtualInventoryManager#serializeInventory(VirtualInventory, OutputStream)}.
     *
     * @param guiPriority The priority for click actions, {@link VirtualInventory VirtualInventories} with
     *                    a higher priority get prioritized.
     */
    public void setGuiPriority(int guiPriority) {
        this.guiPriority = guiPriority;
    }
    
    /**
     * Gets the {@link UUID} of this {@link VirtualInventory}.
     *
     * @return The {@link UUID}
     */
    @NotNull
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
     * Gets the same {@link ItemStack ItemStack[]} that backs this {@link VirtualInventory}.
     * <br>
     * As it is not clone, it should be handled carefully as changes done on that array will not call any
     * Window updates (and create inconsistency between server and client).
     */
    public ItemStack[] getUnsafeItems() {
        return items;
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
     * Checks if all slots have an {@link ItemStack} with their max stack size on them.
     *
     * @return Whether this {@link VirtualInventory} is full.
     */
    public boolean isFull() {
        for (int slot = 0; slot < size; slot++) {
            ItemStack item = items[slot];
            if (item == null || item.getAmount() < getMaxStackSize(slot))
                return false;
        }
        
        return true;
    }
    
    /**
     * Checks if there are no {@link ItemStack ItemStacks} in this {@link VirtualInventory}.
     *
     * @return Whether this {@link VirtualInventory} is empty.
     */
    public boolean isEmpty() {
        for (ItemStack item : items)
            if (item != null) return false;
        
        return true;
    }
    
    /**
     * Checks whether this {@link VirtualInventory} has at least one empty slot.
     *
     * @return Whether this {@link VirtualInventory} has at least one empty slot.
     */
    public boolean hasEmptySlot() {
        for (ItemStack item : items)
            if (item == null) return true;
        
        return false;
    }
    
    /**
     * Checks if there is any {@link ItemStack} in this {@link VirtualInventory} matching the given {@link Predicate}.
     *
     * @param predicate The {@link Predicate} to check.
     * @return Whether there is any {@link ItemStack} in this {@link VirtualInventory} matching the given {@link Predicate}.
     */
    public boolean contains(Predicate<ItemStack> predicate) {
        for (ItemStack item : items) {
            if (item != null && predicate.test(item.clone()))
                return true;
        }
        
        return false;
    }
    
    /**
     * Checks if there is any {@link ItemStack} in this {@link VirtualInventory} similar to the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to match against.
     * @return Whether there is any {@link ItemStack} in this {@link VirtualInventory} similar to the given {@link ItemStack}.
     */
    public boolean containsSimilar(ItemStack itemStack) {
        for (ItemStack item : items) {
            if (item != null && item.isSimilar(itemStack))
                return true;
        }
        
        return false;
    }
    
    /**
     * Counts the amount of {@link ItemStack ItemStacks} in this {@link VirtualInventory} matching the given {@link Predicate}.
     *
     * @param predicate The {@link Predicate} to check.
     * @return The amount of {@link ItemStack ItemStacks} in this {@link VirtualInventory} matching the given {@link Predicate}.
     */
    public int count(Predicate<ItemStack> predicate) {
        int count = 0;
        for (ItemStack item : items) {
            if (item != null && predicate.test(item.clone()))
                count++;
        }
        
        return count;
    }
    
    /**
     * Counts the amount of {@link ItemStack ItemStacks} in this {@link VirtualInventory} similar to the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to match against.
     * @return The amount of {@link ItemStack ItemStacks} in this {@link VirtualInventory} similar to the given {@link ItemStack}.
     */
    public int countSimilar(ItemStack itemStack) {
        int count = 0;
        for (ItemStack item : items) {
            if (item != null && item.isSimilar(itemStack))
                count++;
        }
        
        return count;
    }
    
    /**
     * Gets the maximum stack size for a specific slot.
     * <p>
     * If there is an {@link ItemStack} on that slot, the returned value will be the minimum of both the slot's max stack
     * size and the {@link ItemStack ItemStack's} max stack size retrieved using {@link InventoryUtils#stackSizeProvider}.
     *
     * @param slot The slot
     * @return The current maximum allowed stack size on the specific slot.
     */
    public int getMaxStackSize(int slot) {
        int slotMaxStackSize = stackSizes == null ? 64 : stackSizes[slot];
        ItemStack currentItem = items[slot];
        if (currentItem != null) {
            return min(InventoryUtils.stackSizeProvider.getMaxStackSize(currentItem), slotMaxStackSize);
        } else {
            return slotMaxStackSize;
        }
    }
    
    /**
     * Gets the maximum stack size for a specific slot.
     * <p>
     * If there is an {@link ItemStack} on that slot, the returned value will be the minimum of both the slot's
     * max stack size and the {@link ItemStack ItemStack's} max stack size retrieved using {@link InventoryUtils#stackSizeProvider}.
     * <p>
     * If there is no {@link ItemStack} on that slot, the alternative parameter will be used as a potential maximum stack size.
     *
     * @param slot        The slot
     * @param alternative The alternative maximum stack size if no {@link ItemStack} is placed on that slot.
     *                    Should probably be the max stack size of the {@link Material} that will be added.
     * @return The current maximum allowed stack size on the specific slot.
     */
    public int getMaxStackSize(int slot, int alternative) {
        ItemStack currentItem = items[slot];
        int slotMaxStackSize = stackSizes == null ? 64 : stackSizes[slot];
        return min(currentItem != null ? InventoryUtils.stackSizeProvider.getMaxStackSize(currentItem) : alternative, slotMaxStackSize);
    }
    
    /**
     * Gets the maximum stack size for a specific slot. If there is an {@link ItemStack} on that slot,
     * the returned value will be the minimum of both the slot's and the {@link ItemStack ItemStack's} max stack size retrieved
     * using {@link InventoryUtils#stackSizeProvider}. If there is no {@link ItemStack} on that slot, the alternativeFrom
     * parameter will be used to determine a potential maximum stack size.
     *
     * @param slot            The slot
     * @param alternativeFrom The alternative {@link ItemStack} to determine the potential maximum stack size. Uses 64 if null.
     * @return The current maximum allowed stack size on the specific slot.
     */
    public int getMaxStackSize(int slot, @Nullable ItemStack alternativeFrom) {
        int itemMaxStackSize = alternativeFrom == null ? 64 : InventoryUtils.stackSizeProvider.getMaxStackSize(alternativeFrom);
        return getMaxStackSize(slot, itemMaxStackSize);
    }
    
    /**
     * Gets the maximum stack size for a specific slot while ignoring the {@link ItemStack} on it.
     * The returned value will be a minimum of the slot's maximum stack size and the alternative parameter.
     *
     * @param slot        The slot
     * @param alternative The alternative maximum stack size. Should probably be the max stack size of the {@link Material} that will be added.
     * @return The maximum stack size on that slot
     */
    public int getMaxSlotStackSize(int slot, int alternative) {
        int slotMaxStackSize = stackSizes == null ? 64 : stackSizes[slot];
        return min(alternative, slotMaxStackSize);
    }
    
    /**
     * Gets the maximum stack size for a specific slot while ignoring the {@link ItemStack} on it.
     * The returned value will be a minimum of the maximum stack size of both the slot and the alternativeFrom parameter.
     *
     * @param slot            The slot
     * @param alternativeFrom The alternative {@link ItemStack} to determine the potential maximum stack size. Uses 64 if null.
     * @return The maximum stack size on that slot
     */
    public int getMaxSlotStackSize(int slot, @Nullable ItemStack alternativeFrom) {
        int itemMaxStackSize = alternativeFrom == null ? 64 : InventoryUtils.stackSizeProvider.getMaxStackSize(alternativeFrom);
        return getMaxSlotStackSize(slot, itemMaxStackSize);
    }
    
    /**
     * Gets the maximum stack size for a specific slot while ignoring the {@link ItemStack} on it
     * and its own maximum stack size.
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
    public ItemUpdateEvent callPreUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call ItemUpdateEvent with UpdateReason.SUPPRESSED");
        
        ItemUpdateEvent event = new ItemUpdateEvent(this, slot, updateReason, previousItemStack, newItemStack);
        if (itemUpdateHandler != null) {
            try {
                itemUpdateHandler.accept(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return event;
    }
    
    /**
     * Creates an {@link InventoryUpdatedEvent} and calls the {@link #inventoryUpdatedHandler} to handle it.
     *
     * @param updateReason      The {@link UpdateReason}
     * @param slot              The slot of the affected {@link ItemStack}
     * @param previousItemStack The {@link ItemStack} that was on that slot previously.
     * @param newItemStack      The {@link ItemStack} that is on that slot now.
     */
    public void callAfterUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call InventoryUpdatedEvent with UpdateReason.SUPPRESSED");
        
        InventoryUpdatedEvent event = new InventoryUpdatedEvent(this, slot, updateReason, previousItemStack, newItemStack);
        if (inventoryUpdatedHandler != null) {
            try {
                inventoryUpdatedHandler.accept(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        if (updateReason == UpdateReason.SUPPRESSED) {
            setItemStackSilently(slot, itemStack);
            return true;
        } else {
            ItemStack previousStack = items[slot];
            ItemUpdateEvent event = callPreUpdateEvent(updateReason, slot, previousStack, itemStack);
            if (!event.isCancelled()) {
                ItemStack newStack = event.getNewItemStack();
                setItemStackSilently(slot, newStack);
                callAfterUpdateEvent(updateReason, slot, previousStack, newStack);
                return true;
            }
            return false;
        }
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
        int maxStackSize = getMaxSlotStackSize(slot, itemStack);
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
            int maxStackSize = getMaxStackSize(slot, itemStack);
            if (currentAmount < maxStackSize) {
                int additionalAmount = itemStack.getAmount();
                int newAmount = min(currentAmount + additionalAmount, maxStackSize);
                
                ItemStack newItemStack = itemStack.clone();
                newItemStack.setAmount(newAmount);
                
                if (updateReason != UpdateReason.SUPPRESSED) {
                    ItemUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStack, newItemStack);
                    if (!event.isCancelled()) {
                        newItemStack = event.getNewItemStack();
                        items[slot] = newItemStack;
                        notifyWindows();
                        
                        callAfterUpdateEvent(updateReason, slot, currentStack, newItemStack);
                        
                        int newAmountEvent = newItemStack != null ? newItemStack.getAmount() : 0;
                        return itemStack.getAmount() - (newAmountEvent - currentAmount);
                    }
                } else {
                    items[slot] = newItemStack;
                    notifyWindows();
                    return additionalAmount - (newAmount - currentAmount);
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
        int maxStackSize = getMaxStackSize(slot);
        
        ItemStack newItemStack;
        if (amount > 0) {
            newItemStack = currentStack.clone();
            newItemStack.setAmount(min(amount, maxStackSize));
        } else {
            newItemStack = null;
        }
        
        if (updateReason != UpdateReason.SUPPRESSED) {
            ItemUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStack, newItemStack);
            if (!event.isCancelled()) {
                newItemStack = event.getNewItemStack();
                items[slot] = newItemStack;
                notifyWindows();
                
                callAfterUpdateEvent(updateReason, slot, currentStack, newItemStack);
                
                return newItemStack != null ? newItemStack.getAmount() : 0;
            }
        } else {
            items[slot] = newItemStack;
            notifyWindows();
            return amount;
        }
        
        return currentStack.getAmount();
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
        if (itemStacks.isEmpty()) return new int[] {};
        
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
    public boolean canHold(@NotNull ItemStack first, @NotNull ItemStack... rest) {
        if (rest.length == 0) {
            return simulateSingleAdd(first) == 0;
        } else {
            ItemStack[] allStacks = Stream.concat(Stream.of(first), Arrays.stream(rest)).toArray(ItemStack[]::new);
            return Arrays.stream(simulateMultiAdd(Arrays.asList(allStacks))).allMatch(i -> i == 0);
        }
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link VirtualInventory}
     * and then returns if all {@link ItemStack}s would fit.
     *
     * @return If all provided {@link ItemStack}s would fit if added.
     */
    public boolean canHold(@NotNull List<@NotNull ItemStack> itemStacks) {
        if (itemStacks.isEmpty()) return true;
        
        if (itemStacks.size() == 1) {
            return simulateSingleAdd(itemStacks.get(0)) == 0;
        } else {
            return Arrays.stream(simulateMultiAdd(itemStacks)).allMatch(i -> i == 0);
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
    public int simulateSingleAdd(@NotNull ItemStack itemStack) {
        int amountLeft = itemStack.getAmount();
        
        // find all slots where the item partially fits
        for (int partialSlot : findPartialSlots(itemStack)) {
            if (amountLeft == 0) break;
            
            ItemStack partialItem = items[partialSlot];
            int maxStackSize = getMaxStackSize(partialSlot);
            amountLeft = max(0, amountLeft - (maxStackSize - partialItem.getAmount()));
        }
        
        // remaining items would be added to empty slots
        for (int emptySlot : ArrayUtils.findEmptyIndices(items)) {
            if (amountLeft == 0) break;
            
            int maxStackSize = getMaxStackSize(emptySlot, itemStack);
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
    public int[] simulateMultiAdd(@NotNull List<@NotNull ItemStack> itemStacks) {
        VirtualInventory copiedInv = new VirtualInventory(null, size, getItems(), stackSizes.clone());
        int[] result = new int[itemStacks.size()];
        for (int index = 0; index != itemStacks.size(); index++) {
            result[index] = copiedInv.addItem(null, itemStacks.get(index));
        }
        
        return result;
    }
    
    /**
     * Finds all {@link ItemStack}s similar to the provided {@link ItemStack} and removes them from
     * their slot until the amount of the given {@link ItemStack} reaches its maximum stack size.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param itemStack    The {@link ItemStack} to match against and to use for the base amount.
     * @return The amount of collected items plus the amount of the provided {@link ItemStack}.
     * At most the max stack size of the given {@link ItemStack}.
     */
    public int collectSimilar(@Nullable UpdateReason updateReason, ItemStack itemStack) {
        return collectSimilar(updateReason, itemStack, itemStack.getAmount());
    }
    
    /**
     * Finds all {@link ItemStack}s similar to the provided {@link ItemStack} and removes them from
     * their slot until the maximum stack size of the {@link Material} is reached.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param template     The {@link ItemStack} to match against.
     * @param baseAmount   The base item amount to assume. For example, with a base amount of 32 and a max stack size of 64,
     *                     this method will at most collect 32 other items.
     * @return The amount of collected items plus the base amount. At most the max stack size of the template {@link ItemStack}.
     */
    public int collectSimilar(@Nullable UpdateReason updateReason, ItemStack template, int baseAmount) {
        int amount = baseAmount;
        int maxStackSize = InventoryUtils.stackSizeProvider.getMaxStackSize(template);
        if (amount < maxStackSize) {
            // find partial slots and take items from there
            for (int partialSlot : findPartialSlots(template)) {
                amount += takeFrom(updateReason, partialSlot, maxStackSize - amount);
                if (amount == maxStackSize) return amount;
            }
            
            // only taking from partial stacks wasn't enough, take from a full slot
            for (int fullSlot : findFullSlots(template)) {
                amount += takeFrom(updateReason, fullSlot, maxStackSize - amount);
                if (amount == maxStackSize) return amount;
            }
        }
        
        return amount;
    }
    
    /**
     * Removes all {@link ItemStack ItemStacks} matching the given {@link Predicate}.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param predicate    The {@link Predicate} to use.
     * @return The amount of items that were removed.
     */
    public int removeIf(@Nullable UpdateReason updateReason, @NotNull Predicate<@NotNull ItemStack> predicate) {
        int removed = 0;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && predicate.test(item.clone()) && setItemStack(updateReason, slot, null)) {
                removed += item.getAmount();
            }
        }
        
        return removed;
    }
    
    /**
     * Removes the first n {@link ItemStack ItemStacks} matching the given {@link Predicate}.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param amount       The maximum amount of {@link ItemStack ItemStacks} to remove.
     * @param predicate    The {@link Predicate} to use.
     * @return The amount of items that were removed.
     */
    public int removeFirst(@Nullable UpdateReason updateReason, int amount, @NotNull Predicate<@NotNull ItemStack> predicate) {
        int leftOver = amount;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && predicate.test(item.clone())) {
                leftOver -= takeFrom(updateReason, slot, leftOver);
                if (leftOver == 0) return 0;
            }
        }
        
        return amount - leftOver;
    }
    
    /**
     * Removes all {@link ItemStack ItemStacks} that are similar to the specified {@link ItemStack}.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param itemStack    The {@link ItemStack} to match against.
     * @return The amount of items that were removed.
     */
    public int removeSimilar(@Nullable UpdateReason updateReason, @NotNull ItemStack itemStack) {
        int removed = 0;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && item.isSimilar(itemStack) && setItemStack(updateReason, slot, null)) {
                removed += item.getAmount();
            }
        }
        
        return removed;
    }
    
    /**
     * Removes the first n {@link ItemStack ItemStacks} that are similar to the specified {@link ItemStack}.
     *
     * @param updateReason The reason used in the {@link ItemUpdateEvent}.
     * @param amount       The maximum amount of {@link ItemStack ItemStacks} to remove.
     * @param itemStack    The {@link ItemStack} to match against.
     * @return The amount of items that were removed.
     */
    public int removeFirstSimilar(@Nullable UpdateReason updateReason, int amount, @NotNull ItemStack itemStack) {
        int leftOver = amount;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && item.isSimilar(itemStack)) {
                leftOver -= takeFrom(updateReason, slot, leftOver);
                if (leftOver == 0) return 0;
            }
        }
        
        return amount - leftOver;
    }
    
    private List<Integer> findPartialSlots(ItemStack itemStack) {
        List<Integer> partialSlots = new ArrayList<>();
        for (int slot = 0; slot < size; slot++) {
            ItemStack currentStack = items[slot];
            if (itemStack.isSimilar(currentStack)) {
                int maxStackSize = getMaxStackSize(slot);
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
                int maxStackSize = getMaxStackSize(slot);
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
        
        if (updateReason != UpdateReason.SUPPRESSED) {
            ItemUpdateEvent event = callPreUpdateEvent(updateReason, index, itemStack, newStack);
            if (!event.isCancelled()) {
                newStack = event.getNewItemStack();
                items[index] = newStack;
                notifyWindows();
                
                callAfterUpdateEvent(updateReason, index, itemStack, newStack);
                return itemStack.getAmount() - (newStack == null ? 0 : newStack.getAmount());
            }
        } else {
            items[index] = newStack;
            notifyWindows();
            return take;
        }
        
        return 0;
    }
    
    @Override
    public String toString() {
        return "VirtualInventory{" +
            "uuid=" + uuid +
            ", size=" + size +
            ", stackSizes=" + Arrays.toString(stackSizes) +
            ", items=" + Arrays.toString(items) +
            '}';
    }
    
}
