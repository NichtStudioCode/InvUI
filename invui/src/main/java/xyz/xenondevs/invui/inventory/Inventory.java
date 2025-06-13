package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.ViewerAtSlot;
import xyz.xenondevs.invui.internal.util.ArrayUtils;
import xyz.xenondevs.invui.inventory.event.InventoryClickEvent;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.util.ItemUtils;
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.IntStream;

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
@SuppressWarnings("SynchronizeOnNonFinalField") // VirtualInventory synchronizes on viewers when changing the field
public sealed abstract class Inventory permits VirtualInventory, CompositeInventory, ObscuredInventory, ReferencingInventory {
    
    protected int size;
    protected @Nullable Set<ViewerAtSlot>[] viewers;
    private @Nullable List<Consumer<InventoryClickEvent>> clickHandlers;
    private @Nullable List<Consumer<ItemPreUpdateEvent>> preUpdateHandlers;
    private @Nullable List<Consumer<ItemPostUpdateEvent>> postUpdateHandlers;
    private int guiPriority = 0;
    private int[] iterationOrder;
    
    @SuppressWarnings("unchecked")
    public Inventory(int size) {
        this.size = size;
        viewers = new Set[size];
        iterationOrder = IntStream.range(0, size).toArray();
    }
    
    /**
     * Gets the size of this {@link Inventory}.
     *
     * @return How many slots this {@link Inventory} has.
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Sets the order in which slots are iterated over on methods that affect multiple slots,
     * such as {@link #addItem(UpdateReason, ItemStack)} or {@link #collectSimilar(UpdateReason, ItemStack, int)}.
     *
     * @param iterationOrder The new iteration order. Must include all slots and no duplicates.
     */
    public void setIterationOrder(int[] iterationOrder) {
        if (iterationOrder.length != size)
            throw new IllegalArgumentException("Iteration order size must match inventory size");
        
        var includedSlots = new BitSet(size);
        for (var slot : getIterationOrder()) {
            includedSlots.set(slot);
        }
        
        if (includedSlots.nextClearBit(0) != size)
            throw new IllegalArgumentException("Iteration order must include all slots");
        
        this.iterationOrder = iterationOrder;
    }
    
    /**
     * Gets a copy of the order in which slots are iterated over on methods that affect multiple slots,
     * such as {@link #addItem(UpdateReason, ItemStack)} or {@link #collectSimilar(UpdateReason, ItemStack, int)}.
     *
     * @return The current iteration order.
     */
    public int[] getIterationOrder() {
        return iterationOrder.clone();
    }
    
    /**
     * Reverses the order in which slots are iterated over on methods that affect multiple slots,
     * such as {@link #addItem(UpdateReason, ItemStack)} or {@link #collectSimilar(UpdateReason, ItemStack, int)}.
     */
    public void reverseIterationOrder() {
        setIterationOrder(ArrayUtils.reversed(iterationOrder));
    }
    
    /**
     * Gets the array of max stack sizes for this {@link Inventory}.
     *
     * @return The array defining the max stack sizes for this {@link Inventory}
     */
    public abstract int[] getMaxStackSizes();
    
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
    public abstract @Nullable ItemStack[] getItems();
    
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
    public abstract @Nullable ItemStack[] getUnsafeItems();
    
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
     * @hidden
     */
    @ApiStatus.Internal
    public void addViewer(AbstractWindow<?> viewer, int what, int how) {
        synchronized (viewers) {
            var viewerSet = viewers[what];
            if (viewerSet == null) {
                viewerSet = new HashSet<>();
                viewers[what] = viewerSet;
            }
            
            viewerSet.add(new ViewerAtSlot(viewer, how));
        }
    }
    
    /**
     * @hidden
     */
    @ApiStatus.Internal
    public void removeViewer(AbstractWindow<?> viewer, int what, int how) {
        synchronized (viewers) {
            var viewerSet = viewers[what];
            if (viewerSet != null) {
                viewerSet.remove(new ViewerAtSlot(viewer, how));
                if (viewerSet.isEmpty())
                    viewers[what] = null;
            }
        }
    }
    
