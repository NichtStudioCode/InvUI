package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomPlainMenu;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.function.Supplier;

final class NormalMergedWindowImpl extends AbstractMergedWindow<CustomPlainMenu> {
    
    public NormalMergedWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        Gui gui,
        MutableProperty<Boolean> closeable
    ) {
        super(player, title, gui, new CustomPlainMenu(gui.getWidth(), gui.getHeight() - 4, player), closeable);
    }
    
    public static final class BuilderImpl
        extends AbstractMergedWindow.AbstractBuilder<Window, Window.Builder.Normal.Merged>
        implements Window.Builder.Normal.Merged
    {
        
        @Override
        public Window build(Player viewer) {
            var window = new NormalMergedWindowImpl(
                viewer,
                titleSupplier,
                guiSupplier.get(),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
    }
    
}
