package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;

/**
 * A normal {@link GUI} without any special features.
 */
public final class NormalGUIImpl extends AbstractGUI {
    
    public NormalGUIImpl(int width, int height) {
        super(width, height);
    }
    
    public NormalGUIImpl(@NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight());
        applyStructure(structure);
    }
    
}
