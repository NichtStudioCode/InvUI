package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.internal.menu.CustomPlainMenu;

import java.util.function.Supplier;

final class NormalMergedWindowImpl extends AbstractMergedWindow<CustomPlainMenu> {
    
    public NormalMergedWindowImpl(
        Player player,
        Supplier<Component> title,
        AbstractGui gui,
        boolean closeable
    ) {
        super(player, title, gui, new CustomPlainMenu(gui.getWidth(), gui.getHeight() - 4, player), closeable);
    }
    
    public static final class BuilderImpl
        extends AbstractMergedWindow.AbstractBuilder<Window, Window.Builder.Normal.Merged>
        implements Window.Builder.Normal.Merged
    {
        
        @Override
        public Window build(Player viewer) {
            if (guiSupplier == null)
                throw new IllegalStateException("Gui is not defined.");
            
            var window = new NormalMergedWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) guiSupplier.get(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
    }
    
}
