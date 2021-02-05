package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.builder.PagedGUIBuilder;
import de.studiocode.invui.item.impl.pagedgui.BackItem;
import de.studiocode.invui.item.impl.pagedgui.ForwardItem;
import de.studiocode.invui.item.itembuilder.ItemBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * A {@link PagedGUI} where every page is it's own {@link GUI}.
 *
 * @see PagedGUIBuilder
 * @see SimplePagedItemsGUI
 */
public class SimplePagedGUIsGUI extends PagedGUI {
    
    private List<GUI> guis;
    
    public SimplePagedGUIsGUI(int width, int height,
                              int backItemSlot, Function<PagedGUI, ItemBuilder> backFunction,
                              int forwardItemSlot, Function<PagedGUI, ItemBuilder> forwardFunction,
                              List<GUI> guis, int... itemListSlots) {
        
        super(width, height, false, itemListSlots);
        this.guis = guis;
        
        setControlItems(backItemSlot, new BackItem(this, backFunction),
            forwardItemSlot, new ForwardItem(this, forwardFunction));
        
        update();
    }
    
    @Override
    public int getPageAmount() {
        return guis.size();
    }
    
    @Override
    protected List<SlotElement> getPageElements(int page) {
        return Arrays.asList(guis.get(page).getSlotElements());
    }
    
    public void setGuis(List<GUI> guis) {
        this.guis = guis;
        update();
    }
    
}
