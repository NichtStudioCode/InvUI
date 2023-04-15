package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A {@link AbstractPagedGui} where every page is its own {@link Gui}.
 *
 * @see PagedItemsGuiImpl
 */
final class PagedNestedGuiImpl extends AbstractPagedGui<Gui> {
    
    private List<Gui> guis;
    
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
    public int getPageAmount() {
        return guis.size();
    }
    
    @Override
    public void setContent(@Nullable List<@NotNull Gui> guis) {
        this.guis = guis == null ? new ArrayList<>() : guis;
        update();
    }
    
    @Override
    protected List<SlotElement> getPageElements(int page) {
        if (guis.size() <= page) return new ArrayList<>();
        
        Gui gui = guis.get(page);
        int size = gui.getSize();
        
        return IntStream.range(0, size)
            .mapToObj(i -> new SlotElement.LinkedSlotElement(gui, i))
            .collect(Collectors.toList());
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
