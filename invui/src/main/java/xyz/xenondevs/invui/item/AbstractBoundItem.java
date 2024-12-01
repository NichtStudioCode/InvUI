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
            throw new IllegalStateException("Gui is already bound");
        
        this.gui = gui;
    }
    
}
