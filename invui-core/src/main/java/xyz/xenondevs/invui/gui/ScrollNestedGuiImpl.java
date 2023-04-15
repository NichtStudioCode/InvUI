package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link AbstractScrollGui} that uses {@link Gui Guis} as content.
 *
 * @see ScrollItemsGuiImpl
 * @see ScrollInventoryGuiImpl
 */
final class ScrollNestedGuiImpl extends AbstractScrollGui<Gui> {
    
    private List<Gui> guis;
    private List<SlotElement.LinkedSlotElement> elements;
    
    /**
     * Creates a new {@link ScrollNestedGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The height of this Gui.
     * @param guis             The {@link Gui Guis} to use.
     * @param contentListSlots The slots where content should be displayed.
     */
    public ScrollNestedGuiImpl(int width, int height, @Nullable List<@NotNull Gui> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    /**
     * Creates a new {@link ScrollNestedGuiImpl}.
     *
     * @param guis      The {@link Gui Guis} to use.
     * @param structure The {@link Structure} to use.
     */
    public ScrollNestedGuiImpl(@Nullable List<@NotNull Gui> guis, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }
    
    @Override
    public void setContent(@Nullable List<@NotNull Gui> guis) {
        this.guis = guis != null ? guis : new ArrayList<>();
        updateElements();
        update();
    }
    
    private void updateElements() {
        elements = new ArrayList<>();
        for (Gui gui : guis) {
            for (int i = 0; i < gui.getSize(); i++) {
                elements.add(new SlotElement.LinkedSlotElement(gui, i));
            }
        }
    }
    
    @Override
    protected List<SlotElement.LinkedSlotElement> getElements(int from, int to) {
        return elements.subList(from, Math.min(elements.size(), to));
    }
    
    @Override
    public int getMaxLine() {
        if (elements == null) return 0;
        return (int) Math.ceil((double) elements.size() / (double) getLineLength()) - 1;
    }
    
    public static final class Builder extends AbstractBuilder<Gui> {
        
        @Override
        public @NotNull ScrollGui<Gui> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new ScrollNestedGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
    
}
