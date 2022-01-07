package de.studiocode.invui.gui.builder;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.builder.guitype.GUIType;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@link GUIContext} contains all information from the {@link GUIBuilder} to be passed to
 * an instance of {@link GUIType} to create a new {@link GUI}.
 */
public class GUIContext {
    
    private final int width;
    private final int height;
    private Structure structure;
    private List<GUI> guis;
    private List<Item> items;
    private VirtualInventory inventory;
    
    public GUIContext(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public Structure getStructure() {
        return structure;
    }
    
    public void setStructure(@NotNull Structure structure) {
        this.structure = structure;
    }
    
    public List<GUI> getGuis() {
        return guis;
    }
    
    public void setGuis(@NotNull List<GUI> guis) {
        this.guis = guis;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    public void setItems(@NotNull List<Item> items) {
        this.items = items;
    }
    
    public VirtualInventory getInventory() {
        return inventory;
    }
    
    public void setInventory(@NotNull VirtualInventory inventory) {
        this.inventory = inventory;
    }
    
}
