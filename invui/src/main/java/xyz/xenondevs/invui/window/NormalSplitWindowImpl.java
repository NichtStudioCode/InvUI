package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.internal.util.InventoryUtils;

import java.util.function.Supplier;

final class NormalSplitWindowImpl extends AbstractSplitWindow {
    
    public NormalSplitWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, upperGui, lowerGui, InventoryUtils.createMatchingInventory(upperGui), closeable);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<Window, Window.Builder.Normal.Split>
        implements Window.Builder.Normal.Split
    {
        
        @Override
        public Window build(Player viewer) {
            if (upperGuiSupplier == null)
                throw new IllegalStateException("Upper Gui is not defined.");
            if (lowerGuiSupplier == null)
                throw new IllegalStateException("Lower Gui is not defined.");
            
            var window = new NormalSplitWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) upperGuiSupplier.get(),
                (AbstractGui) lowerGuiSupplier.get(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
