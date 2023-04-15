package xyz.xenondevs.invui.window;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.util.Pair;
import xyz.xenondevs.invui.util.SlotUtils;

import java.util.function.Supplier;

/**
 * A {@link Window} where top and player {@link Inventory} are affected by different {@link Gui}s.
 */
public abstract class AbstractSplitWindow extends AbstractDoubleWindow {
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    
    public AbstractSplitWindow(Player player, ComponentWrapper title, AbstractGui upperGui, AbstractGui lowerGui, Inventory upperInventory, boolean closeable) {
        super(player, title, upperGui.getSize() + lowerGui.getSize(), upperInventory, closeable);
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
        
        upperGui.addParent(this);
        lowerGui.addParent(this);
    }
    
    @Override
    public void handleSlotElementUpdate(Gui child, int slotIndex) {
        redrawItem(child == upperGui ? slotIndex : upperGui.getSize() + slotIndex,
            child.getSlotElement(slotIndex), true);
    }
    
    @Override
    public SlotElement getSlotElement(int index) {
        if (index >= upperGui.getSize()) return lowerGui.getSlotElement(index - upperGui.getSize());
        else return upperGui.getSlotElement(index);
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getWhereClicked(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == getUpperInventory()) {
            return new Pair<>(upperGui, event.getSlot());
        } else {
            int index = SlotUtils.translatePlayerInvToGui(event.getSlot());
            return new Pair<>(lowerGui, index);
        }
    }
    
    @Override
    protected Pair<AbstractGui, Integer> getGuiAt(int index) {
        if (index < upperGui.getSize()) return new Pair<>(upperGui, index);
        else if (index < (upperGui.getSize() + lowerGui.getSize()))
            return new Pair<>(lowerGui, index - upperGui.getSize());
        else return null;
    }
    
    @Override
    public AbstractGui[] getGuis() {
        return new AbstractGui[] {upperGui, lowerGui};
    }
    
    @SuppressWarnings("unchecked")
    public static abstract class AbstractBuilder<W extends Window, S extends Window.Builder.Double<W, S>>
        extends AbstractWindow.AbstractBuilder<W, S>
        implements Window.Builder.Double<W, S>
    {
        
        protected Supplier<Gui> upperGuiSupplier;
        protected Supplier<Gui> lowerGuiSupplier;
        
        @Override
        public @NotNull S setUpperGui(@NotNull Supplier<Gui> guiSupplier) {
            this.upperGuiSupplier = guiSupplier;
            return (S) this;
        }
        
        @Override
        public @NotNull S setUpperGui(@NotNull Gui gui) {
            this.upperGuiSupplier = () -> gui;
            return (S) this;
        }
        
        @Override
        public @NotNull S setUpperGui(@NotNull Gui.Builder<?, ?> builder) {
            this.upperGuiSupplier = builder::build;
            return (S) this;
        }
        
        @Override
        public @NotNull S setLowerGui(@NotNull Supplier<Gui> guiSupplier) {
            this.lowerGuiSupplier = guiSupplier;
            return (S) this;
        }
        
        @Override
        public @NotNull S setLowerGui(@NotNull Gui gui) {
            this.lowerGuiSupplier = () -> gui;
            return (S) this;
        }
        
        @Override
        public @NotNull S setLowerGui(@NotNull Gui.Builder<?, ?> builder) {
            this.lowerGuiSupplier = builder::build;
            return (S) this;
        }
        
    }
    
}
