package de.studiocode.invgui.gui.impl;

import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.gui.SlotElement;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;

import java.util.Arrays;
import java.util.List;

public class SimplePagedGUIs extends PagedGUI {
    
    private final List<GUI> guis;
    
    public SimplePagedGUIs(int width, int height, int backItemSlot, ItemBuilder backBuilder, int forwardItemSlot,
                           ItemBuilder forwardBuilder, List<GUI> guis, int... itemListSlots) {
        super(width, height, false, itemListSlots);
        this.guis = guis;
        
        System.out.println("control slot " + backItemSlot + " fwd " + forwardItemSlot);
        
        setControlItems(backItemSlot, new BackItem(backBuilder), forwardItemSlot, new ForwardItem(forwardBuilder));
        update();
    }
    
    @Override
    protected int getPageAmount() {
        return guis.size();
    }
    
    @Override
    protected List<SlotElement> getPageItems(int page) {
        return Arrays.asList(guis.get(page).getSlotElements());
    }
    
}
