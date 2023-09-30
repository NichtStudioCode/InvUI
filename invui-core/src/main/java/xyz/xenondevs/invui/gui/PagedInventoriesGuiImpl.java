package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * An {@link AbstractPagedGui} where every page is its own {@link Inventory}.
 *
 * @see PagedItemsGuiImpl
 * @see PagedNestedGuiImpl
 */
final class PagedInventoriesGuiImpl extends AbstractPagedGui<Inventory> {
    
    private final @NotNull BiConsumer<@NotNull Integer, @NotNull Integer> resizeHandler = (from, to) -> bake();
    
    /**
     * Creates a new {@link PagedInventoriesGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The height of this Gui.
     * @param inventories      The {@link Inventory Inventories} to use as pages.
     * @param contentListSlots The slots where content should be displayed.
     */
    public PagedInventoriesGuiImpl(int width, int height, @Nullable List<@NotNull Inventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    /**
     * Creates a new {@link PagedInventoriesGuiImpl}.
     *
     * @param inventories The {@link Inventory Inventories} to use as pages.
     * @param structure   The {@link Structure} to use.
     */
    public PagedInventoriesGuiImpl(@Nullable List<@NotNull Inventory> inventories, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(inventories);
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    public void setContent(@Nullable List<Inventory> content) {
        // remove resize handlers from previous inventories
        if (this.content != null) {
            for (Inventory inventory : this.content) {
                if (inventory instanceof VirtualInventory) {
                    ((VirtualInventory) inventory).removeResizeHandler(resizeHandler);
                }
            }
        }
        
        // set content, bake pages, update
        super.setContent(content);
        
        // add resize handlers to new inventories
        if (this.content != null) {
            for (Inventory inventory : this.content) {
                if (inventory instanceof VirtualInventory) {
                    ((VirtualInventory) inventory).addResizeHandler(resizeHandler);
                }
            }
        }
    }
    
    @Override
    public void bake() {
        int contentSize = getContentListSlots().length;
        
        List<List<SlotElement>> pages = new ArrayList<>();
        List<SlotElement> page = new ArrayList<>(contentSize);
        
        for (Inventory inventory : content) {
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                page.add(new SlotElement.InventorySlotElement(inventory, slot));
                
                if (page.size() >= contentSize) {
                    pages.add(page);
                    page = new ArrayList<>(contentSize);
                }
            }
        }
        
        if (!page.isEmpty()) {
            pages.add(page);
        }
        
        this.pages = pages;
        update();
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
