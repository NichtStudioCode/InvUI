package de.studiocode.invui.window.impl.combined.splitgui;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement.ItemStackHolder;
import de.studiocode.invui.util.SlotUtils;
import de.studiocode.invui.window.impl.combined.BaseCombinedWindow;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class SplitGUIWindow extends BaseCombinedWindow {
    
    private final GUI upperGui;
    private final GUI lowerGui;
    
    public SplitGUIWindow(Player player, GUI upperGui, GUI lowerGui, Inventory upperInventory, boolean closeable, boolean closeOnEvent) {
        super(player, upperGui.getSize() + lowerGui.getSize(), upperInventory, closeable, closeOnEvent);
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
        
        upperGui.addParent(this);
        lowerGui.addParent(this);
        initUpperItems();
    }
    
    @Override
    public void handleSlotElementUpdate(GUI child, int slotIndex) {
        redrawItem(child == upperGui ? slotIndex : upperGui.getSize() + slotIndex,
            child.getItemStackHolder(slotIndex), true);
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        Inventory clicked = event.getClickedInventory();
        if (clicked == getUpperInventory()) {
            upperGui.handleClick(event.getSlot(), (Player) event.getWhoClicked(), event.getClick(), event);
        } else {
            int index = SlotUtils.translatePlayerInvToGui(event.getSlot());
            lowerGui.handleClick(index, (Player) event.getWhoClicked(), event.getClick(), event);
        }
    }
    
    @Override
    public ItemStackHolder getItemStackHolder(int index) {
        if (index >= upperGui.getSize()) return lowerGui.getItemStackHolder(index - upperGui.getSize());
        else return upperGui.getItemStackHolder(index);
    }
    
    @Override
    public GUI[] getGuis() {
        return new GUI[] {upperGui, lowerGui};
    }
    
}
