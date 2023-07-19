package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * An {@link AbstractPagedGui} that is filled with {@link Item Items}.
 *
 * @see PagedNestedGuiImpl
 * @see PagedInventoriesGuiImpl
 */
final class PagedItemsGuiImpl extends AbstractPagedGui<Item> {
    
    private List<Item> items;
    private List<BiConsumer<Integer, Integer>> pageChangeHandlers;
    
    /**
     * Creates a new {@link PagedItemsGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The height of this Gui.
     * @param items            The {@link Item Items} to use as pages.
     * @param contentListSlots The slots where content should be displayed.
     */
    public PagedItemsGuiImpl(int width, int height, @Nullable List<@NotNull Item> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    /**
     * Creates a new {@link PagedItemsGuiImpl}.
     *
     * @param items     The {@link Item Items} to use as pages.
     * @param structure The {@link Structure} to use.
     */
    public PagedItemsGuiImpl(@Nullable List<@NotNull Item> items, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }
    
    @Override
    public int getPageAmount() {
        return (int) Math.ceil((double) items.size() / (double) getContentListSlots().length);
    }
    
    @Override
    public void setContent(@Nullable List<@NotNull Item> items) {
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
    
    public static final class Builder extends AbstractBuilder<Item> {
        
        @Override
        public @NotNull PagedGui<Item> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new PagedItemsGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
