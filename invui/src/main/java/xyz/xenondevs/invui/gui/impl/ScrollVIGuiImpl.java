package xyz.xenondevs.invui.gui.impl;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.AbstractScrollGui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DeprecatedIsStillUsed")
public final class ScrollVIGuiImpl extends AbstractScrollGui<VirtualInventory> {
    
    private List<VirtualInventory> inventories;
    private List<SlotElement.VISlotElement> elements;
    
    /**
     * Creates a new {@link ScrollVIGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The width of this Gui.
     * @param inventories      The {@link VirtualInventory VirtualInventories} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @deprecated Use {@link ScrollGui#ofInventories(int, int, List, int...)} instead.
     */
    @Deprecated
    public ScrollVIGuiImpl(int width, int height, @NotNull List<VirtualInventory> inventories, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(inventories);
    }
    
    /**
     * Creates a new {@link ScrollVIGuiImpl}.
     *
     * @param inventories The {@link VirtualInventory VirtualInventories} to use.
     * @param structure   The {@link Structure} to use.
     * @deprecated Use {@link ScrollGui#ofInventories(Structure, List)} instead.
     */
    @Deprecated
    public ScrollVIGuiImpl(@NotNull List<VirtualInventory> inventories, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(inventories);
    }
    
    @Override
    public void setContent(@NotNull List<VirtualInventory> inventories) {
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
