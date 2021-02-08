package de.studiocode.invui.gui;

import de.studiocode.invui.item.impl.controlitem.ControlItem;

public interface Controllable {
    
    void addControlItem(int index, ControlItem<?> controlItem);
    
}
