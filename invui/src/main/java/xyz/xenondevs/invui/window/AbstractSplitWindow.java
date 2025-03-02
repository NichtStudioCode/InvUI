package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomContainerMenu;
import xyz.xenondevs.invui.internal.util.Pair;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.ReferencingInventory;

import java.util.function.Supplier;

/**
 * @hidden
 */
@ApiStatus.Internal
sealed abstract class AbstractSplitWindow<M extends CustomContainerMenu>
    extends AbstractWindow<M>
    permits AnvilWindowImpl, CartographyWindowImpl, CrafterWindowImpl, MerchantWindowImpl, NormalSplitWindowImpl, StonecutterWindowImpl
{
    
    private final AbstractGui lowerGui;
    
    AbstractSplitWindow(Player player, Supplier<Component> title, AbstractGui lowerGui, int size, M menu, boolean closeable) {
        super(player, title, size, menu, closeable);
        this.lowerGui = lowerGui;
        
        if (lowerGui.getWidth() != 9 || lowerGui.getHeight() != 4)
            throw new IllegalArgumentException("Lower gui must of of dimensions 9x4");
    }
    
    @Override
    public @Nullable Pair<AbstractGui, Integer> getGuiAtHotbar(int i) {
        return new Pair<>(lowerGui, 9 * 3 + i);
    }
    
    @SuppressWarnings("unchecked")
    static sealed abstract class AbstractBuilder<W extends Window, S extends Builder.Split<W, S>>
        extends AbstractWindow.AbstractBuilder<W, S>
        implements Builder.Split<W, S>
        permits AnvilWindowImpl.BuilderImpl, CartographyWindowImpl.BuilderImpl, CrafterWindowImpl.BuilderImpl, MerchantWindowImpl.BuilderImpl, NormalSplitWindowImpl.BuilderImpl, StonecutterWindowImpl.BuilderImpl
    {
        
        private @Nullable Supplier<Gui> lowerGuiSupplier;
        
        @Override
        public S setLowerGui(Supplier<Gui> guiSupplier) {
            this.lowerGuiSupplier = guiSupplier;
            return (S) this;
        }
        
        protected AbstractGui supplyLowerGui(Player viewer) {
            if (lowerGuiSupplier == null) {
                AbstractGui gui = (AbstractGui) Gui.empty(9, 4);
                Inventory inv = ReferencingInventory.fromPlayerStorageContents(viewer.getInventory());
                inv.reverseIterationOrder();
                inv.setGuiPriority(Integer.MAX_VALUE); // expected vanilla-like behavior: shift-click moves between upper and lower inv
                gui.fillRectangle(0, 0, 9, inv, true);
                return gui;
            }
            
            return (AbstractGui) lowerGuiSupplier.get();
        }
        
    }
    
}
