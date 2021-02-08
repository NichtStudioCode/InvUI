package de.studiocode.invui.item.impl.controlitem;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.item.impl.BaseItem;
import de.studiocode.invui.item.itembuilder.ItemBuilder;

import java.util.function.Function;

public abstract class ControlItem<G extends GUI> extends BaseItem {
    
    private G gui;
    private final Function<G, ItemBuilder> builderFunction;
    
    public ControlItem(Function<G, ItemBuilder> builderFunction) {
        this.builderFunction = builderFunction;
    }
    
    @Override
    public ItemBuilder getItemBuilder() {
        return builderFunction.apply(gui);
    }
    
    public G getGui() {
        return gui;
    }
    
    public void setGui(G gui) {
        this.gui = gui;
    }
    
}
