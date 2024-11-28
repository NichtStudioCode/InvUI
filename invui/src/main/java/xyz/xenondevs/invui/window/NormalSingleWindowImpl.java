package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.internal.util.InventoryUtils;

/**
 * An {@link AbstractSingleWindow} that uses a chest/dropper/hopper inventory as the upper inventory.
 * <p>
 * Use the builder obtained by {@link Window#single()}, to get an instance of this class.
 */
final class NormalSingleWindowImpl extends AbstractSingleWindow {
    
    public NormalSingleWindowImpl(
        Player player,
        Component title,
        AbstractGui gui,
        boolean closeable
    ) {
        super(player, title, gui, InventoryUtils.createMatchingInventory(gui), closeable);
    }
    
    public static final class BuilderImpl
        extends AbstractSingleWindow.AbstractBuilder<Window, Window.Builder.Normal.Single>
        implements Window.Builder.Normal.Single
    {
        
        @Override
        public Window build(Player viewer) {
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            
            var window = new NormalSingleWindowImpl(
                viewer,
                title,
                (AbstractGui) guiSupplier.get(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
