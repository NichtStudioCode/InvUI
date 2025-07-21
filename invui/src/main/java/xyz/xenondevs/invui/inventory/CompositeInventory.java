package xyz.xenondevs.invui.inventory;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.internal.util.ArrayUtils;
import xyz.xenondevs.invui.inventory.event.InventoryClickEvent;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * An {@link Inventory} which is composed of multiple other {@link Inventory Inventories}.
 */
public final class CompositeInventory extends Inventory {
    
    private final Inventory[] inventories;
    
    /**
     * Constructs a new {@link CompositeInventory}.
     *
     * @param first The first {@link Inventory}.
     * @param other The other {@link Inventory Inventories}.
     */
    public CompositeInventory(Inventory first, Inventory... other) {
        super(calculateSize(first, other));
        this.inventories = ArrayUtils.concat(Inventory[]::new, first, other);
    }
    
    /**
     * Constructs a new {@link CompositeInventory}.
     *
     * @param inventories The {@link Inventory Inventories}. Cannot be empty.
     */
    public CompositeInventory(Collection<? extends Inventory> inventories) {
        super(calculateSize(inventories));
        if (inventories.isEmpty())
            throw new IllegalArgumentException("CompositeInventory must contain at least one Inventory");
        
        this.inventories = inventories.toArray(Inventory[]::new);
    }
    
    private static int calculateSize(Inventory first, Inventory[] other) {
        int size = first.getSize();
        for (Inventory inventory : other) {
            size += inventory.getSize();
        }
        return size;
    }
    
    private static int calculateSize(Collection<? extends Inventory> inventories) {
        int size = 0;
        for (Inventory inventory : inventories) {
            size += inventory.getSize();
        }
        return size;
    }
    
    @Override
    public int[] getIterationOrder(IterationOrderCategory category) {
        int[] iterationOrder = new int[getSize()];
        int i = 0;
        int offset = 0;
        for (Inventory inventory : inventories) {
            int[] invOrder = inventory.getIterationOrder(category);
            for (var slot : invOrder) {
                iterationOrder[i++] = offset + slot;
            }
            offset += inventory.getSize();
        }
        return iterationOrder;
    }
    
    @Override
    public int[] getMaxStackSizes() {
        int[] stackSizes = new int[getSize()];
        
        int pos = 0;
        for (Inventory inventory : inventories) {
            int[] otherStackSizes = inventory.getMaxStackSizes();
            System.arraycopy(otherStackSizes, 0, stackSizes, pos, otherStackSizes.length);
            pos += otherStackSizes.length;
        }
        
        return stackSizes;
    }
    
    @Override
    public int getMaxSlotStackSize(int slot) {
        var invSlot = findInventory(slot);
        return invSlot.inventory().getMaxSlotStackSize(invSlot.slot());
    }
    
    @Override
    public @Nullable ItemStack[] getItems() {
        ItemStack[] items = new ItemStack[getSize()];
        int pos = 0;
        for (Inventory inv : inventories) {
            @Nullable ItemStack[] invItems = inv.getItems();
            System.arraycopy(invItems, 0, items, pos, invItems.length);
            pos += invItems.length;
        }
        return items;
    }
    
    @Override
    public @Nullable ItemStack[] getUnsafeItems() {
        ItemStack[] items = new ItemStack[getSize()];
        int pos = 0;
        for (Inventory inv : inventories) {
            @Nullable ItemStack[] invItems = inv.getUnsafeItems();
            System.arraycopy(invItems, 0, items, pos, invItems.length);
            pos += invItems.length;
        }
        return items;
    }
    
    @Override
    public @Nullable ItemStack getItem(int slot) {
        var invSlot = findInventory(slot);
        return invSlot.inventory().getItem(invSlot.slot());
    }
    
    @Override
    public @Nullable ItemStack getUnsafeItem(int slot) {
        var invSlot = findInventory(slot);
        return invSlot.inventory().getUnsafeItem(invSlot.slot());
    }
    
