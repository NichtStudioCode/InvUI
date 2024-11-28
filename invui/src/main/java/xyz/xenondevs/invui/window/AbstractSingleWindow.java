package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.internal.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A {@link Window} that just uses the top {@link org.bukkit.inventory.Inventory}.
 * <p>
 * Only in very rare circumstances should this class be used directly.
 * Instead, use the static builder functions in the
 * {@link Window} interfaces to create a new {@link Window}, such as
 * {@link Window#single()}.
 */
public abstract class AbstractSingleWindow extends AbstractWindow {
    
    private final AbstractGui gui;
    private final int size;
    /**
     * The {@link org.bukkit.inventory.Inventory} of the window.
     */
    protected org.bukkit.inventory.Inventory inventory;
    
    /**
     * Creates a new {@link AbstractSingleWindow}.
     *
     * @param viewer    The player that views the window.
     * @param title     The title of the window.
     * @param gui       The gui of the window.
     * @param inventory The inventory of the window.
     * @param closeable Whether the window is closeable.
     */
    public AbstractSingleWindow(Player viewer, Component title, AbstractGui gui, org.bukkit.inventory.Inventory inventory, boolean closeable) {
        super(viewer, title, gui.getSize(), closeable);
        this.gui = gui;
        this.size = gui.getSize();
        this.inventory = inventory;
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
    
    @Override
    protected List<Inventory> getContentInventories() {
        List<Inventory> inventories = new ArrayList<>(gui.getAllInventories());
        inventories.add(ReferencingInventory.fromStorageContents(getViewer().getInventory()));
        return inventories;
    }
    
    @Override
    protected @Nullable Pair<AbstractGui, Integer> getGuiAt(int index) {
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
    public org.bukkit.inventory.Inventory[] getInventories() {
        return new org.bukkit.inventory.Inventory[] {inventory};
    }
    
    @Override
    public AbstractGui[] getGuis() {
        return new AbstractGui[] {gui};
    }
    
    /**
     * Gets the {@link Gui} used for this {@link AbstractSingleWindow}.
     *
     * @return The {@link Gui} used for this {@link AbstractSingleWindow}.
     */
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
    
    /**
     * Builder for a {@link AbstractSingleWindow}.
     * <p>
     * This class should only be used directly if you're creating a custom {@link AbstractBuilder} for a custom
     * {@link AbstractSingleWindow} implementation. Otherwise, use the static builder functions in the {@link Window}
     * interface, such as {@link Window#single()} to obtain a builder.
     *
     * @param <W> The type of the window.
     * @param <S> The type of the builder.
     */
    @SuppressWarnings("unchecked")
    public abstract static class AbstractBuilder<W extends Window, S extends Builder.Single<W, S>>
        extends AbstractWindow.AbstractBuilder<W, S>
        implements Builder.Single<W, S>
    {
        
        /**
         * The {@link Supplier} to retrieve the {@link Gui} for the {@link AbstractSingleWindow}.
         */
        protected @Nullable Supplier<Gui> guiSupplier;
        
        @Override
        public S setGui(Supplier<Gui> guiSupplier) {
            this.guiSupplier = guiSupplier;
            return (S) this;
        }
        
        @Override
        public S setGui(Gui gui) {
            this.guiSupplier = () -> gui;
            return (S) this;
        }
        
        @Override
        public S setGui(Gui.Builder<?, ?> builder) {
            this.guiSupplier = builder::build;
            return (S) this;
        }
        
    }
    
}
