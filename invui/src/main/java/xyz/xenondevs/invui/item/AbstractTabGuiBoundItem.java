package xyz.xenondevs.invui.item;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.TabGui;

import java.util.function.BiConsumer;

/**
 * A {@link BoundItem} that is bound to a {@link TabGui} and updated when the current tab changes.
 */
public abstract class AbstractTabGuiBoundItem extends AbstractBoundItem {
    
    private final BiConsumer<Integer, Integer> notifier = (i1, i2) -> notifyWindows();
    
    @Override
    public void bind(Gui gui) {
        if (!(gui instanceof TabGui tabGui))
            throw new IllegalArgumentException("AbstractTabGuiBoundItem can only be bound to a TabGui");
        
        super.bind(gui);
        tabGui.addTabChangeHandler(notifier);
    }
    
    @Override
    public void unbind() {
        var gui = getGui();
        
        super.unbind();
        
        gui.removeTabChangeHandler(notifier);
    }
    
    @Override
    public TabGui getGui() {
        return (TabGui) super.getGui();
    }
    
}
