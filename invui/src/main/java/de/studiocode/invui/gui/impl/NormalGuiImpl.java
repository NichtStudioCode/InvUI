package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.AbstractGui;
import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;

/**
 * A normal {@link Gui} without any special features.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public final class NormalGuiImpl extends AbstractGui {
    
    /**
     * Creates a new {@link NormalGuiImpl}.
     *
     * @param width  The width of this Gui.
     * @param height The height of this Gui.
     * @deprecated Use {@link Gui#empty(int, int)} instead.
     */
    @Deprecated
    public NormalGuiImpl(int width, int height) {
        super(width, height);
    }
    
    /**
     * Creates a new {@link NormalGuiImpl}.
     *
     * @param structure The {@link Structure} to use.
     * @deprecated Use {@link Gui#of(Structure)} instead.
     */
    @Deprecated
    public NormalGuiImpl(@NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight());
        applyStructure(structure);
    }
    
}
