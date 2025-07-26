package xyz.xenondevs.invui.item;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;

import java.util.function.BiConsumer;

/**
 * A {@link BoundItem} that is bound to a {@link PagedGui} and updated when the page or page count changes.
 */
public abstract class AbstractPagedGuiBoundItem extends AbstractBoundItem {
    
    private final BiConsumer<Integer, Integer> notifier = (i1, i2) -> notifyWindows();
    
    @Override
    public void bind(Gui gui) {
        if (!(gui instanceof PagedGui<?> pagedGui))
            throw new IllegalArgumentException("PageItem can only be used in a PagedGui");
        
        super.bind(gui);
        pagedGui.addPageChangeHandler(notifier);
        pagedGui.addPageCountChangeHandler(notifier);
    }
    
    @Override
    public void unbind() {
        var gui = getGui();
        
        super.unbind();
        
        gui.removePageChangeHandler(notifier);
        gui.removePageCountChangeHandler(notifier);
    }
    
    @Override
    public PagedGui<?> getGui() {
        return (PagedGui<?>) super.getGui();
    }
    
}
