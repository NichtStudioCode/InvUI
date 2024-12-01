package xyz.xenondevs.invui.item;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;

/**
 * A {@link BoundItem} that is bound to a {@link PagedGui} and updated when the page or page count changes.
 */
public abstract class AbstractPagedGuiBoundItem extends AbstractBoundItem {
    
    @Override
    public void bind(Gui gui) {
        if (!(gui instanceof PagedGui<?> pagedGui))
            throw new IllegalArgumentException("PageItem can only be used in a PagedGui");
        
        super.bind(gui);
        pagedGui.addPageChangeHandler((oldPage, newPage) -> notifyWindows());
        pagedGui.addPageCountChangeHandler((oldCount, newCount) -> notifyWindows());
    }
    
    @Override
    public PagedGui<?> getGui() {
        return (PagedGui<?>) super.getGui();
    }
    
}
