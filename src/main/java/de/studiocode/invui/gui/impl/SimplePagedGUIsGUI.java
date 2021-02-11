package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.builder.GUIBuilder;
import de.studiocode.invui.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link PagedGUI} where every page is it's own {@link GUI}.
 *
 * @see GUIBuilder
 * @see SimplePagedItemsGUI
 */
public class SimplePagedGUIsGUI extends PagedGUI {
    
    private List<GUI> guis;
    
    public SimplePagedGUIsGUI(int width, int height, @Nullable List<GUI> guis, int... itemListSlots) {
        super(width, height, false, itemListSlots);
        this.guis = guis == null ? new ArrayList<>() : guis;
        
        update();
    }
    
    public SimplePagedGUIsGUI(int width, int height, @Nullable List<GUI> guis, @NotNull Structure structure) {
        super(width, height, false, structure);
        this.guis = guis == null ? new ArrayList<>() : guis;
        
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
