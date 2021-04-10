package de.studiocode.invui.gui.builder;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.impl.*;
import de.studiocode.invui.gui.structure.Marker;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemBuilder;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static de.studiocode.invui.gui.builder.GUIType.*;

/**
 * A builder class to easily construct {@link GUI}s.<br>
 * It provides similar functionality to Bukkit's {@link ShapedRecipe}, as it
 * allows for a structure String which defines the layout of the {@link GUI}.
 */
public class GUIBuilder {
    
    private final GUIType guiType;
    private final int width;
    private final int height;
    
    private Structure structure;
    
    private List<Item> items = new ArrayList<>();
    private List<GUI> guis = new ArrayList<>();
    
    public GUIBuilder(@NotNull GUIType guiType, int width, int height) {
        this.guiType = guiType;
        this.width = width;
        this.height = height;
    }
    
    public GUIBuilder setStructure(@NotNull String structureData) {
        this.structure = new Structure(structureData);
        return this;
    }
    
    public GUIBuilder setStructure(@NotNull Structure structure) {
        this.structure = structure;
        return this;
    }
    
    public GUIBuilder addIngredient(char key, @NotNull ItemBuilder itemBuilder) {
        structure.addIngredient(key, itemBuilder);
        return this;
    }
    
    public GUIBuilder addIngredient(char key, @NotNull Item item) {
        structure.addIngredient(key, item);
        return this;
    }
    
    public GUIBuilder addIngredient(char key, @NotNull SlotElement element) {
        structure.addIngredient(key, element);
        return this;
    }
    
    public GUIBuilder addIngredient(char key, @NotNull Marker marker) {
        structure.addIngredient(key, marker);
        return this;
    }
    
    public GUIBuilder addIngredient(char key, @NotNull Supplier<Item> itemSupplier) {
        structure.addIngredient(key, itemSupplier);
        return this;
    }
    
    public GUIBuilder setItems(@NotNull List<Item> items) {
        if (guiType != PAGED_ITEMS && guiType != SCROLL)
            throw new UnsupportedOperationException("Items cannot be set in this gui type.");
        this.items = items;
        return this;
    }
    
    public GUIBuilder addItem(@NotNull Item item) {
        if (guiType != PAGED_ITEMS && guiType != SCROLL)
            throw new UnsupportedOperationException("Items cannot be set in this gui type.");
        items.add(item);
        return this;
    }
    
    public GUIBuilder setGUIs(@NotNull List<GUI> guis) {
        if (guiType != PAGED_GUIs)
            throw new UnsupportedOperationException("GUIs cannot be set in this gui type.");
        this.guis = guis;
        return this;
    }
    
    public GUIBuilder addGUI(@NotNull GUI gui) {
        if (guiType != PAGED_GUIs && guiType != TAB)
            throw new UnsupportedOperationException("GUIs cannot be set in this gui type.");
        guis.add(gui);
        return this;
    }
    
    public GUI build() {
        switch (guiType) {
            
            case NORMAL:
                return buildSimpleGUI();
            
            case PAGED_ITEMS:
                return buildSimplePagedItemsGUI();
            
            case PAGED_GUIs:
                return buildSimplePagedGUIsGUI();
            
            case TAB:
                return buildSimpleTabGUI();
            
            case SCROLL:
                return buildSimpleScrollGUI();
            
            default:
                throw new UnsupportedOperationException("Unknown GUI type");
        }
    }
    
    private SimpleGUI buildSimpleGUI() {
        return new SimpleGUI(width, height, structure);
    }
    
    private SimplePagedItemsGUI buildSimplePagedItemsGUI() {
        return new SimplePagedItemsGUI(width, height, items, structure);
    }
    
    private SimplePagedGUIsGUI buildSimplePagedGUIsGUI() {
        return new SimplePagedGUIsGUI(width, height, guis, structure);
    }
    
    private SimpleTabGUI buildSimpleTabGUI() {
        return new SimpleTabGUI(width, height, guis, structure);
    }
    
    private SimpleScrollGUI buildSimpleScrollGUI() {
        return new SimpleScrollGUI(width, height, items, structure);
    }
    
}
