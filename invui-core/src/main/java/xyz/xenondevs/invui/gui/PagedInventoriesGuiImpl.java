package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * An {@link AbstractPagedGui} where every page is its own {@link Inventory}.
 *
 * @see PagedItemsGuiImpl
 * @see PagedNestedGuiImpl
 */
final class PagedInventoriesGuiImpl extends AbstractPagedGui<Inventory> {
    
    private List<Inventory> inventories;
    
    /**
     * Creates a new {@link PagedInventoriesGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The height of this Gui.
     * @param inventories             The {@link Inventory Inventories} to use as pages.
     * @param contentListSlots The slots where content should be displayed.
     */
    public PagedInventoriesGuiImpl(int width, int height, @Nullable List<@NotNull Inventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    /**
     * Creates a new {@link PagedInventoriesGuiImpl}.
     *
     * @param inventories      The {@link Inventory Inventories} to use as pages.
     * @param structure The {@link Structure} to use.
     */
    public PagedInventoriesGuiImpl(@Nullable List<@NotNull Inventory> inventories, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(inventories);
    }
    
    @Override
    public int getPageAmount() {
        return inventories.size();
    }
    
    @Override
    public void setContent(@Nullable List<@NotNull Inventory> inventories) {
        this.inventories = inventories == null ? new ArrayList<>() : inventories;
        update();
    }
    
    @Override
    protected List<SlotElement> getPageElements(int page) {
        if (inventories.size() <= page) return new ArrayList<>();
        
        Inventory inventory = inventories.get(page);
        int size = inventory.getSize();
        
        return IntStream.range(0, size)
            .mapToObj(i -> new SlotElement.InventorySlotElement(inventory, i))
            .collect(Collectors.toList());
    }
    
    public static final class Builder extends AbstractBuilder<Inventory> {
        
        @Override
        public @NotNull PagedGui<Inventory> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new PagedInventoriesGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
