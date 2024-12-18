package xyz.xenondevs.invui.inventory;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.ArrayUtils;
import xyz.xenondevs.invui.internal.util.Pair;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.AbstractWindow;

import java.util.Collection;
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
    public CompositeInventory(Collection<Inventory> inventories) {
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
    
    private static int calculateSize(Collection<Inventory> inventories) {
        int size = 0;
        for (Inventory inventory : inventories) {
            size += inventory.getSize();
        }
        return size;
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
        Pair<Inventory, Integer> pair = findInventory(slot);
        return pair.first().getMaxSlotStackSize(pair.second());
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
        Pair<Inventory, Integer> pair = findInventory(slot);
        return pair.first().getItem(pair.second());
    }
    
    @Override
    public @Nullable ItemStack getUnsafeItem(int slot) {
        Pair<Inventory, Integer> pair = findInventory(slot);
        return pair.first().getUnsafeItem(pair.second());
    }
    
    @Override
    protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
        Pair<Inventory, Integer> pair = findInventory(slot);
        pair.first().setCloneBackingItem(pair.second(), itemStack);
    }
    
    @Override
    protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
        Pair<Inventory, Integer> pair = findInventory(slot);
        pair.first().setDirectBackingItem(pair.second(), itemStack);
    }
    
    private Pair<Inventory, Integer> findInventory(int slot) {
        int pos = 0;
        for (Inventory inv : inventories) {
            int invSize = inv.getSize();
            if (slot < pos + invSize) {
                return new Pair<>(inv, slot - pos);
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
        invSlot.first().notifyWindows(invSlot.second());
    }
    
    @Override
    public void addViewer(AbstractWindow viewer, int what, int how) {
        var invSlot = findInventory(what);
        invSlot.first().addViewer(viewer, invSlot.second(), how);
    }
    
    @Override
    public void removeViewer(AbstractWindow viewer, int what, int how) {
        var invSlot = findInventory(what);
        invSlot.first().removeViewer(viewer, invSlot.second(), how);
    }
    
    @Override
    public void callClickEvent(int slot, InventoryClickEvent event) {
        var invSlot = findInventory(slot);
        invSlot.first().callClickEvent(invSlot.second(), event);
    }
    
    @Override
    public ItemPreUpdateEvent callPreUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        var invSlot = findInventory(slot);
        return invSlot.first().callPreUpdateEvent(updateReason, invSlot.second(), previousItemStack, newItemStack);
    }
    
    @Override
    public void callPostUpdateEvent(@Nullable UpdateReason updateReason, int slot, @Nullable ItemStack previousItemStack, @Nullable ItemStack newItemStack) {
        var invSlot = findInventory(slot);
        invSlot.first().callPostUpdateEvent(updateReason, invSlot.second(), previousItemStack, newItemStack);
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
    public void setPostUpdateHandler(@Nullable Consumer<ItemPostUpdateEvent> inventoryUpdatedHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
    @Override
    public void setPreUpdateHandler(@Nullable Consumer<ItemPreUpdateEvent> preUpdateHandler) {
        throw new UnsupportedOperationException("Update handlers need to be set in the backing inventory");
    }
    
}
