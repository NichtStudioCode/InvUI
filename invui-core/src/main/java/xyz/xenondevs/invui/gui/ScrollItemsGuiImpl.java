package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link AbstractScrollGui} that uses {@link Item Items} as content.
 *
 * @see ScrollInventoryGuiImpl
 * @see ScrollNestedGuiImpl
 */
final class ScrollItemsGuiImpl extends AbstractScrollGui<Item> {
    
    private List<Item> items;
    
    /**
     * Creates a new {@link ScrollItemsGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The height of this Gui.
     * @param items            The {@link Item Items} to use.
     * @param contentListSlots The slots where content should be displayed.
     */
    public ScrollItemsGuiImpl(int width, int height, @Nullable List<@NotNull Item> items, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(items);
    }
    
    /**
     * Creates a new {@link ScrollItemsGuiImpl}.
     *
     * @param items     The {@link Item Items} to use.
     * @param structure The {@link Structure} to use.
     */
    public ScrollItemsGuiImpl(@Nullable List<@NotNull Item> items, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(items);
    }
    
    @Override
    public void setContent(@Nullable List<@NotNull Item> items) {
        this.items = items != null ? items : new ArrayList<>();
        update();
    }
    
    @Override
    protected List<SlotElement> getElements(int from, int to) {
        return items.subList(from, Math.min(items.size(), to)).stream()
            .map(SlotElement.ItemSlotElement::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public int getMaxLine() {
        return (int) Math.ceil((double) items.size() / (double) getLineLength()) - 1;
    }
    
    public static final class Builder extends AbstractBuilder<Item> {
        
        @Override
        public @NotNull ScrollGui<Item> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new ScrollItemsGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
