package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomPlainMenu;

import java.util.List;
import java.util.function.Supplier;

final class NormalSplitWindowImpl extends AbstractSplitWindow<CustomPlainMenu> {
    
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    
    public NormalSplitWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        boolean closeable
    ) {
        super(player, title, lowerGui, upperGui.getSize() + lowerGui.getSize(), new CustomPlainMenu(upperGui.getWidth(), upperGui.getHeight(), player), closeable);
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(upperGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<Window, Window.Builder.Normal.Split>
        implements Window.Builder.Normal.Split
    {
        
        private Supplier<? extends Gui> upperGuiSupplier = () -> Gui.empty(9, 6);
        
        @Override
        public Normal.Split setUpperGui(Supplier<? extends Gui> guiSupplier) {
            this.upperGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public Window build(Player viewer) {
            var window = new NormalSplitWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) upperGuiSupplier.get(),
                supplyLowerGui(viewer),
                closeable
            );
            
            applyModifiers(window);
            
            return window;
        }
    }
    
}
