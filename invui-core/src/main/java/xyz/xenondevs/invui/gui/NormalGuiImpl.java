package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.structure.Structure;

/**
 * A normal {@link Gui} without any special features.
 */
final class NormalGuiImpl extends AbstractGui {
    
    /**
     * Creates a new {@link NormalGuiImpl}.
     *
     * @param width  The width of this Gui.
     * @param height The height of this Gui.
     */
    public NormalGuiImpl(int width, int height) {
        super(width, height);
    }
    
    /**
     * Creates a new {@link NormalGuiImpl}.
     *
     * @param structure The {@link Structure} to use.
     */
    public NormalGuiImpl(@NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight());
        applyStructure(structure);
    }
    
    public static class Builder extends AbstractBuilder<Gui, Gui.Builder.Normal> implements Gui.Builder.Normal {
        
        @Override
        public @NotNull Gui build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
        
            var gui = new NormalGuiImpl(structure);
            applyModifiers(gui);
            return gui;
        }
    
    }
    
}
