package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomContainerMenu;
import xyz.xenondevs.invui.internal.util.Pair;

import java.util.List;
import java.util.function.Supplier;

/**
 * @hidden
 */
@ApiStatus.Internal
sealed abstract class AbstractMergedWindow<M extends CustomContainerMenu> extends AbstractWindow<M> permits NormalMergedWindowImpl {
    
    private final AbstractGui gui;
    
    AbstractMergedWindow(Player viewer, Supplier<Component> title, AbstractGui gui, M menu, boolean closeable) {
        super(viewer, title, gui.getSize(), menu, closeable);
        this.gui = gui;
    }
    
    @Override
    public List<? extends Gui> getGuis() {
        return List.of(gui);
    }
    
    @Override
    public @Nullable Pair<AbstractGui, Integer> getGuiAtHotbar(int i) {
        return new Pair<>(gui, gui.getSize() - 9 + i);
    }
    
    @SuppressWarnings("unchecked")
    static sealed abstract class AbstractBuilder<W extends Window, S extends Builder.Merged<W, S>>
        extends AbstractWindow.AbstractBuilder<W, S>
        implements Builder.Merged<W, S> permits NormalMergedWindowImpl.BuilderImpl
    {
        
        protected @Nullable Supplier<Gui> guiSupplier;
        
        @Override
        public S setGui(Supplier<Gui> guiSupplier) {
            this.guiSupplier = guiSupplier;
            return (S) this;
        }
        
        @Override
        public S setGui(Gui gui) {
            this.guiSupplier = () -> gui;
            return (S) this;
        }
        
        @Override
        public S setGui(Gui.Builder<?, ?> builder) {
            this.guiSupplier = builder::build;
            return (S) this;
        }
        
    }
    
}