    /**
     * Gets all {@link Window Windows} displaying this {@link Inventory}.
     *
     * @return A list of all {@link Window Windows} displaying this {@link Inventory}.
     */
    public List<Window> getWindows() {
        var windows = new ArrayList<Window>();
        synchronized (viewers) {
            for (var viewerSet : viewers) {
                if (viewerSet == null)
                    continue;
                for (var viewerAtSlot : viewerSet) {
                    windows.add(viewerAtSlot.window());
                }
            }
        }
        return windows;
    }
    
    /**
     * Notifies all {@link Window Windows} displaying this {@link Inventory} to update their
     * representative {@link ItemStack ItemStacks}.
     * <p>
     * Can be called asynchronously.
     */
    public void notifyWindows() {
        synchronized (viewers) {
            for (var viewerSet : viewers) {
                if (viewerSet == null)
                    continue;
                for (var viewerAtSlot : viewerSet) {
                    viewerAtSlot.notifyUpdate();
                }
            }
        }
    }
    
    /**
     * Notifies all {@link Window Windows} displaying the given slot of this inventory
     * to update their representative {@link ItemStack ItemStacks}.
     * <p>
     * Can be called asynchronously.
     *
     * @param slot The slot to notify
     */
    public void notifyWindows(int slot) {
        synchronized (viewers) {
            var viewerSet = viewers[slot];
            if (viewerSet != null) {
                for (var viewerAtSlot : viewerSet) {
                    viewerAtSlot.notifyUpdate();
                }
            }
        }
    }
    
    /**
     * Gets all registered click handlers of this {@link Inventory}.
     *
     * @return The click handlers
     */
    public List<Consumer<InventoryClickEvent>> getClickHandlers() {
        if (clickHandlers == null)
            return List.of();
        
        return Collections.unmodifiableList(clickHandlers);
    }
    
    /**
     * Sets the click handlers of this {@link Inventory}.
     * Handlers may cancel the event. If it is, the click will not be processed further.
     *
     * @param clickHandlers The click handlers
     */
    public void setClickHandlers(List<Consumer<InventoryClickEvent>> clickHandlers) {
        this.clickHandlers = new ArrayList<>(clickHandlers);
    }
    
    /**
     * Registers a click handler for this {@link Inventory}.
     * Handlers may cancel the event. If it is, the click will not be processed further.
     *
     * @param clickHandler The click handler
     */
    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        if (clickHandlers == null)
            clickHandlers = new ArrayList<>();
        
