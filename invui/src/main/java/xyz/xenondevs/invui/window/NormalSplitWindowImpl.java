package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.internal.menu.CustomPlainMenu;

import java.util.function.Supplier;

final class NormalSplitWindowImpl extends AbstractSplitWindow<CustomPlainMenu> {
    
    public NormalSplitWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, upperGui, lowerGui, new CustomPlainMenu(upperGui.getWidth(), upperGui.getHeight(), player), closeable);
        initItems();
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<Window, Window.Builder.Normal.Split>
        implements Window.Builder.Normal.Split
    {
        
        @Override
        public Window build(Player viewer) {
            var window = new NormalSplitWindowImpl(
                viewer,
                titleSupplier,
                supplyUpperGui(),
                supplyLowerGui(viewer),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
