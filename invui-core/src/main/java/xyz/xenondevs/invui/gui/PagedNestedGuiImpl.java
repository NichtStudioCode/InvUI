package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link AbstractPagedGui} where every page is its own {@link Gui}.
 * <p>
 * Use the static factory and builder functions, such as {@link PagedGui#guis()},
 * to get an instance of this class.
 * 
 * @see PagedItemsGuiImpl
 * @see PagedInventoriesGuiImpl
 */
final class PagedNestedGuiImpl extends AbstractPagedGui<Gui> {
    
    /**
     * Creates a new {@link PagedNestedGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The height of this Gui.
     * @param guis             The {@link Gui Guis} to use as pages.
     * @param contentListSlots The slots where content should be displayed.
     */
    public PagedNestedGuiImpl(int width, int height, @Nullable List<@NotNull Gui> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    /**
     * Creates a new {@link PagedNestedGuiImpl}.
     *
     * @param guis      The {@link Gui Guis} to use as pages.
     * @param structure The {@link Structure} to use.
     */
    public PagedNestedGuiImpl(@Nullable List<@NotNull Gui> guis, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }
    
    @Override
    public void bake() {
        List<List<SlotElement>> pages = new ArrayList<>();
        for (Gui gui : content) {
            List<SlotElement> page = new ArrayList<>(gui.getSize());
            for (int slot = 0; slot < gui.getSize(); slot++) {
                page.add(new SlotElement.LinkedSlotElement(gui, slot));
            }
            
            pages.add(page);
        }
        
        this.pages = pages;
        update();
    }
    
    public static final class Builder extends AbstractBuilder<Gui> {
        
        @Override
        public @NotNull PagedGui<Gui> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new PagedNestedGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
