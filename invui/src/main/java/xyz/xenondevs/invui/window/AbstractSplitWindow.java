package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.internal.menu.CustomContainerMenu;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.OperationCategory;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.state.Property;

import java.util.function.Supplier;

/**
 * @hidden
 */
@ApiStatus.Internal
sealed abstract class AbstractSplitWindow<M extends CustomContainerMenu>
    extends AbstractWindow<M>
    permits AnvilWindowImpl, BrewerWindowImpl, CartographyWindowImpl, CrafterWindowImpl, CraftingWindowImpl, FurnaceWindowImpl, GrindstoneWindowImpl, MerchantWindowImpl, NormalSplitWindowImpl, SmithingWindowImpl, StonecutterWindowImpl
{
    
    private final AbstractGui lowerGui;
    
    AbstractSplitWindow(Player player, Supplier<? extends Component> title, AbstractGui lowerGui, int size, M menu, Property<? extends Boolean> closeable) {
        super(player, title, size, menu, closeable);
        this.lowerGui = lowerGui;
        
        if (lowerGui.getWidth() != 9 || lowerGui.getHeight() != 4)
            throw new IllegalArgumentException("Lower gui must of of dimensions 9x4");
    }
    
    @Override
    public SlotElement.@Nullable GuiLink getGuiAtHotbar(int i) {
        return new SlotElement.GuiLink(lowerGui, 9 * 3 + i);
    }
    
    @SuppressWarnings("unchecked")
    static sealed abstract class AbstractBuilder<W extends Window, S extends Builder.Split<W, S>>
        extends AbstractWindow.AbstractBuilder<W, S>
        implements Builder.Split<W, S>
        permits AnvilWindowImpl.BuilderImpl, BrewerWindowImpl.BuilderImpl, CartographyWindowImpl.BuilderImpl, CrafterWindowImpl.BuilderImpl, CraftingWindowImpl.BuilderImpl, FurnaceWindowImpl.BuilderImpl, GrindstoneWindowImpl.BuilderImpl, MerchantWindowImpl.BuilderImpl, NormalSplitWindowImpl.BuilderImpl, SmithingWindowImpl.BuilderImpl, StonecutterWindowImpl.BuilderImpl
    {
        
        private @Nullable Supplier<? extends Gui> lowerGuiSupplier;
        
        @Override
        public S setLowerGui(Supplier<? extends Gui> guiSupplier) {
            this.lowerGuiSupplier = guiSupplier;
            return (S) this;
        }
        
        protected AbstractGui supplyLowerGui(Player viewer) {
            if (lowerGuiSupplier == null) {
                Inventory inv = ReferencingInventory.fromPlayerStorageContents(viewer.getInventory());
                inv.reverseIterationOrder(OperationCategory.ADD); // shift-clicking moves to bottom right
                inv.setGuiPriority(OperationCategory.ADD, Integer.MAX_VALUE); // shift-click always moves between upper and lower inv
                inv.setGuiPriority(OperationCategory.COLLECT, Integer.MIN_VALUE); // double-click collects from lower inv last
                return (AbstractGui) Gui.of(9, 4, inv);
            }
            
            return (AbstractGui) lowerGuiSupplier.get();
        }
        
    }
    
}
