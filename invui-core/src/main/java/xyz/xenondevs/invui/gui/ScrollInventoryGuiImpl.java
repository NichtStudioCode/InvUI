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
 * A {@link AbstractScrollGui} that uses {@link Inventory VirtualInventories} as content.
 *
 * @see ScrollItemsGuiImpl
 * @see ScrollNestedGuiImpl
 */
final class ScrollInventoryGuiImpl extends AbstractScrollGui<Inventory> {
    
    private final @NotNull BiConsumer<@NotNull Integer, @NotNull Integer> resizeHandler = (from, to) -> bake();
    
    /**
     * Creates a new {@link ScrollInventoryGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The width of this Gui.
     * @param inventories      The {@link Inventory VirtualInventories} to use.
     * @param contentListSlots The slots where content should be displayed.
     */
    public ScrollInventoryGuiImpl(int width, int height, @Nullable List<@NotNull Inventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    /**
     * Creates a new {@link ScrollInventoryGuiImpl}.
     *
     * @param inventories The {@link Inventory VirtualInventories} to use.
     * @param structure   The {@link Structure} to use.
     */
    public ScrollInventoryGuiImpl(@Nullable List<@NotNull Inventory> inventories, @NotNull Structure structure) {
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
        List<SlotElement> elements = new ArrayList<>();
        for (Inventory inventory : content) {
            for (int i = 0; i < inventory.getSize(); i++) {
                elements.add(new SlotElement.InventorySlotElement(inventory, i));
            }
        }
        
        this.elements = elements;
        update();
    }
    
    public static final class Builder extends AbstractBuilder<Inventory> {
        
        @Override
        public @NotNull ScrollGui<Inventory> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new ScrollInventoryGuiImpl(content, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
