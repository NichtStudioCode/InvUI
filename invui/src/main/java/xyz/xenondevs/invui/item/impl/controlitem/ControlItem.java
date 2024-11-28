package xyz.xenondevs.invui.item.impl.controlitem;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

/**
 * A special type of {@link Item} that stores the {@link Gui} in which it is displayed.
 *
 * @param <G> The Gui Type this {@link ControlItem} will control. Not checked when adding it to a Gui.
 */
public abstract class ControlItem<G extends Gui> extends AbstractItem {
    
    private @Nullable G gui;
    
    public abstract ItemProvider getItemProvider(G gui);
    
    @Override
    public final ItemProvider getItemProvider() {
        if (gui == null)
            throw new IllegalStateException("Gui is not set");
        
        return getItemProvider(gui);
    }
    
    public @Nullable G getGui() {
        return gui;
    }
    
    public void setGui(G gui) {
        if (this.gui == null) {
            this.gui = gui;
        }
    }
    
}
