package xyz.xenondevs.invui.item;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

/**
 * An abstract implementation of {@link BoundItem}.
 */
public non-sealed abstract class AbstractBoundItem extends AbstractItem implements BoundItem {
    
    private @Nullable Gui gui;
    
    @Override
    public Gui getGui() {
        if (gui == null)
            throw new IllegalStateException("Gui is not bound");
        
        return gui;
    }
    
    @Override
    public void bind(Gui gui) {
        if (this.gui != null)
            throw new IllegalStateException("Item is already bound to a gui");
        
        this.gui = gui;
    }
    
    @Override
    public void unbind() {
        if (gui == null)
            throw new IllegalStateException("Item is not bound to a gui");
        this.gui = null;
    }
    
    @Override
    public boolean isBound() {
        return gui != null;
    }
    
}
