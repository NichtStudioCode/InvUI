package de.studiocode.invui.gui.builder;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.builder.guitype.GUIType;
import de.studiocode.invui.gui.structure.Marker;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.ItemProvider;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;


/**
 * A builder class to easily construct {@link GUI}s.<br>
 * It provides similar functionality to Bukkit's {@link ShapedRecipe}, as it
 * allows for a structure String which defines the layout of the {@link GUI}.
 */
public class GUIBuilder<G extends GUI> {
    
    private final GUIType<G> guiType;
    private final GUIContext context;
    
    public GUIBuilder(@NotNull GUIType<G> guiType, int width, int height) {
        this.guiType = guiType;
        this.context = new GUIContext(width, height);
    }
    
    public GUIBuilder<G> setStructure(@NotNull String structureData) {
        context.setStructure(new Structure(structureData));
        return this;
    }
    
    public GUIBuilder<G> setStructure(@NotNull Structure structure) {
        context.setStructure(structure);
        return this;
    }
    
    public GUIBuilder<G> addIngredient(char key, @NotNull ItemProvider itemProvider) {
        context.getStructure().addIngredient(key, itemProvider);
        return this;
    }
    
    public GUIBuilder<G> addIngredient(char key, @NotNull Item item) {
        context.getStructure().addIngredient(key, item);
        return this;
    }
    
    public GUIBuilder<G> addIngredient(char key, @NotNull SlotElement element) {
        context.getStructure().addIngredient(key, element);
        return this;
    }
    
    public GUIBuilder<G> addIngredient(char key, @NotNull Marker marker) {
        context.getStructure().addIngredient(key, marker);
        return this;
    }
    
    public GUIBuilder<G> addIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        context.getStructure().addIngredient(key, itemSupplier);
        return this;
    }
    
    public GUIBuilder<G> addIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        context.getStructure().addIngredientElementSupplier(key, elementSupplier);
        return this;
    }
    
    public GUIBuilder<G> setItems(@NotNull List<Item> items) {
        if (!guiType.acceptsItems())
            throw new UnsupportedOperationException("Items cannot be set in this gui type.");
        context.setItems(items);
        return this;
    }
    
    public GUIBuilder<G> addItem(@NotNull Item item) {
        if (!guiType.acceptsItems())
            throw new UnsupportedOperationException("Items cannot be set in this gui type.");
        context.getItems().add(item);
        return this;
    }
    
    public GUIBuilder<G> setGUIs(@NotNull List<GUI> guis) {
        if (!guiType.acceptsGUIs())
            throw new UnsupportedOperationException("GUIs cannot be set in this gui type.");
        context.setGuis(guis);
        return this;
    }
    
    public GUIBuilder<G> addGUI(@NotNull GUI gui) {
        if (!guiType.acceptsGUIs())
            throw new UnsupportedOperationException("GUIs cannot be set in this gui type.");
        context.getGuis().add(gui);
        return this;
    }
    
    public G build() {
        if (context.getStructure() == null) throw new IllegalStateException("GUIContext has not been set yet.");
        return guiType.createGUI(context);
    }
    
}
