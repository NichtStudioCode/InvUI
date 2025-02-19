package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.util.Pair;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.ReferencingInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @hidden
 */
@ApiStatus.Internal
sealed abstract class AbstractSingleWindow
    extends AbstractWindow
    permits NormalSingleWindowImpl, AnvilSingleWindowImpl, CartographySingleWindowImpl, StonecutterSingleWindowImpl
{
    
    protected final AbstractGui gui;
    protected org.bukkit.inventory.Inventory inventory;
    
    AbstractSingleWindow(Player viewer, Supplier<Component> title, AbstractGui gui, org.bukkit.inventory.Inventory inventory, boolean closeable) {
        super(viewer, title, gui.getSize(), closeable);
        this.gui = gui;
        this.inventory = inventory;
    }
    
    AbstractSingleWindow(Player viewer, Supplier<Component> title, AbstractGui gui, int size, org.bukkit.inventory.Inventory inventory, boolean closeable) {
        super(viewer, title, size, closeable);
        this.gui = gui;
        this.inventory = inventory;
    }
    
    @Override
    protected void setInvItem(int slot, @Nullable ItemStack itemStack) {
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
    public void handleClick(InventoryClickEvent event) {
        gui.handleClick(event.getSlot(), (Player) event.getWhoClicked(), event);
    }
    
    @Override
    public void handleItemShift(InventoryClickEvent event) {
        gui.handleItemShift(event);
    }
    
    @Override
    protected List<Inventory> getContentInventories() {
        List<Inventory> inventories = new ArrayList<>(gui.getInventories());
        inventories.add(ReferencingInventory.fromStorageContents(getViewer().getInventory()));
        return inventories;
    }
    
    @Override
    protected @Nullable Pair<AbstractGui, Integer> getGuiAt(int index) {
        return index < gui.getSize() ? new Pair<>(gui, index) : null;
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
    public List<? extends Gui> getGuis() {
        return List.of(gui);
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
    
    @Override
    public boolean isDouble() {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    static sealed abstract class AbstractBuilder<W extends Window, S extends Builder.Single<W, S>>
        extends AbstractWindow.AbstractBuilder<W, S>
        implements Builder.Single<W, S>
        permits StonecutterSingleWindowImpl.BuilderImpl, AnvilSingleWindowImpl.BuilderImpl, CartographySingleWindowImpl.BuilderImpl, NormalMergedWindowImpl.BuilderImpl, NormalSingleWindowImpl.BuilderImpl
    {
        
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
