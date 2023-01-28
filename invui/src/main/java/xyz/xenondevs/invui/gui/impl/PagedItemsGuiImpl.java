package xyz.xenondevs.invui.gui.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractPagedGui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.builder.GuiBuilder;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * A {@link AbstractPagedGui} that is filled with {@link Item}s.
 *
 * @see GuiBuilder
 * @see PagedNestedGuiImpl
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public final class PagedItemsGuiImpl extends AbstractPagedGui<Item> {
    
    private List<Item> items;
    private List<BiConsumer<Integer, Integer>> pageChangeHandlers;
    
    /**
     * Creates a new {@link PagedItemsGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The height of this Gui.
     * @param items            The {@link Item Items} to use as pages.
     * @param contentListSlots The slots where content should be displayed.
     * @deprecated Use {@link PagedGui#ofItems(int, int, List, int...)} instead.
     */
    @Deprecated
    public PagedItemsGuiImpl(int width, int height, @Nullable List<Item> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    /**
     * Creates a new {@link PagedItemsGuiImpl}.
     *
     * @param items     The {@link Item Items} to use as pages.
     * @param structure The {@link Structure} to use.
     * @deprecated Use {@link PagedGui#ofItems(Structure, List)} instead.
     */
    @Deprecated
    public PagedItemsGuiImpl(@Nullable List<Item> items, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }
    
    @Override
    public int getPageAmount() {
        return (int) Math.ceil((double) items.size() / (double) getContentListSlots().length);
    }
    
    @Override
    public void setContent(List<@Nullable Item> content) {
        this.items = items != null ? items : new ArrayList<>();
        update();
    }
    
    @Override
    protected List<SlotElement> getPageElements(int page) {
        int length = getContentListSlots().length;
        int from = page * length;
        int to = Math.min(from + length, items.size());
        
        return items.subList(from, to).stream().map(SlotElement.ItemSlotElement::new).collect(Collectors.toList());
    }
    
}