    @Override
    protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
        var invSlot = findInventory(slot);
        invSlot.inventory().setCloneBackingItem(invSlot.slot(), itemStack);
    }
    
    @Override
    protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
        var invSlot = findInventory(slot);
        invSlot.inventory().setDirectBackingItem(invSlot.slot(), itemStack);
    }
    
    private InventorySlot findInventory(int slot) {
        int pos = 0;
        for (Inventory inv : inventories) {
            int invSize = inv.getSize();
            if (slot < pos + invSize) {
                return new InventorySlot(inv, slot - pos);
            }
            
            pos += invSize;
        }
        
        throw new IndexOutOfBoundsException(slot);
    }
    
    @Override
    public void notifyWindows() {
        for (Inventory inventory : inventories) {
            inventory.notifyWindows();
        }
    }
    
    @Override
    public void notifyWindows(int slot) {
        var invSlot = findInventory(slot);
        invSlot.inventory().notifyWindows(invSlot.slot());
    }
    
    @Override
    public void addViewer(AbstractWindow<?> viewer, int what, int how) {
        var invSlot = findInventory(what);
        invSlot.inventory().addViewer(viewer, invSlot.slot(), how);
    }
    
    @Override
    public void removeViewer(AbstractWindow<?> viewer, int what, int how) {
        var invSlot = findInventory(what);
        invSlot.inventory().removeViewer(viewer, invSlot.slot(), how);
    }
    
    @Override
    public boolean callClickEvent(int slot, Click click) {
        var invSlot = findInventory(slot);
        return invSlot.inventory().callClickEvent(invSlot.slot(), click);
    }
    
    @Override
    public ItemPreUpdateEvent callPreUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        var invSlot = findInventory(slot);
        return invSlot.inventory().callPreUpdateEvent(updateReason, invSlot.slot(), previousItemStack, newItemStack);
    }
    
    @Override
    public void callPostUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        var invSlot = findInventory(slot);
        invSlot.inventory().callPostUpdateEvent(updateReason, invSlot.slot(), previousItemStack, newItemStack);
    }
    
    @Override
    public boolean hasEventHandlers() {
        for (Inventory inventory : inventories) {
            if (inventory.hasEventHandlers())
                return true;
        }
        
        return false;
    }
    
    @Override
    public void setIterationOrder(IterationOrderCategory category, int[] iterationOrder) {
        throw new UnsupportedOperationException("Iteration order needs to be set in the backing inventories");
    }
    
    @Override
    public List<Consumer<InventoryClickEvent>> getClickHandlers() {
        throw new UnsupportedOperationException("Click handlers need to be set in the backing inventory");
    }
    
    @Override
    public void setClickHandlers(List<Consumer<InventoryClickEvent>> clickHandlers) {
        throw new UnsupportedOperationException("Click handlers need to be set in the backing inventory");
    }
    
    @Override
    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        throw new UnsupportedOperationException("Click handlers need to be set in the backing inventory");
    }
    
    @Override
    public void removeClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        throw new UnsupportedOperationException("Click handlers need to be set in the backing inventory");
    }
    
    @Override
    public List<Consumer<ItemPreUpdateEvent>> getPreUpdateHandlers() {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void setPreUpdateHandlers(List<Consumer<ItemPreUpdateEvent>> preUpdateHandlers) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void addPreUpdateHandler(Consumer<ItemPreUpdateEvent> preUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void removePreUpdateHandler(Consumer<ItemPreUpdateEvent> preUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public List<Consumer<ItemPostUpdateEvent>> getPostUpdateHandlers() {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void setPostUpdateHandlers(List<Consumer<ItemPostUpdateEvent>> postUpdateHandlers) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void addPostUpdateHandler(Consumer<ItemPostUpdateEvent> postUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void removePostUpdateHandler(Consumer<ItemPostUpdateEvent> postUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
}
