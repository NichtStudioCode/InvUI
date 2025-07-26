package xyz.xenondevs.invui.item;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;

import java.util.function.BiConsumer;

/**
 * A {@link BoundItem} that is bound to a {@link ScrollGui} and updated when the line or line count changes.
 */
public abstract class AbstractScrollGuiBoundItem extends AbstractBoundItem {
    
    private final BiConsumer<Integer, Integer> notifier = (i1, i2) -> notifyWindows();
    
    @Override
    public void bind(Gui gui) {
        if (!(gui instanceof ScrollGui<?> scrollGui))
            throw new IllegalArgumentException("ScrollItem can only be used in a ScrollGui");
        
        super.bind(gui);
        scrollGui.addScrollHandler(notifier);
        scrollGui.addLineCountChangeHandler(notifier);
    }
    
    @Override
    public void unbind() {
        var gui = getGui();
        
        super.unbind();
        
        gui.removeScrollHandler(notifier);
        gui.removeLineCountChangeHandler(notifier);
    }
    
    @Override
    public ScrollGui<?> getGui() {
        return (ScrollGui<?>) super.getGui();
    }
    
}
