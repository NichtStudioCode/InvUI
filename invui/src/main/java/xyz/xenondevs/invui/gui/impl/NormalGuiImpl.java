package xyz.xenondevs.invui.gui.impl;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.structure.Structure;

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
