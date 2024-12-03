package xyz.xenondevs.invui.item;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.TabGui;

/**
 * A {@link BoundItem} that is bound to a {@link TabGui} and updated when the current tab changes.
 */
public abstract class AbstractTabGuiBoundItem extends AbstractBoundItem {
    
    @Override
    public void bind(Gui gui) {
        if (!(gui instanceof TabGui tabGui))
            throw new IllegalArgumentException("TabItem can only be used in a TabGui");
        
        super.bind(gui);
        tabGui.addTabChangeHandler((oldTab, newTab) -> notifyWindows());
    }
    
    @Override
    public TabGui getGui() {
        return (TabGui) super.getGui();
    }
    
}
