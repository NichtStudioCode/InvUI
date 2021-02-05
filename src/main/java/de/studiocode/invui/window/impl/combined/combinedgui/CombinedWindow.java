package de.studiocode.invui.window.impl.combined.combinedgui;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement.ItemStackHolder;
import de.studiocode.invui.util.SlotUtils;
import de.studiocode.invui.window.Window;
import de.studiocode.invui.window.impl.combined.BaseCombinedWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * A {@link Window} where top and player {@link Inventory} are affected by the same {@link GUI}.
 */
public abstract class CombinedWindow extends BaseCombinedWindow {
    
    private final GUI gui;
    
    public CombinedWindow(Player player, GUI gui, Inventory upperInventory, boolean closeable, boolean closeOnEvent) {
        super(player, gui.getSize(), upperInventory, closeable, closeOnEvent);
        this.gui = gui;
        
        gui.addParent(this);
        initUpperItems();
    }
    
    @Override
    public void handleSlotElementUpdate(GUI child, int slotIndex) {
        redrawItem(slotIndex, gui.getItemStackHolder(slotIndex), true);
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        int guiSlot = clicked == getUpperInventory() ? event.getSlot()
            : getUpperInventory().getSize() + SlotUtils.translatePlayerInvToGui(event.getSlot());
        
        gui.handleClick(guiSlot, (Player) event.getWhoClicked(), event.getClick(), event);
    }
    
    @Override
    protected ItemStackHolder getItemStackHolder(int index) {
        return gui.getItemStackHolder(index);
    }
    
    @Override
    public GUI[] getGuis() {
        return new GUI[] {gui};
    }
    
}
