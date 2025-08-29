package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.internal.menu.CustomContainerMenu;
import xyz.xenondevs.invui.internal.util.InventoryUtils;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.function.Supplier;

sealed abstract class AbstractSplitWindow<M extends CustomContainerMenu>
    extends AbstractWindow<M>
    permits AnvilWindowImpl, BrewingWindowImpl, CartographyWindowImpl, CrafterWindowImpl, CraftingWindowImpl, FurnaceWindowImpl, GrindstoneWindowImpl, MerchantWindowImpl, NormalSplitWindowImpl, SmithingWindowImpl, StonecutterWindowImpl
{
    
    private final Gui lowerGui;
    
    AbstractSplitWindow(Player player, Supplier<? extends Component> title, Gui lowerGui, int size, M menu, MutableProperty<Boolean> closeable) {
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
        permits AnvilWindowImpl.BuilderImpl, BrewingWindowImpl.BuilderImpl, CartographyWindowImpl.BuilderImpl, CrafterWindowImpl.BuilderImpl, CraftingWindowImpl.BuilderImpl, FurnaceWindowImpl.BuilderImpl, GrindstoneWindowImpl.BuilderImpl, MerchantWindowImpl.BuilderImpl, NormalSplitWindowImpl.BuilderImpl, SmithingWindowImpl.BuilderImpl, StonecutterWindowImpl.BuilderImpl
    {
        
        private @Nullable Supplier<? extends Gui> lowerGuiSupplier;
        
        @Override
        public S setLowerGui(Supplier<? extends Gui> guiSupplier) {
            this.lowerGuiSupplier = guiSupplier;
            return (S) this;
        }
        
        protected Gui supplyLowerGui(Player viewer) {
            return lowerGuiSupplier != null
                ? lowerGuiSupplier.get()
                : InventoryUtils.createPlayerReferencingInventoryGui(viewer);
        }
        
    }
    
}
