package de.studiocode.invui.window;

import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.util.Pair;
import de.studiocode.invui.util.SlotUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * A {@link Window} where top and player {@link Inventory} are affected by the same {@link GUI}.
 */
public abstract class AbstractMergedWindow extends AbstractDoubleWindow {
    
    private final AbstractGUI gui;
    
    public AbstractMergedWindow(Player player, ComponentWrapper title, AbstractGUI gui, Inventory upperInventory, boolean closeable, boolean retain) {
        super(player, title, gui.getSize(), upperInventory, closeable, retain);
        this.gui = gui;
        
        gui.addParent(this);
        initUpperItems();
    }
    
    @Override
    public void handleSlotElementUpdate(GUI child, int slotIndex) {
        redrawItem(slotIndex, gui.getSlotElement(slotIndex), true);
    }
    
    @Override
    protected SlotElement getSlotElement(int index) {
        return gui.getSlotElement(index);
    }
    
    @Override
    protected Pair<AbstractGUI, Integer> getWhereClicked(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        int slot = event.getSlot();
        int clickedIndex = clicked == getUpperInventory() ? slot
            : getUpperInventory().getSize() + SlotUtils.translatePlayerInvToGui(slot);
        return new Pair<>(gui, clickedIndex);
    }
    
    @Override
    protected Pair<AbstractGUI, Integer> getGUIAt(int index) {
        return index < gui.getSize() ? new Pair<>(gui, index) : null;
    }
    
    @Override
    public AbstractGUI[] getGUIs() {
        return new AbstractGUI[] {gui};
    }
    
}
