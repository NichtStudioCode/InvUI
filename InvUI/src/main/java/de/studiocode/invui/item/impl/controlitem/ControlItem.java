package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.item.ItemBuilder;
import de.studiocode.invui.item.impl.BaseItem;

public abstract class ControlItem<G extends GUI> extends BaseItem {
    
    private G gui;
    
    public abstract ItemBuilder getItemBuilder(G gui);
    
    @Override
    public final ItemBuilder getItemBuilder() {
        return getItemBuilder(gui);
    }
    
    public G getGui() {
        return gui;
    }
    
    public void setGui(G gui) {
        if (this.gui != null)
            throw new IllegalStateException("The GUI is already set. (One ControlItem can't control multiple GUIs)");
        
        this.gui = gui;
    }
    
}
