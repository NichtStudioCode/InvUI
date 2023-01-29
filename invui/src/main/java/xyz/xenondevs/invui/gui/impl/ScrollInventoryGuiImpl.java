package xyz.xenondevs.invui.gui.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractScrollGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.builder.GuiType;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link AbstractScrollGui} that uses {@link VirtualInventory VirtualInventories} as content.
 * 
 * @see GuiType
 * @see ScrollItemsGuiImpl
 * @see ScrollNestedGuiImpl
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public final class ScrollInventoryGuiImpl extends AbstractScrollGui<VirtualInventory> {
    
    private List<VirtualInventory> inventories;
    private List<SlotElement.VISlotElement> elements;
    
    /**
     * Creates a new {@link ScrollInventoryGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The width of this Gui.
     * @param inventories      The {@link VirtualInventory VirtualInventories} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @deprecated Use {@link ScrollGui#ofInventories(int, int, List, int...)} instead.
     */
    @Deprecated
    public ScrollInventoryGuiImpl(int width, int height, @Nullable List<@NotNull VirtualInventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    /**
     * Creates a new {@link ScrollInventoryGuiImpl}.
     *
     * @param inventories The {@link VirtualInventory VirtualInventories} to use.
     * @param structure   The {@link Structure} to use.
     * @deprecated Use {@link ScrollGui#ofInventories(Structure, List)} instead.
     */
    @Deprecated
    public ScrollInventoryGuiImpl(@Nullable List<@NotNull VirtualInventory> inventories, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(inventories);
    }
    
    @Override
    public void setContent(@Nullable List<@NotNull VirtualInventory> inventories) {
        this.inventories = inventories != null ? inventories : new ArrayList<>();
        updateElements();
        update();
    }
    
    private void updateElements() {
        elements = new ArrayList<>();
        for (VirtualInventory inventory : inventories) {
            for (int i = 0; i < inventory.getSize(); i++) {
                elements.add(new SlotElement.VISlotElement(inventory, i));
            }
        }
    }
    
    @Override
    protected List<SlotElement.VISlotElement> getElements(int from, int to) {
        return elements.subList(from, Math.min(elements.size(), to));
    }
    
    @Override
    public int getMaxLine() {
        if (elements == null) return 0;
        return (int) Math.ceil((double) elements.size() / (double) getLineLength()) - 1;
    }
    
}
