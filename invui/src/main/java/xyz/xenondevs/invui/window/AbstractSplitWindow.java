package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.util.Pair;
import xyz.xenondevs.invui.internal.util.SlotUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * @hidden
 */
@ApiStatus.Internal
sealed abstract class AbstractSplitWindow
    extends AbstractDoubleWindow
    permits AnvilSplitWindowImpl, CartographySplitWindowImpl, StonecutterSplitWindowImpl, NormalSplitWindowImpl
{
    
    protected final AbstractGui upperGui;
    protected final AbstractGui lowerGui;
    
    AbstractSplitWindow(Player player, Supplier<Component> title, AbstractGui upperGui, AbstractGui lowerGui, Inventory upperInventory, boolean closeable) {
        this(player, title, upperGui, lowerGui, upperGui.getSize() + lowerGui.getSize(), upperInventory, closeable);
    }
    
    AbstractSplitWindow(Player player, Supplier<Component> title, AbstractGui upperGui, AbstractGui lowerGui, int size, Inventory upperInventory, boolean closeable) {
        super(player, title, size, upperInventory, closeable);
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
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
    protected @Nullable Pair<AbstractGui, Integer> getGuiAt(int index) {
        if (index < upperGui.getSize()) {
            return new Pair<>(upperGui, index);
        } else if (index < (upperGui.getSize() + lowerGui.getSize())) {
            return new Pair<>(lowerGui, index - upperGui.getSize());
        } else {
            return null;
        }
    }
    
    @Override
    public List<? extends Gui> getGuis() {
        return List.of(upperGui, lowerGui);
    }
    
    @SuppressWarnings("unchecked")
    static sealed abstract class AbstractBuilder<W extends Window, S extends Window.Builder.Double<W, S>>
        extends AbstractWindow.AbstractBuilder<W, S>
        implements Window.Builder.Double<W, S>
        permits AnvilSplitWindowImpl.BuilderImpl, CartographySplitWindowImpl.BuilderImpl, NormalSplitWindowImpl.BuilderImpl, StonecutterSplitWindowImpl.BuilderImpl
    {
        
        protected @Nullable Supplier<Gui> upperGuiSupplier;
        protected @Nullable Supplier<Gui> lowerGuiSupplier;
        
        @Override
        public S setUpperGui(Supplier<Gui> guiSupplier) {
            this.upperGuiSupplier = guiSupplier;
            return (S) this;
        }
        
        @Override
        public S setUpperGui(Gui gui) {
            this.upperGuiSupplier = () -> gui;
            return (S) this;
        }
        
        @Override
        public S setUpperGui(Gui.Builder<?, ?> builder) {
            this.upperGuiSupplier = builder::build;
            return (S) this;
        }
        
        @Override
        public S setLowerGui(Supplier<Gui> guiSupplier) {
            this.lowerGuiSupplier = guiSupplier;
            return (S) this;
        }
        
        @Override
        public S setLowerGui(Gui gui) {
            this.lowerGuiSupplier = () -> gui;
            return (S) this;
        }
        
        @Override
        public S setLowerGui(Gui.Builder<?, ?> builder) {
            this.lowerGuiSupplier = builder::build;
            return (S) this;
        }
        
    }
    
}
