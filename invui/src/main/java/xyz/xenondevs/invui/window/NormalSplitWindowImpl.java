package xyz.xenondevs.invui.window;

import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.util.InventoryUtils;

/**
 * An {@link AbstractSplitWindow} that uses a chest/dropper/hopper inventory as the upper inventory
 * and the player inventory as the lower inventory.
 * <p>
 * Use the builder obtained by {@link Window#split()}, to get an instance of this class.
 */
final class NormalSplitWindowImpl extends AbstractSplitWindow {
    
    public NormalSplitWindowImpl(
        Player player,
        @Nullable ComponentWrapper title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, upperGui, lowerGui, InventoryUtils.createMatchingInventory(upperGui, ""), closeable);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<Window, Window.Builder.Normal.Split>
        implements Window.Builder.Normal.Split
    {
        
        @Override
        public Window build(Player viewer) {
            if (viewer == null)
                throw new IllegalStateException("Viewer is not defined.");
            if (upperGuiSupplier == null)
                throw new IllegalStateException("Upper Gui is not defined.");
            if (lowerGuiSupplier == null)
                throw new IllegalStateException("Lower Gui is not defined.");
            
            var window = new NormalSplitWindowImpl(
                viewer,
                title,
                (AbstractGui) upperGuiSupplier.get(),
                (AbstractGui) lowerGuiSupplier.get(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
