package xyz.xenondevs.invui.item;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;

/**
 * A {@link BoundItem} that is bound to a {@link ScrollGui} and updated when the line or line count changes.
 */
public abstract class AbstractScrollGuiBoundItem extends AbstractBoundItem {
    
    @Override
    public void bind(Gui gui) {
        if (!(gui instanceof ScrollGui<?> scrollGui))
            throw new IllegalArgumentException("ScrollItem can only be used in a ScrollGui");
        
        super.bind(gui);
        scrollGui.addScrollHandler((fromLine, toLine) -> notifyWindows());
        scrollGui.addLineCountChangeHandler((oldCount, newCount) -> notifyWindows());
    }
    
    @Override
    public ScrollGui<?> getGui() {
        return (ScrollGui<?>) super.getGui();
    }
    
}