        clickHandlers.add(clickHandler);
    }
    
    /**
     * Removes a click handler that was previously registered for this {@link Inventory}.
     *
     * @param clickHandler The click handler to remove
     */
    public void removeClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        if (clickHandlers != null)
            clickHandlers.remove(clickHandler);
    }
    
    /**
     * Gets the pre update handlers of this {@link Inventory}.
     *
     * @return The pre update handlers
     */
    public List<Consumer<ItemPreUpdateEvent>> getPreUpdateHandlers() {
        if (preUpdateHandlers == null)
            return List.of();
        
        return Collections.unmodifiableList(preUpdateHandlers);
    }
    
    /**
     * Sets the pre update handlers of this {@link Inventory}.
     *
     * @param preUpdateHandlers The pre update handlers
     */
    public void setPreUpdateHandlers(List<Consumer<ItemPreUpdateEvent>> preUpdateHandlers) {
        this.preUpdateHandlers = preUpdateHandlers;
    }
    
    /**
     * Adds a pre update handler for this {@link Inventory}.
     *
     * @param preUpdateHandler The pre update handler
     */
    public void addPreUpdateHandler(Consumer<ItemPreUpdateEvent> preUpdateHandler) {
        if (preUpdateHandlers == null)
            preUpdateHandlers = new ArrayList<>();
        
        preUpdateHandlers.add(preUpdateHandler);
    }
    
    /**
     * Removes a pre update handler that was previously registered for this {@link Inventory}.
     *
     * @param preUpdateHandler The pre update handler to remove
     */
    public void removePreUpdateHandler(Consumer<ItemPreUpdateEvent> preUpdateHandler) {
        if (preUpdateHandlers != null)
            preUpdateHandlers.remove(preUpdateHandler);
    }
    
    /**
     * Gets the post update handlers of this {@link Inventory}.
     *
     * @return The post update handlers
     */
    public List<Consumer<ItemPostUpdateEvent>> getPostUpdateHandlers() {
        if (postUpdateHandlers == null)
            return List.of();
        
        return Collections.unmodifiableList(postUpdateHandlers);
    }
    
    /**
     * Sets the post update handlers of this {@link Inventory}.
     *
     * @param postUpdateHandlers The post update handlers
     */
    public void setPostUpdateHandlers(List<Consumer<ItemPostUpdateEvent>> postUpdateHandlers) {
        this.postUpdateHandlers = postUpdateHandlers;
    }
    
    /**
     * Adds a post update handler for this {@link Inventory}.
     *
     * @param postUpdateHandler The post update handler
     */
    public void addPostUpdateHandler(Consumer<ItemPostUpdateEvent> postUpdateHandler) {
        if (postUpdateHandlers == null)
            postUpdateHandlers = new ArrayList<>();
        
        postUpdateHandlers.add(postUpdateHandler);
    }
    
    /**
     * Removes a post update handler that was previously registered for this {@link Inventory}.
     *
     * @param postUpdateHandler The post update handler to remove
     */
    public void removePostUpdateHandler(Consumer<ItemPostUpdateEvent> postUpdateHandler) {
        if (postUpdateHandlers != null)
            postUpdateHandlers.remove(postUpdateHandler);
    }
    
    /**
     * Whether this {@link Inventory} has any event handlers.
     *
     * @return `true` if this {@link Inventory} has pre- or post-update handlers.
     */
    public boolean hasEventHandlers() {
        return preUpdateHandlers != null && !preUpdateHandlers.isEmpty()
               || postUpdateHandlers != null && !postUpdateHandlers.isEmpty();
    }
    
    /**
     * Whether events should be called for this {@link Inventory}.
     *
     * @param updateReason The {@link UpdateReason} for the event.
     * @return `true` if events should be called for this {@link Inventory} in this case.
     */
    private boolean shouldCallEvents(@Nullable UpdateReason updateReason) {
        return hasEventHandlers() && updateReason != UpdateReason.SUPPRESSED;
    }
    
    /**
     * Calls the click handlers of this {@link Inventory} for the given slot and {@link Click}.
     *
     * @param slot  The slot of this {@link Inventory} that was clicked.
     * @param click The {@link Click} that occurred.
     * @return Whether the click event was cancelled.
     */
    public boolean callClickEvent(int slot, Click click) {
        var clickEvent = new InventoryClickEvent(this, slot, click);
        for (var handler : getClickHandlers()) {
            try {
                handler.accept(clickEvent);
            } catch (Throwable t) {
                InvUI.getInstance().getLogger().log(Level.SEVERE, "An exception occurred while handling an inventory event", t);
            }
        }
        
        return clickEvent.isCancelled();
    }
    
    /**
     * Creates an {@link ItemPreUpdateEvent} and calls the pre update handlers to handle it.
     *
     * @param updateReason      The {@link UpdateReason}.
     * @param slot              The slot of the affected {@link ItemStack}.
     * @param previousItemStack The {@link ItemStack} that was previously on that slot.
     *                          Will be cloned to prevent modifications.
     * @param newItemStack      The {@link ItemStack} that will be on that slot.
     *                          Will be cloned to prevent modifications.
     * @return The {@link ItemPreUpdateEvent} after it has been handled by the pre update handlers.
     */
    public ItemPreUpdateEvent callPreUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call ItemUpdateEvent with UpdateReason.SUPPRESSED");
        
        ItemPreUpdateEvent event = new ItemPreUpdateEvent(this, slot, updateReason, previousItemStack, newItemStack);
        for (var handler : getPreUpdateHandlers()) {
            try {
                handler.accept(event);
            } catch (Throwable t) {
                InvUI.getInstance().getLogger().log(Level.SEVERE, "An exception occurred while handling an inventory event", t);
            }
        }
        return event;
    }
    
    /**
     * Creates an {@link ItemPostUpdateEvent} and calls the post update handlers to handle it.
     *
     * @param updateReason      The {@link UpdateReason}.
     * @param slot              The slot of the affected {@link ItemStack}.
     * @param previousItemStack The {@link ItemStack} that was on that slot previously.
     *                          Will be cloned to prevent modifications.
     * @param newItemStack      The {@link ItemStack} that is on that slot now.
     *                          Will be cloned to prevent modifications.
     */
    public void callPostUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        if (updateReason == UpdateReason.SUPPRESSED)
            throw new IllegalArgumentException("Cannot call InventoryUpdatedEvent with UpdateReason.SUPPRESSED");
        
        ItemPostUpdateEvent event = new ItemPostUpdateEvent(this, slot, updateReason, previousItemStack, newItemStack);
        for (var handler : getPostUpdateHandlers()) {
            try {
                handler.accept(event);
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
     * size and the {@link ItemStack ItemStack's} max stack size.
     *
     * @param slot The slot
     * @return The current maximum allowed stack size on the specific slot.
     */
    public int getMaxStackSize(int slot) {
        ItemStack currentItem = getUnsafeItem(slot);
        int slotMaxStackSize = getMaxSlotStackSize(slot);
        if (currentItem != null) {
            return Math.min(currentItem.getMaxStackSize(), slotMaxStackSize);
        } else {
            return slotMaxStackSize;
        }
    }
    
    /**
     * Gets the maximum stack size for a specific slot.
     * <p>
     * If there is an {@link ItemStack} on that slot, the returned value will be the minimum of both the slot's
     * max stack size and the {@link ItemStack ItemStack's} max stack size.
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
        return Math.min(currentItem != null ? currentItem.getMaxStackSize() : alternative, slotMaxStackSize);
    }
    
    /**
     * Gets the maximum stack size for a specific slot. If there is an {@link ItemStack} on that slot,
     * the returned value will be the minimum of both the slot's and the {@link ItemStack ItemStack's} max stack size.
     * If there is no {@link ItemStack} on that slot, the alternativeFrom
     * parameter will be used to determine a potential maximum stack size.
     *
     * @param slot            The slot
     * @param alternativeFrom The alternative {@link ItemStack} to determine the potential maximum stack size. Uses 64 if null.
     * @return The current maximum allowed stack size on the specific slot.
     */
    public int getMaxStackSize(int slot, @Nullable ItemStack alternativeFrom) {
        int itemMaxStackSize = alternativeFrom == null ? 64 : alternativeFrom.getMaxStackSize();
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
        int itemMaxStackSize = alternativeFrom == null ? 64 : alternativeFrom.getMaxStackSize();
        return getMaxSlotStackSize(slot, itemMaxStackSize);
    }
    
    /**
     * Checks if all slots have an {@link ItemStack} with their max stack size on them.
     *
     * @return Whether this {@link Inventory} is full.
     */
    public boolean isFull() {
        @Nullable ItemStack[] items = getUnsafeItems();
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
    private void setItemSilently(int slot, @Nullable ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            itemStack = null;
        
        setCloneBackingItem(slot, itemStack);
        notifyWindows(slot);
    }
    
    /**
     * Changes the {@link ItemStack} on a specific slot to that one, regardless of what was previously on that slot and
     * ignoring the maximum allowed stack size of both the slot and the {@link ItemStack} on it.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param slot         The slot
     * @param itemStack    The {@link ItemStack} to set.
     * @return If the action was successful
     */
    public boolean forceSetItem(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack itemStack) {
        if (!shouldCallEvents(updateReason)) {
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
     * Changes the {@link ItemStack} on a specific slot to that one, regardless of what previously was on that slot.
     * <br>
     * This method will fail if the given {@link ItemStack} does not completely fit because of the maximum allowed
     * stack size of either the slot or the {@link ItemStack} on it.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
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
     * Tries to change the {@link ItemStack} on a specific slot to that one, regardless of what was on that slot,
     * then returns the {@link ItemStack} that is actually on that slot now.
     * <br>
     * This method will fail if the given {@link ItemStack} does not completely fit because of the maximum allowed
     * stack size of either the slot or the {@link ItemStack} on it.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param slot         The slot
     * @param itemStack    The {@link ItemStack} to set.
     * @return The {@link ItemStack} that is actually on that slot now.
     */
    public @Nullable ItemStack changeItem(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            itemStack = null;
        
        int maxStackSize = getMaxSlotStackSize(slot, itemStack);
        if (itemStack != null && itemStack.getAmount() > maxStackSize)
            return getItem(slot);
        
        if (!shouldCallEvents(updateReason)) {
            setItemSilently(slot, itemStack);
            return ItemUtils.cloneUnlessEmpty(itemStack);
        } else {
            ItemStack previousStack = getItem(slot);
            ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, previousStack, itemStack);
            if (!event.isCancelled()) {
                ItemStack newStack = event.getNewItem();
                setItemSilently(slot, newStack);
                callPostUpdateEvent(updateReason, slot, previousStack, newStack);
                return ItemUtils.cloneUnlessEmpty(event.getNewItem());
            }
            return getItem(slot);
        }
    }
    
    /**
     * Changes the {@link ItemStack} on a specific slot based on the current {@link ItemStack} on that slot,
     * using a modifier consumer.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param slot         The slot.
     * @param modifier     The modifier consumer. Accepts the current {@link ItemStack} and modifies it.
     * @return If the action was successful.
     */
    public boolean modifyItem(@Nullable UpdateReason updateReason, int slot, Consumer<@Nullable ItemStack> modifier) {
        ItemStack itemStack = getItem(slot);
        modifier.accept(itemStack);
        return setItem(updateReason, slot, itemStack);
    }
    
    /**
     * Replaces the {@link ItemStack} on a specific slot based on the current {@link ItemStack} on that slot,
     * using a replace function.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param slot         The slot.
     * @param function     The replace function. The argument is the current {@link ItemStack},
     *                     the return value is the new {@link ItemStack}.
     * @return If the action was successful.
     */
    public boolean changeItem(@Nullable UpdateReason updateReason, int slot, Function<@Nullable ItemStack, @Nullable ItemStack> function) {
        ItemStack currentStack = getItem(slot);
        ItemStack newStack = function.apply(currentStack);
        return setItem(updateReason, slot, newStack);
    }
    
    /**
     * Adds an {@link ItemStack} on a specific slot and returns the amount of items that did not fit on that slot.
     * <p>
     * This method will fail if there is an {@link ItemStack} on that slot that is not similar to the given one.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param slot         The slot
     * @param itemStack    The {@link ItemStack} to add.
     * @return The amount of items that did not fit on that slot.
     */
    public int putItem(@Nullable UpdateReason updateReason, int slot, ItemStack itemStack) {
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
                
                if (shouldCallEvents(updateReason)) {
                    ItemStack currentStackC = currentStack != null ? currentStack.clone() : null;
                    ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStackC, newItemStack);
                    if (!event.isCancelled()) {
                        newItemStack = event.getNewItem();
                        setCloneBackingItem(slot, newItemStack);
                        notifyWindows(slot);
                        
                        int newAmountEvent = newItemStack != null ? newItemStack.getAmount() : 0;
                        int remaining = itemStack.getAmount() - (newAmountEvent - currentAmount);
                        
                        callPostUpdateEvent(updateReason, slot, currentStackC, newItemStack);
                        
                        return remaining;
                    }
                } else {
                    setDirectBackingItem(slot, newItemStack); // already cloned above
                    notifyWindows(slot);
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
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
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
        
        if (shouldCallEvents(updateReason)) {
            ItemStack currentStackC = currentStack.clone();
            ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStackC, newItemStack);
            if (!event.isCancelled()) {
                newItemStack = event.getNewItem();
                setCloneBackingItem(slot, newItemStack);
                notifyWindows(slot);
                
                int actualAmount = newItemStack != null ? newItemStack.getAmount() : 0;
                
                callPostUpdateEvent(updateReason, slot, currentStackC, newItemStack);
                
                return actualAmount;
            }
        } else {
            setDirectBackingItem(slot, newItemStack); // already cloned above
            notifyWindows(slot);
            return amount;
        }
        
        return currentStack.getAmount();
    }
    
    /**
     * Adds a specific amount to an {@link ItemStack} on a slot while respecting
     * the maximum allowed stack size on that slot.
     * Returns 0 if there is no {@link ItemStack} on that slot.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
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
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param itemStack    The {@link ItemStack} to add
     * @return The amount of items that didn't fit
     * @see #simulateAdd(ItemStack, ItemStack...)
     */
    public int addItem(@Nullable UpdateReason updateReason, ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return 0;
        
        @Nullable ItemStack[] items = getUnsafeItems();
        
        int amountLeft = itemStack.getAmount();
        
        amountLeft = addToPartialSlots(updateReason, itemStack, amountLeft, items);
        amountLeft = addToEmptySlots(updateReason, itemStack, amountLeft, items);
        
        return amountLeft;
    }
    
    private int addToPartialSlots(
        @Nullable UpdateReason updateReason,
        ItemStack itemStack,
        int amountLeft,
        @Nullable ItemStack[] items
    ) {
        for (int slot : getIterationOrder()) {
            if (amountLeft <= 0)
                break;
            
            ItemStack currentStack = items[slot];
            if (currentStack == null)
                continue;
            int maxStackSize = getMaxSlotStackSize(slot, itemStack);
            if (currentStack.getAmount() >= maxStackSize)
                continue;
            if (!itemStack.isSimilar(currentStack))
                continue;
            
            // partial stack found, put items
            ItemStack newStack = itemStack.clone();
            newStack.setAmount(Math.min(currentStack.getAmount() + amountLeft, maxStackSize));
            if (shouldCallEvents(updateReason)) {
                ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentStack.clone(), newStack);
                if (!event.isCancelled()) {
                    newStack = event.getNewItem();
                    setCloneBackingItem(slot, newStack);
                    notifyWindows(slot);
                    callPostUpdateEvent(updateReason, slot, currentStack.clone(), newStack);
                    
                    int newStackAmount = newStack != null ? newStack.getAmount() : 0;
                    amountLeft -= newStackAmount - currentStack.getAmount();
                }
            } else {
                setDirectBackingItem(slot, newStack);
                notifyWindows(slot);
                amountLeft -= newStack.getAmount() - currentStack.getAmount();
            }
        }
        
        return amountLeft;
    }
    
    private int addToEmptySlots(
        @Nullable UpdateReason updateReason,
        ItemStack itemStack,
        int amountLeft,
        @Nullable ItemStack[] items
    ) {
        for (int slot : getIterationOrder()) {
            if (amountLeft <= 0)
                break;
            
            if (items[slot] != null)
                continue;
            
            // empty slot found, put items
            ItemStack newStack = itemStack.clone();
            newStack.setAmount(Math.min(amountLeft, getMaxSlotStackSize(slot, itemStack)));
            if (shouldCallEvents(updateReason)) {
                ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, null, newStack);
                if (!event.isCancelled()) {
                    newStack = event.getNewItem();
                    setCloneBackingItem(slot, newStack);
                    notifyWindows(slot);
                    callPostUpdateEvent(updateReason, slot, null, newStack);
                    
                    int newStackAmount = newStack != null ? newStack.getAmount() : 0;
                    amountLeft -= newStackAmount;
                }
            } else {
                setDirectBackingItem(slot, newStack);
                notifyWindows(slot);
                amountLeft -= newStack.getAmount();
            }
        }
        
        return amountLeft;
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link Inventory}
     * and returns the amount of {@link ItemStack}s that did not fit.
     *
     * @param first The first {@link ItemStack} to use.
     * @param rest  The rest of the {@link ItemStack ItemStacks} to use.
     * @return An array of integers representing the leftover amount for each {@link ItemStack} provided.
     * The size of this array is always equal to the amount of {@link ItemStack}s provided as method parameters.
     */
    public int[] simulateAdd(ItemStack first, ItemStack... rest) {
        if (rest.length == 0) {
            return new int[] {simulateSingleAdd(first)};
        } else {
            ItemStack[] allStacks = ArrayUtils.concat(ItemStack[]::new, first, rest);
            return simulateMultiAdd(Arrays.asList(allStacks));
        }
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link Inventory}
     * and returns the amount of {@link ItemStack}s that did not fit.
     *
     * @param itemStacks The {@link ItemStack} to use.
     * @return An array of integers representing the leftover amount for each {@link ItemStack} provided.
     * The size of this array is always equal to the amount of {@link ItemStack}s provided as method parameters.
     */
    public int[] simulateAdd(List<ItemStack> itemStacks) {
        if (itemStacks.isEmpty())
            return new int[0];
        
        if (itemStacks.size() == 1) {
            return new int[] {simulateSingleAdd(itemStacks.getFirst())};
        } else {
            return simulateMultiAdd(itemStacks);
        }
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link Inventory}
     * and then returns if all {@link ItemStack}s would fit.
     *
     * @param first The first {@link ItemStack} to use.
     * @param rest  The rest of the {@link ItemStack ItemStacks} to use.
     * @return If all provided {@link ItemStack}s would fit if added.
     */
    public boolean canHold(ItemStack first, ItemStack... rest) {
        if (rest.length == 0) {
            return simulateSingleAdd(first) == 0;
        } else {
            ItemStack[] allStacks = ArrayUtils.concat(ItemStack[]::new, first, rest);
            return Arrays.stream(simulateMultiAdd(Arrays.asList(allStacks))).allMatch(i -> i == 0);
        }
    }
    
    /**
     * Simulates adding {@link ItemStack}s to this {@link Inventory}
     * and then returns if all {@link ItemStack}s would fit.
     *
     * @param itemStacks The {@link ItemStack} to use.
     * @return If all provided {@link ItemStack}s would fit if added.
     */
    public boolean canHold(List<ItemStack> itemStacks) {
        if (itemStacks.isEmpty()) return true;
        
        if (itemStacks.size() == 1) {
            return simulateSingleAdd(itemStacks.getFirst()) == 0;
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
    public int simulateSingleAdd(@Nullable ItemStack itemStack) {
        if (ItemUtils.isEmpty(itemStack))
            return 0;
        
        @Nullable ItemStack[] items = getUnsafeItems();
        int amountLeft = itemStack.getAmount();
        
        // find all slots where the item partially fits
        for (int slot : getIterationOrder()) {
            if (amountLeft == 0)
                break;
            
            ItemStack currentStack = items[slot];
            if (currentStack == null)
                continue;
            int maxStackSize = getMaxSlotStackSize(slot, itemStack);
            if (currentStack.getAmount() >= maxStackSize)
                continue;
            if (!itemStack.isSimilar(currentStack))
                continue;
            
            amountLeft = Math.max(0, amountLeft - (maxStackSize - currentStack.getAmount()));
        }
        
        // remaining items would be added to empty slots
        for (int slot : getIterationOrder()) {
            if (amountLeft == 0)
                break;
            
            if (items[slot] != null)
                continue;
            
            int maxStackSize = getMaxStackSize(slot, itemStack);
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
    public int[] simulateMultiAdd(List<ItemStack> itemStacks) {
        Inventory copy = new VirtualInventory(null, getSize(), getItems(), getMaxStackSizes().clone());
        int[] result = new int[itemStacks.size()];
        for (int i = 0; i < itemStacks.size(); i++) {
            result[i] = copy.addItem(UpdateReason.SUPPRESSED, itemStacks.get(i));
        }
        
        return result;
    }
    
    /**
     * Finds all {@link ItemStack}s similar to the provided {@link ItemStack} and removes them from
     * their slot until the amount of the given {@link ItemStack} reaches its maximum stack size.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
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
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param template     The {@link ItemStack} to match against.
     * @param baseAmount   The base item amount to assume. For example, with a base amount of 32 and a max stack size of 64,
     *                     this method will at most collect 32 other items.
     * @return The amount of collected items plus the base amount. At most the max stack size of the template {@link ItemStack}.
     */
    public int collectSimilar(@Nullable UpdateReason updateReason, ItemStack template, int baseAmount) {
        int amount = baseAmount;
        int maxStackSize = template.getMaxStackSize();
        if (amount < maxStackSize) {
            @Nullable ItemStack[] items = getUnsafeItems();
            
            // find partial slots and take items from there
            for (int slot : getIterationOrder()) {
                ItemStack currentStack = items[slot];
                if (currentStack == null || currentStack.getAmount() >= maxStackSize || !template.isSimilar(currentStack))
                    continue;
                
                amount += takeFrom(updateReason, slot, maxStackSize - amount);
                if (amount == maxStackSize)
                    return amount;
            }
            
            // only taking from partial stacks wasn't enough, take from a full slot
            for (int slot : getIterationOrder()) {
                ItemStack currentStack = items[slot];
                if (currentStack == null || currentStack.getAmount() < maxStackSize || !template.isSimilar(currentStack))
                    continue;
                
                amount += takeFrom(updateReason, slot, maxStackSize - amount);
                if (amount == maxStackSize)
                    return amount;
            }
        }
        
        return amount;
    }
    
    /**
     * Removes all {@link ItemStack ItemStacks} matching the given {@link Predicate}.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param predicate    The {@link Predicate} to use.
     * @return The amount of items that were removed.
     */
    public int removeIf(@Nullable UpdateReason updateReason, Predicate<ItemStack> predicate) {
        @Nullable ItemStack[] items = getUnsafeItems();
        
        int removed = 0;
        for (int slot : getIterationOrder()) {
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
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param amount       The maximum amount of {@link ItemStack ItemStacks} to remove.
     * @param predicate    The {@link Predicate} to use.
     * @return The amount of items that were removed.
     */
    public int removeFirst(@Nullable UpdateReason updateReason, int amount, Predicate<ItemStack> predicate) {
        @Nullable ItemStack[] items = getUnsafeItems();
        
        int leftOver = amount;
        for (int slot : getIterationOrder()) {
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
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param itemStack    The {@link ItemStack} to match against.
     * @return The amount of items that were removed.
     */
    public int removeSimilar(@Nullable UpdateReason updateReason, ItemStack itemStack) {
        @Nullable ItemStack[] items = getUnsafeItems();
        
        int removed = 0;
        for (int slot : getIterationOrder()) {
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
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param amount       The maximum amount of {@link ItemStack ItemStacks} to remove.
     * @param itemStack    The {@link ItemStack} to match against.
     * @return The amount of items that were removed.
     */
    public int removeFirstSimilar(@Nullable UpdateReason updateReason, int amount, ItemStack itemStack) {
        @Nullable ItemStack[] items = getUnsafeItems();
        
        int leftOver = amount;
        for (int slot : getIterationOrder()) {
            ItemStack item = items[slot];
            if (item != null && item.isSimilar(itemStack)) {
                leftOver -= takeFrom(updateReason, slot, leftOver);
                if (leftOver == 0) return 0;
            }
        }
        
        return amount - leftOver;
    }
    
    /**
     * Tries to take the specified amount of items from the specified slot.
     *
     * @param updateReason The reason used in the {@link ItemPreUpdateEvent} and {@link ItemPostUpdateEvent}.
     * @param slot         The slot to take from.
     * @param maxTake      The maximum amount of items to take.
     * @return The amount of items that were taken.
     */
    private int takeFrom(@Nullable UpdateReason updateReason, int slot, int maxTake) {
        ItemStack currentItemStack = getUnsafeItem(slot);
        if (currentItemStack == null)
            return 0;
        
        int amount = currentItemStack.getAmount();
        int take = Math.min(amount, maxTake);
        
        ItemStack newItemStack;
        if (take != amount) {
            newItemStack = currentItemStack.clone();
            newItemStack.setAmount(amount - take);
        } else newItemStack = null;
        
        if (shouldCallEvents(updateReason)) {
            ItemStack currentItemStackC = currentItemStack.clone();
            ItemPreUpdateEvent event = callPreUpdateEvent(updateReason, slot, currentItemStackC, newItemStack);
            if (!event.isCancelled()) {
                newItemStack = event.getNewItem();
                setCloneBackingItem(slot, newItemStack);
                notifyWindows(slot);
                
                int amountTaken = currentItemStack.getAmount() - (newItemStack == null ? 0 : newItemStack.getAmount());
                
                callPostUpdateEvent(updateReason, slot, currentItemStackC, newItemStack);
                
                return amountTaken;
            }
        } else {
            setDirectBackingItem(slot, newItemStack); // already cloned above
            notifyWindows(slot);
            return take;
        }
        
        return 0;
    }
    
}
