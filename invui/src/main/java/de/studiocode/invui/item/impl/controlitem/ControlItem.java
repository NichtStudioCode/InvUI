package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemProvider;
import de.studiocode.invui.item.impl.BaseItem;

/**
 * A special type of {@link Item} that stores the {@link GUI} in which it is displayed.
 *
 * @param <G> The GUI Type this {@link ControlItem} will control. Not checked when adding it to a GUI.
 */
public abstract class ControlItem<G extends GUI> extends BaseItem {
    
    private G gui;
    
    public abstract ItemProvider getItemProvider(G gui);
    
    @Override
    public final ItemProvider getItemProvider() {
        return getItemProvider(gui);
    }
    
    public G getGui() {
        return gui;
    }
    
    @SuppressWarnings("unchecked")
    public void setGui(Object gui) {
        if (this.gui == null) this.gui = (G) gui;
    }
    
}
