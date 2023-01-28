package xyz.xenondevs.invui.gui.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractPagedGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.builder.GuiBuilder;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A {@link AbstractPagedGui} where every page is its own {@link Gui}.
 *
 * @see GuiBuilder
 * @see PagedItemsGuiImpl
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public final class PagedNestedGuiImpl extends AbstractPagedGui<Gui> {
    
    private List<Gui> guis;
    
    /**
     * Creates a new {@link PagedNestedGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The height of this Gui.
     * @param guis             The {@link Gui Guis} to use as pages.
     * @param contentListSlots The slots where content should be displayed.
     * @deprecated Use {@link PagedGui#ofGuis(int, int, List, int...)} instead.
     */
    @Deprecated
    public PagedNestedGuiImpl(int width, int height, @Nullable List<Gui> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    /**
     * Creates a new {@link PagedNestedGuiImpl}.
     *
     * @param guis      The {@link Gui Guis} to use as pages.
     * @param structure The {@link Structure} to use.
     * @deprecated Use {@link PagedGui#ofGuis(Structure, List)} instead.
     */
    @Deprecated
    public PagedNestedGuiImpl(@Nullable List<Gui> guis, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }
    
    @Override
    public int getPageAmount() {
        return guis.size();
    }
    
    @Override
    public void setContent(@Nullable List<Gui> guis) {
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
    
}
