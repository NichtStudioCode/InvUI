package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;

/**
 * A normal {@link GUI} without any special features.
 */
public class SimpleGUI extends BaseGUI {
    
    public SimpleGUI(int width, int height) {
        super(width, height);
    }
    
    public SimpleGUI(int width, int height, @NotNull Structure structure) {
        super(width, height);
        applyStructure(structure);
    }
    
}
