package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.util.ArrayUtils;
import xyz.xenondevs.invui.util.InventoryUtils;
import xyz.xenondevs.invui.util.ItemUtils;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * An inventory that can be embedded in InvUI's {@link Gui Guis}.
 * <p>
 * Provides several utility methods to easily add and remove items from the inventory, as well as an advanced
 * event system allowing to listen for and affect changes in the inventory.
 * <p>
 * General contracts of this class and all of its implementations are:
 * <ul>
 *     <li>
 *         There are no {@link ItemStack ItemStacks} of type {@link Material#AIR} or {@link ItemStack#getAmount()} == 0.
 *         <br>
 *         Empty {@link ItemStack ItemStacks} are represented by <code>null</code>.
 *     </li>
 *     <li>
 *         Unless otherwise specified, all methods will return a clone of the actual backing {@link ItemStack}.<br>
 *         Changes to returned {@link ItemStack ItemStacks} will never affect the {@link Inventory}.
 *     </li>
 *     <li>
 *         Unless otherwise specified, all methods accepting {@link ItemStack ItemStacks} will always clone them before
 *         putting them in the backing array / inventory.<br>
 *         Changes to the passed {@link ItemStack ItemStacks} after calling a method will never affect the {@link Inventory}.
 *     </li>
 * </ul>
 */
public abstract class Inventory {
    
    private final Set<AbstractWindow> windows = new HashSet<>();
    private Consumer<ItemPreUpdateEvent> preUpdateHandler;
    private Consumer<ItemPostUpdateEvent> postUpdateHandler;
    private int guiPriority = 0;
    
    /**
     * Gets the size of this {@link Inventory}.
     *
     * @return How many slots this {@link Inventory} has.
     */
    public abstract int getSize();
    
    /**
     * Gets the array of max stack sizes for this {@link Inventory}.
     *
     * @return The array defining the max stack sizes for this {@link Inventory}
     */
    public abstract int @NotNull [] getMaxStackSizes();
    
    /**
     * Gets the maximum stack size for a specific slot while ignoring max stack size of the {@link ItemStack} on it.
     *
     * @param slot The slot
     * @return The maximum stack size on that slo
     */
    public abstract int getMaxSlotStackSize(int slot);
    
    /**
     * Gets a copy of the contents of this {@link Inventory}.
     * <p>
     * It is guaranteed that this method will never return an air / empty item stack. Those are always represented by null.
     *
     * @return A deep copy of the {@link ItemStack ItemStacks} this {@link Inventory} contains.
     */
    public abstract @Nullable ItemStack @NotNull [] getItems();
    
    /**
     * Gets the {@link ItemStack ItemStacks} this {@link Inventory} contains.
     * Depending on the implementation, this method may return a copy, a deep copy, or the actual backing item stack array.
     * Modifying the returned array might or might not reflect in this {@link Inventory}.
     *
     * <p>
     * It is guaranteed that this method will never return an air / empty item stack. Those are always represented by null.
     *
     * @return The {@link ItemStack ItemStacks} this {@link Inventory} contains.
     */
    public abstract @Nullable ItemStack @NotNull [] getUnsafeItems();
    
    /**
     * Gets a clone of the {@link ItemStack} on that slot.
     * <p>
     * It is guaranteed that this method will never return an air / empty item stack. Those are always represented by null.
     *
     * @param slot The slot
     * @return The {@link ItemStack} on the given slot
     */
    public abstract @Nullable ItemStack getItem(int slot);
    
    /**
     * Gets the {@link ItemStack} on that slot. Depending on the implementation, this method may a copy of or the actual backing {@link ItemStack}.
     * <p>
     * It is guaranteed that this method will never return an air / empty item stack. Those are always represented by null.
     *
     * @param slot The slot
     * @return The {@link ItemStack} on the given slot.
     */
    public abstract @Nullable ItemStack getUnsafeItem(int slot);
    
    /**
     * Clones the given {@link ItemStack} and sets in the backing array of this {@link Inventory}.
     * <p>
     * This method should <strong>never</strong> be invoked with an air / empty item stack. Those should always be represented by null.
     *
     * @param slot      The slot
     * @param itemStack The {@link ItemStack} to be set
     */
    protected abstract void setCloneBackingItem(int slot, @Nullable ItemStack itemStack);
    
    /**
     * Sets the {@link ItemStack} in the backing array of this {@link Inventory} <strong>without explicitly cloning it</strong>.
     * Depending on the implementation, the {@link ItemStack} might still be cloned.
     * <p>
     * This method should <strong>never</strong> be invoked with an air / empty item stack. Those should always be represented by null.
     *
     * @param slot      The slot
     * @param itemStack The {@link ItemStack} to be set
     */
    protected abstract void setDirectBackingItem(int slot, @Nullable ItemStack itemStack);
    
    /**
     * Gets a set of {@link Window Windows} that display this {@link Inventory}.
     *
     * @return An unmodifiable view of the set that contains all {@link Window}s that display
     * content of this {@link Inventory}.
     */
    public @NotNull Set<@NotNull Window> getWindows() {
        return Collections.unmodifiableSet(windows);
    }
    
    /**
     * Adds an {@link AbstractWindow} to the set of {@link AbstractWindow AbstractWindows}, telling the {@link Inventory} that
     * its contents are now being displayed in that {@link AbstractWindow}.
     *
     * @param window The {@link Window} to be added.
     */
    public void addWindow(AbstractWindow window) {
        windows.add(window);
    }
    
    /**
     * Removes an {@link AbstractWindow} from the set of {@link AbstractWindow AbstractWindows}, telling the {@link Inventory} that
     * its contents are no longer being displayed in that {@link AbstractWindow}.
     *
     * @param window The {@link AbstractWindow} to be removed.
     */
    public void removeWindow(AbstractWindow window) {
        windows.remove(window);
    }
    
    /**
     * Notifies all {@link Window}s displaying this {@link Inventory} to update their
     * representative {@link ItemStack}s.
     * This method should only be called manually in very specific cases like when the
     * {@link ItemMeta} of an {@link ItemStack} in this inventory has changed.
     */
    public void notifyWindows() {
        windows.forEach(window -> window.handleInventoryUpdate(this));
    }
    
    /**
     * Gets the configured pre update handler.
     *
     * @return The pre update handler
     */
    public @Nullable Consumer<ItemPreUpdateEvent> getPreUpdateHandler() {
        return preUpdateHandler;
    }
    
    /**
     * Sets a handler which is called every time something gets updated in the {@link Inventory}.
     *
     * @param preUpdateHandler The new item update handler
     */
    public void setPreUpdateHandler(@NotNull Consumer<@NotNull ItemPreUpdateEvent> preUpdateHandler) {
        this.preUpdateHandler = preUpdateHandler;
    }
    
    /**
     * Gets the configured post update handler.
     *
     * @return The post update handler
     */
    public @Nullable Consumer<@NotNull ItemPostUpdateEvent> getPostUpdateHandler() {
        return postUpdateHandler;
    }
    
    /**
     * Sets a handler which is called every time after something has been updated in the {@link Inventory}.
     *
     * @param inventoryUpdatedHandler The new handler
     */
    public void setPostUpdateHandler(@NotNull Consumer<@NotNull ItemPostUpdateEvent> inventoryUpdatedHandler) {
        this.postUpdateHandler = inventoryUpdatedHandler;
    }
    
    /**
     * Creates an {@link ItemPreUpdateEvent} and calls the {@link #preUpdateHandler} to handle it.
     *
     * @param updateReason      The {@link UpdateReason}.
     * @param slot              The slot of the affected {@link ItemStack}.
     * @param previousItemStack The {@link ItemStack} that was previously on that slot.
     * @param newItemStack      The {@link ItemStack} that will be on that slot.
     * @return The {@link ItemPreUpdateEvent} after it has been handled by the {@link #preUpdateHandler}.
     */
    public ItemPreUpdateEvent callPreUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call ItemUpdateEvent with UpdateReason.SUPPRESSED");
        
        ItemPreUpdateEvent event = new ItemPreUpdateEvent(this, slot, updateReason, previousItemStack, newItemStack);
        if (preUpdateHandler != null) {
            try {
                preUpdateHandler.accept(event);
            } catch (Throwable t) {
                InvUI.getInstance().getLogger().log(Level.SEVERE, "An exception occurred while handling an inventory event", t);
            }
        }
        return event;
    }
    
    /**
     * Creates an {@link ItemPostUpdateEvent} and calls the {@link #postUpdateHandler} to handle it.
     *
     * @param updateReason      The {@link UpdateReason}.
     * @param slot              The slot of the affected {@link ItemStack}.
     * @param previousItemStack The {@link ItemStack} that was on that slot previously.
     * @param newItemStack      The {@link ItemStack} that is on that slot now.
     */
    public void callPostUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call InventoryUpdatedEvent with UpdateReason.SUPPRESSED");
        
        ItemPostUpdateEvent event = new ItemPostUpdateEvent(this, slot, updateReason, previousItemStack, newItemStack);
        if (postUpdateHandler != null) {
            try {
                postUpdateHandler.accept(event);
            } catch (Throwable t) {
                InvUI.getInstance().getLogger().log(Level.SEVERE, "An exception occurred while handling an inventory event", t);
            }
        }
    }
    
    /**
     * Gets the priority for click actions in a {@link Gui}, such as shift clicking or cursor collection
     * with multiple {@link Inventory VirtualInventories}.
     *
     * @return The priority for click actions, {@link Inventory VirtualInventories} with
     * a higher priority get prioritized.
     */
    public int getGuiPriority() {
        return guiPriority;
    }
    
    /**
     * Sets the priority for click actions in a {@link Gui}, such as shift-clicking or cursor collection
     * with multiple {@link Inventory VirtualInventories}.
     *
     * @param guiPriority The priority for click actions, {@link Inventory VirtualInventories} with
     *                    a higher priority get prioritized.
     */
    public void setGuiPriority(int guiPriority) {
        this.guiPriority = guiPriority;
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
        ItemStack currentItem = getUnsafeItem(slot);
        int slotMaxStackSize = getMaxSlotStackSize(slot);
        if (currentItem != null) {
            return Math.min(InventoryUtils.stackSizeProvider.getMaxStackSize(currentItem), slotMaxStackSize);
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
        ItemStack currentItem = getUnsafeItem(slot);
        int slotMaxStackSize = getMaxSlotStackSize(slot);
        return Math.min(currentItem != null ? InventoryUtils.stackSizeProvider.getMaxStackSize(currentItem) : alternative, slotMaxStackSize);
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
        return Math.min(alternative, getMaxSlotStackSize(slot));
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
     * Checks if the {@link ItemStack} on that slot is the same
     * as the assumed {@link ItemStack} provided as parameter.
     *
     * @param slot         The slot
     * @param assumedStack The assumed {@link ItemStack}
     * @return If the {@link ItemStack} on that slot is the same as the assumed {@link ItemStack}
     */
    public boolean isSynced(int slot, ItemStack assumedStack) {
        ItemStack actualStack = getUnsafeItem(slot);
        return Objects.equals(actualStack, assumedStack);
    }
    
    /**
     * Checks if all slots have an {@link ItemStack} with their max stack size on them.
     *
     * @return Whether this {@link Inventory} is full.
     */
    public boolean isFull() {
        ItemStack[] items = getUnsafeItems();
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item == null || item.getAmount() < getMaxStackSize(slot))
                return false;
        }
        
        return true;
    }
    
    /**
     * Checks if there are no {@link ItemStack ItemStacks} in this {@link Inventory}.
     *
     * @return Whether this {@link Inventory} is empty.
     */
    public boolean isEmpty() {
        for (ItemStack item : getUnsafeItems())
            if (item != null) return false;
        
        return true;
    }
    
    /**
     * Checks whether this {@link Inventory} has at least one empty slot.
     *
     * @return Whether this {@link Inventory} has at least one empty slot.
     */
    public boolean hasEmptySlot() {
        for (ItemStack item : getUnsafeItems())
            if (item == null) return true;
        
        return false;
    }
    
    /**
     * Checks if there is any {@link ItemStack} in this {@link Inventory} matching the given {@link Predicate}.
     *
     * @param predicate The {@link Predicate} to check.
     * @return Whether there is any {@link ItemStack} in this {@link Inventory} matching the given {@link Predicate}.
     */
    public boolean contains(Predicate<ItemStack> predicate) {
        for (ItemStack item : getUnsafeItems()) {
            if (item != null && predicate.test(item.clone()))
                return true;
        }
        
        return false;
    }
    
    /**
     * Checks if there is any {@link ItemStack} in this {@link Inventory} similar to the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to match against.
     * @return Whether there is any {@link ItemStack} in this {@link Inventory} similar to the given {@link ItemStack}.
     */
    public boolean containsSimilar(ItemStack itemStack) {
        for (ItemStack item : getUnsafeItems()) {
            if (item != null && item.isSimilar(itemStack))
                return true;
        }
        
        return false;
    }
    
    /**
     * Counts the amount of {@link ItemStack ItemStacks} in this {@link Inventory} matching the given {@link Predicate}.
     *
     * @param predicate The {@link Predicate} to check.
     * @return The amount of {@link ItemStack ItemStacks} in this {@link Inventory} matching the given {@link Predicate}.
     */
    public int count(Predicate<ItemStack> predicate) {
        int count = 0;
        for (ItemStack item : getUnsafeItems()) {
            if (item != null && predicate.test(item.clone()))
                count++;
        }
        
        return count;
    }
    
    /**
     * Counts the amount of {@link ItemStack ItemStacks} in this {@link Inventory} similar to the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to match against.
     * @return The amount of {@link ItemStack ItemStacks} in this {@link Inventory} similar to the given {@link ItemStack}.
     */
    public int countSimilar(ItemStack itemStack) {
        int count = 0;
        for (ItemStack item : getUnsafeItems()) {
            if (item != null && item.isSimilar(itemStack))
                count++;
        }
        
        return count;
    }
    
    /**
     * Checks if there is an {@link ItemStack} on that slot.
     *
     * @param slot The Slot
     * @return If there is an {@link ItemStack} on that slot.
     */
    public boolean hasItem(int slot) {
        return getUnsafeItem(slot) != null;
    }
    
    /**
     * Gets the {@link ItemStack#getAmount() ItemStack amount} of the {@link ItemStack} on the given slot.
     *
     * @param slot The slot
     * @return The amount of items on that slot
     */
    public int getItemAmount(int slot) {
        ItemStack currentStack = getUnsafeItem(slot);
        return currentStack != null ? currentStack.getAmount() : 0;
    }
    
    /**
     * Changes the {@link ItemStack} on a specific slot to that one, regardless of what was
     * previously on that slot.
     * <p>
     * This method does not call an {@link ItemPreUpdateEvent} and ignores the maximum allowed stack size of
     * both the {@link Material} and the slot.
     * <p>
     * This method will always be successful.
     *
     * @param slot      The slot
     * @param itemStack The {@link ItemStack} to set.
     */
    public void setItemSilently(int slot, @Nullable ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            itemStack = null;
        
        setCloneBackingItem(slot, itemStack);
        notifyWindows();
    }
    
    /**
     * Changes the {@link ItemStack} on a specific slot to that one, regardless of what was
     * previously on that slot.
     * <br>
     * This method ignores the maximum allowed stack size of both the {@link Material} and the slot.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param slot         The slot
     * @param itemStack    The {@link ItemStack} to set.
     * @return If the action was successful
     */
    public boolean forceSetItem(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack itemStack) {
        if (updateReason == UpdateReason.SUPPRESSED) {
            setItemSilently(slot, itemStack);
            return true;
        } else {
            ItemStack previousStack = getItem(slot);
            ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, previousStack, itemStack != null ? itemStack.clone() : null);
            if (!event.isCancelled()) {
                ItemStack newStack = event.getNewItem();
                setItemSilently(slot, newStack);
                callPostUpdateEvent(updateReason, slot, previousStack, newStack);
                return true;
            }
            return false;
        }
    }
    
    /**
     * Changes the {@link ItemStack} on a specific slot to the given one, regardless of what previously was on
     * that slot.
     * <br>
     * This method will fail if the given {@link ItemStack} does not completely fit because of the
     * maximum allowed stack size.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param slot         The slot
     * @param itemStack    The {@link ItemStack} to set.
     * @return If the action was successful
     */
    public boolean setItem(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return forceSetItem(updateReason, slot, null);
        
        int maxStackSize = getMaxSlotStackSize(slot, itemStack);
        if (itemStack.getAmount() > maxStackSize)
            return false;
        
        return forceSetItem(updateReason, slot, itemStack);
    }
    
    /**
     * Adds an {@link ItemStack} on a specific slot and returns the amount of items that did not fit on that slot.
     * <p>
     * This method will fail if there is an {@link ItemStack} on that slot that is not similar to the given one.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param slot         The slot
     * @param itemStack    The {@link ItemStack} to add.
     * @return The amount of items that did not fit on that slot.
     */
    public int putItem(@Nullable UpdateReason updateReason, int slot, @NotNull ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return 0;
        
        ItemStack currentStack = getUnsafeItem(slot);
        if (currentStack == null || currentStack.isSimilar(itemStack)) {
            int currentAmount = currentStack == null ? 0 : currentStack.getAmount();
            int maxStackSize = getMaxStackSize(slot, itemStack);
            if (currentAmount < maxStackSize) {
                int additionalAmount = itemStack.getAmount();
                int newAmount = Math.min(currentAmount + additionalAmount, maxStackSize);
                
                ItemStack newItemStack = itemStack.clone();
                newItemStack.setAmount(newAmount);
                
                if (updateReason != UpdateReason.SUPPRESSED) {
                    ItemStack currentStackC = currentStack != null ? currentStack.clone() : null;
                    ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStackC, newItemStack);
                    if (!event.isCancelled()) {
                        newItemStack = event.getNewItem();
                        setCloneBackingItem(slot, newItemStack);
                        notifyWindows();
                        
                        int newAmountEvent = newItemStack != null ? newItemStack.getAmount() : 0;
                        int remaining = itemStack.getAmount() - (newAmountEvent - currentAmount);
                        
                        callPostUpdateEvent(updateReason, slot, currentStackC, newItemStack);
                        
                        return remaining;
                    }
                } else {
                    setDirectBackingItem(slot, newItemStack); // already cloned above
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
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param slot         The slot
     * @param amount       The amount to change to.
     * @return The amount that it actually changed to.
     * @throws IllegalStateException If there is no {@link ItemStack} on that slot.
     */
    public int setItemAmount(@Nullable UpdateReason updateReason, int slot, int amount) {
        ItemStack currentStack = getUnsafeItem(slot);
        if (currentStack == null)
            throw new IllegalStateException("There is no ItemStack on that slot");
        
        int maxStackSize = getMaxStackSize(slot);
        
        ItemStack newItemStack;
        if (amount > 0) {
            newItemStack = currentStack.clone();
            newItemStack.setAmount(Math.min(amount, maxStackSize));
        } else {
            newItemStack = null;
        }
        
        if (updateReason != UpdateReason.SUPPRESSED) {
            ItemStack currentStackC = currentStack != null ? currentStack.clone() : null;
            ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStackC, newItemStack);
            if (!event.isCancelled()) {
                newItemStack = event.getNewItem();
                setCloneBackingItem(slot, newItemStack);
                notifyWindows();
                
                int actualAmount = newItemStack != null ? newItemStack.getAmount() : 0;
                
                callPostUpdateEvent(updateReason, slot, currentStackC, newItemStack);
                
                return actualAmount;
            }
        } else {
            setDirectBackingItem(slot, newItemStack); // already cloned above
            notifyWindows();
            return amount;
        }
        
        return currentStack.getAmount();
    }
    
    /**
     * Adds a specific amount to an {@link ItemStack} on a slot while respecting
     * the maximum allowed stack size on that slot.
     * Returns 0 if there is no {@link ItemStack} on that slot.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param slot         The slot
     * @param amount       The amount to add
     * @return The amount that was actually added.
     */
    public int addItemAmount(@Nullable UpdateReason updateReason, int slot, int amount) {
        ItemStack currentStack = getUnsafeItem(slot);
        if (currentStack == null)
            return 0;
        
        int currentAmount = currentStack.getAmount();
        return setItemAmount(updateReason, slot, currentAmount + amount) - currentAmount;
    }
    
    /**
     * Adds an {@link ItemStack} to the {@link Inventory}.
     * This method does not work the same way as Bukkit's addItem method
     * as it respects the max stack size of the item type.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param itemStack    The {@link ItemStack} to add
     * @return The amount of items that didn't fit
     * @see #simulateAdd(ItemStack, ItemStack...)
     */
    public int addItem(@Nullable UpdateReason updateReason, @NotNull ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return 0;
        
        final int originalAmount = itemStack.getAmount();
        int amountLeft = originalAmount;
        
        // find all slots where the item partially fits and add it there
        for (int partialSlot : findPartialSlots(itemStack)) {
            if (amountLeft == 0) break;
            
            ItemStack stackToPut = itemStack.clone();
            stackToPut.setAmount(amountLeft);
            amountLeft = putItem(updateReason, partialSlot, stackToPut);
            
        }
        
        // find all empty slots and put the item there
        for (int emptySlot : ArrayUtils.findEmptyIndices(getUnsafeItems())) {
            if (amountLeft == 0) break;
            
            ItemStack stackToPut = itemStack.clone();
            stackToPut.setAmount(amountLeft);
            amountLeft = putItem(updateReason, emptySlot, stackToPut);
        }
        
        // if items have been added, notify windows
        if (originalAmount != amountLeft) notifyWindows();
        
        // return how many items couldn't be added
        return amountLeft;
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link Inventory}
     * and returns the amount of {@link ItemStack}s that did not fit.
     *
     * @return An array of integers representing the leftover amount for each {@link ItemStack} provided.
     * The size of this array is always equal to the amount of {@link ItemStack}s provided as method parameters.
     */
    public int[] simulateAdd(@NotNull ItemStack itemStack, @NotNull ItemStack @NotNull ... itemStacks) {
        if (itemStacks.length == 0) {
            return new int[] {simulateSingleAdd(itemStack)};
        } else {
            ItemStack[] allStacks = Stream.concat(Stream.of(itemStack), Arrays.stream(itemStacks)).toArray(ItemStack[]::new);
            return simulateMultiAdd(Arrays.asList(allStacks));
        }
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link Inventory}
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
     * Simulates adding {@link ItemStack}s to this {@link Inventory}
     * and then returns if all {@link ItemStack}s would fit.
     *
     * @return If all provided {@link ItemStack}s would fit if added.
     */
    public boolean canHold(@NotNull ItemStack first, @NotNull ItemStack @NotNull ... rest) {
        if (rest.length == 0) {
            return simulateSingleAdd(first) == 0;
        } else {
            ItemStack[] allStacks = Stream.concat(Stream.of(first), Arrays.stream(rest)).toArray(ItemStack[]::new);
            return Arrays.stream(simulateMultiAdd(Arrays.asList(allStacks))).allMatch(i -> i == 0);
        }
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link Inventory}
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
     * <strong>Note: This method does not add any {@link ItemStack}s to the {@link Inventory}.</strong>
     *
     * @param itemStack The {@link ItemStack} to use
     * @return How many items wouldn't fit in the inventory when added
     */
    public int simulateSingleAdd(@NotNull ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return 0;
        
        int amountLeft = itemStack.getAmount();
        
        // find all slots where the item partially fits
        for (int partialSlot : findPartialSlots(itemStack)) {
            if (amountLeft == 0)
                break;
            
            ItemStack partialItem = getUnsafeItem(partialSlot);
            int maxStackSize = getMaxStackSize(partialSlot);
            amountLeft = Math.max(0, amountLeft - (maxStackSize - partialItem.getAmount()));
        }
        
        // remaining items would be added to empty slots
        for (int emptySlot : ArrayUtils.findEmptyIndices(getUnsafeItems())) {
            if (amountLeft == 0)
                break;
            
            int maxStackSize = getMaxStackSize(emptySlot, itemStack);
            amountLeft -= Math.min(amountLeft, maxStackSize);
        }
        
        return amountLeft;
    }
    
    /**
     * Simulates adding multiple {@link ItemStack}s to this {@link Inventory}
     * and returns the amount of {@link ItemStack}s that did not fit.<br>
     *
     * @param itemStacks The {@link ItemStack} to be used in the simulation
     * @return An array of integers representing the leftover amount for each {@link ItemStack} provided.
     * The size of this array is always equal to the amount of {@link ItemStack ItemStacks} provided as method parameters.
     */
    public int[] simulateMultiAdd(@NotNull List<@NotNull ItemStack> itemStacks) {
        Inventory copy = new VirtualInventory(null, getSize(), getItems(), getMaxStackSizes().clone());
        int[] result = new int[itemStacks.size()];
        for (int index = 0; index != itemStacks.size(); index++) {
            result[index] = copy.addItem(UpdateReason.SUPPRESSED, itemStacks.get(index));
        }
        
        return result;
    }
    
    /**
     * Finds all {@link ItemStack}s similar to the provided {@link ItemStack} and removes them from
     * their slot until the amount of the given {@link ItemStack} reaches its maximum stack size.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param itemStack    The {@link ItemStack} to match against and to use for the base amount.
     * @return The amount of collected items plus the amount of the provided {@link ItemStack}.
     * At most the max stack size of the given {@link ItemStack}.
     */
    public int collectSimilar(@Nullable UpdateReason updateReason, @NotNull ItemStack itemStack) {
        return collectSimilar(updateReason, itemStack, itemStack.getAmount());
    }
    
    /**
     * Finds all {@link ItemStack}s similar to the provided {@link ItemStack} and removes them from
     * their slot until the maximum stack size of the {@link Material} is reached.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param template     The {@link ItemStack} to match against.
     * @param baseAmount   The base item amount to assume. For example, with a base amount of 32 and a max stack size of 64,
     *                     this method will at most collect 32 other items.
     * @return The amount of collected items plus the base amount. At most the max stack size of the template {@link ItemStack}.
     */
    public int collectSimilar(@Nullable UpdateReason updateReason, @NotNull ItemStack template, int baseAmount) {
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
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param predicate    The {@link Predicate} to use.
     * @return The amount of items that were removed.
     */
    public int removeIf(@Nullable UpdateReason updateReason, @NotNull Predicate<@NotNull ItemStack> predicate) {
        ItemStack[] items = getUnsafeItems();
        
        int removed = 0;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && predicate.test(item.clone()) && setItem(updateReason, slot, null)) {
                removed += item.getAmount();
            }
        }
        
        return removed;
    }
    
    /**
     * Removes the first n {@link ItemStack ItemStacks} matching the given {@link Predicate}.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param amount       The maximum amount of {@link ItemStack ItemStacks} to remove.
     * @param predicate    The {@link Predicate} to use.
     * @return The amount of items that were removed.
     */
    public int removeFirst(@Nullable UpdateReason updateReason, int amount, @NotNull Predicate<@NotNull ItemStack> predicate) {
        ItemStack[] items = getUnsafeItems();
        
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
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param itemStack    The {@link ItemStack} to match against.
     * @return The amount of items that were removed.
     */
    public int removeSimilar(@Nullable UpdateReason updateReason, @NotNull ItemStack itemStack) {
        ItemStack[] items = getUnsafeItems();
        
        int removed = 0;
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack item = items[slot];
            if (item != null && item.isSimilar(itemStack) && setItem(updateReason, slot, null)) {
                removed += item.getAmount();
            }
        }
        
        return removed;
    }
    
    /**
     * Removes the first n {@link ItemStack ItemStacks} that are similar to the specified {@link ItemStack}.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param amount       The maximum amount of {@link ItemStack ItemStacks} to remove.
     * @param itemStack    The {@link ItemStack} to match against.
     * @return The amount of items that were removed.
     */
    public int removeFirstSimilar(@Nullable UpdateReason updateReason, int amount, @NotNull ItemStack itemStack) {
        ItemStack[] items = getUnsafeItems();
        
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
        ItemStack[] items = getUnsafeItems();
        
        List<Integer> partialSlots = new ArrayList<>();
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack currentStack = items[slot];
            if (itemStack.isSimilar(currentStack)) {
                int maxStackSize = getMaxStackSize(slot);
                if (currentStack.getAmount() < maxStackSize) partialSlots.add(slot);
            }
        }
        
        return partialSlots;
    }
    
    private List<Integer> findFullSlots(ItemStack itemStack) {
        ItemStack[] items = getUnsafeItems();
        
        List<Integer> fullSlots = new ArrayList<>();
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack currentStack = items[slot];
            if (itemStack.isSimilar(currentStack)) {
                int maxStackSize = getMaxStackSize(slot);
                if (currentStack.getAmount() == maxStackSize) fullSlots.add(slot);
            }
        }
        
        return fullSlots;
    }
    
    /**
     * Tries to take the specified amount of items from the specified slot.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent}.
     * @param slot         The slot to take from.
     * @param maxTake      The maximum amount of items to take.
     * @return The amount of items that were taken.
     */
    private int takeFrom(@Nullable UpdateReason updateReason, int slot, int maxTake) {
        ItemStack currentItemStack = getUnsafeItem(slot);
        int amount = currentItemStack.getAmount();
        int take = Math.min(amount, maxTake);
        
        ItemStack newItemStack;
        if (take != amount) {
            newItemStack = currentItemStack.clone();
            newItemStack.setAmount(amount - take);
        } else newItemStack = null;
        
        if (updateReason != UpdateReason.SUPPRESSED) {
            ItemStack currentItemStackC = currentItemStack.clone();
            ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentItemStackC, newItemStack);
            if (!event.isCancelled()) {
                newItemStack = event.getNewItem();
                setCloneBackingItem(slot, newItemStack);
                notifyWindows();
                
                int amountTaken = currentItemStack.getAmount() - (newItemStack == null ? 0 : newItemStack.getAmount());
                
                callPostUpdateEvent(updateReason, slot, currentItemStackC, newItemStack);
                
                return amountTaken;
            }
        } else {
            setDirectBackingItem(slot, newItemStack); // already cloned above
            notifyWindows();
            return take;
        }
        
        return 0;
    }
    
}
