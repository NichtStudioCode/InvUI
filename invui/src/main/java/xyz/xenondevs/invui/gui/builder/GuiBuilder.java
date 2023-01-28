package xyz.xenondevs.invui.gui.builder;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.builder.guitype.GuiType;
import xyz.xenondevs.invui.gui.structure.Marker;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


/**
 * A builder class to easily construct {@link Gui}s.<br>
 * It provides similar functionality to Bukkit's {@link ShapedRecipe}, as it
 * allows for a structure String which defines the layout of the {@link Gui}.
 */
public class GuiBuilder<G extends Gui, C> {
    
    protected final GuiType<G, C> guiType;
    protected final GuiContext<C> context;
    
    public GuiBuilder(@NotNull GuiType<G, C> guiType) {
        this.guiType = guiType;
        this.context = new GuiContext<>();
    }
    
    public GuiBuilder<G, C> setStructure(int width, int height, @NotNull String structureData) {
        context.setStructure(new Structure(width, height, structureData));
        return this;
    }
    
    public GuiBuilder<G, C> setStructure(@NotNull String... structureData) {
        return setStructure(new Structure(structureData));
    }
    
    public GuiBuilder<G, C> setStructure(@NotNull Structure structure) {
        context.setStructure(structure);
        return this;
    }
    
    public GuiBuilder<G, C> addIngredient(char key, @NotNull ItemStack itemStack) {
        context.getStructure().addIngredient(key, itemStack);
        return this;
    }
    
    public GuiBuilder<G, C> addIngredient(char key, @NotNull ItemProvider itemProvider) {
        context.getStructure().addIngredient(key, itemProvider);
        return this;
    }
    
    public GuiBuilder<G, C> addIngredient(char key, @NotNull Item item) {
        context.getStructure().addIngredient(key, item);
        return this;
    }
    
    public GuiBuilder<G, C> addIngredient(char key, @NotNull VirtualInventory inventory) {
        context.getStructure().addIngredient(key, inventory);
        return this;
    }
    
    public GuiBuilder<G, C> addIngredient(char key, @NotNull VirtualInventory inventory, @Nullable ItemProvider background) {
        context.getStructure().addIngredient(key, inventory, background);
        return this;
    }
    
    public GuiBuilder<G, C> addIngredient(char key, @NotNull SlotElement element) {
        context.getStructure().addIngredient(key, element);
        return this;
    }
    
    public GuiBuilder<G, C> addIngredient(char key, @NotNull Marker marker) {
        context.getStructure().addIngredient(key, marker);
        return this;
    }
    
    public GuiBuilder<G, C> addIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        context.getStructure().addIngredient(key, itemSupplier);
        return this;
    }
    
    public GuiBuilder<G, C> addIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        context.getStructure().addIngredientElementSupplier(key, elementSupplier);
        return this;
    }
    
    public GuiBuilder<G, C> setBackground(@NotNull ItemProvider itemProvider) {
        context.setBackground(itemProvider);
        return this;
    }
    
    public GuiBuilder<G, C> setBackground(@NotNull ItemStack itemStack) {
        context.setBackground(new ItemWrapper(itemStack));
        return this;
    }
    
    public GuiBuilder<G, C> setContent(@NotNull List<C> content) {
        context.setContent(content);
        return this;
    }
    
    public GuiBuilder<G, C> addContent(@NotNull C content) {
        if (context.getContent() == null)
            context.setContent(new ArrayList<>());
        
        context.getContent().add(content);
        return this;
    }
    
    public G build() {
        if (context.getStructure() == null)
            throw new IllegalStateException("GuiContext has not been set yet.");
        
        return guiType.createGui(context);
    }
    
}
