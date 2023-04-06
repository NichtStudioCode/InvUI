package xyz.xenondevs.invui.window;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.util.InventoryUtils;
import xyz.xenondevs.invui.util.Pair;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;
import xyz.xenondevs.invui.virtualinventory.event.PlayerUpdateReason;
import xyz.xenondevs.invui.virtualinventory.event.UpdateReason;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * A {@link Window} that just uses the top {@link Inventory}.
 */
public abstract class AbstractSingleWindow extends AbstractWindow {
    
    private final AbstractGui gui;
    private final int size;
    protected Inventory inventory;
    
    public AbstractSingleWindow(UUID viewerUUID, ComponentWrapper title, AbstractGui gui, Inventory inventory, boolean closeable) {
        super(viewerUUID, title, gui.getSize(), closeable);
        this.gui = gui;
        this.size = gui.getSize();
        this.inventory = inventory;
        
        gui.addParent(this);
    }
    
    @Override
    protected void initItems() {
        for (int i = 0; i < size; i++) {
            SlotElement element = gui.getSlotElement(i);
            redrawItem(i, element, true);
        }
    }
    
    @Override
    protected void setInvItem(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }
    
    @Override
    protected void handleOpened() {
        // empty
    }
    
    @Override
    protected void handleClosed() {
        // empty
    }
    
    @Override
    public void handleSlotElementUpdate(Gui child, int slotIndex) {
        redrawItem(slotIndex, gui.getSlotElement(slotIndex), true);
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        gui.handleClick(event.getSlot(), (Player) event.getWhoClicked(), event.getClick(), event);
    }
    
    @Override
    public void handleItemShift(InventoryClickEvent event) {
        gui.handleItemShift(event);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void handleCursorCollect(InventoryClickEvent event) {
        // cancel event as we do the collection logic ourselves
        event.setCancelled(true);
        
        Player player = (Player) event.getWhoClicked();
        
        // the template item stack that is used to collect similar items
        ItemStack template = event.getCursor();
        int maxStackSize = InventoryUtils.stackSizeProvider.getMaxStackSize(template);
        
        // retrieve all inventories that are (partially) displayed in the gui, sorted by their gui shift priority
        Set<VirtualInventory> inventories = gui.getAllVirtualInventories();
        
        // add the player inventory to the list of available inventories
        PlayerInventory playerInventory = player.getInventory();
        VirtualInventory virtualPlayerInventory = new VirtualInventory(null, 36, playerInventory.getStorageContents(), null);
        inventories.add(virtualPlayerInventory);
        
        // collect items from inventories until the cursor is full
        UpdateReason updateReason = new PlayerUpdateReason(player, event);
        int amount = template.getAmount();
        for (VirtualInventory inventory : inventories) {
            amount = inventory.collectSimilar(updateReason, template, amount);
            
            if (amount >= maxStackSize)
                break;
        }
        
        // sync player inventory with virtual player inventory
        playerInventory.setStorageContents(virtualPlayerInventory.getUnsafeItems());
        
        // put collected items on cursor
        template.setAmount(amount);
        event.setCursor(template);
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getGuiAt(int index) {
        return index < gui.getSize() ? new Pair<>(gui, index) : null;
    }
    
    @Override
    protected SlotElement getSlotElement(int index) {
        return gui.getSlotElement(index);
    }
    
    @Override
    public void handleViewerDeath(PlayerDeathEvent event) {
        // empty
    }
    
    @Override
    public Inventory[] getInventories() {
        return new Inventory[] {inventory};
    }
    
    @Override
    public AbstractGui[] getGuis() {
        return new AbstractGui[] {gui};
    }
    
    public AbstractGui getGui() {
        return gui;
    }
    
    @Override
    public @Nullable ItemStack @Nullable [] getPlayerItems() {
        Player viewer = getCurrentViewer();
        if (viewer != null) {
            return viewer.getInventory().getContents();
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public abstract static class AbstractBuilder<W extends Window, S extends Builder.Single<W, S>>
        extends AbstractWindow.AbstractBuilder<W, S>
        implements Builder.Single<W, S>
    {
        
        protected Supplier<Gui> guiSupplier;
        
        @Override
        public @NotNull S setGui(@NotNull Supplier<Gui> guiSupplier) {
            this.guiSupplier = guiSupplier;
            return (S) this;
        }
        
        @Override
        public @NotNull S setGui(@NotNull Gui gui) {
            this.guiSupplier = () -> gui;
            return (S) this;
        }
        
        @Override
        public @NotNull S setGui(@NotNull Gui.Builder<?, ?> builder) {
            this.guiSupplier = builder::build;
            return (S) this;
        }
        
    }
    
}
