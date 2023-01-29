package xyz.xenondevs.invui.gui.builder;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.structure.Marker;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * A builder class to easily construct {@link Gui Guis}.<br>
 * It provides similar functionality to Bukkit's {@link ShapedRecipe}, as it
 * allows for a structure String which defines the layout of the {@link Gui}.
 */
public abstract class AbstractGuiBuilder<G extends Gui, S extends AbstractGuiBuilder<G, S>> implements GuiBuilder<G> {
    
    protected Structure structure;
    protected ItemProvider background;
    protected List<Consumer<Gui>> modifiers;
    
    @Contract("_, _, _, -> this")
    public S setStructure(int width, int height, @NotNull String structureData) {
        structure = new Structure(width, height, structureData);
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setStructure(@NotNull String... structureData) {
        structure = new Structure(structureData);
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setStructure(@NotNull Structure structure) {
        this.structure = structure;
        return getThis();
    }
    
    @Contract("_, _ -> this")
    public S addIngredient(char key, @NotNull ItemStack itemStack) {
        structure.addIngredient(key, itemStack);
        return getThis();
    }
    
    @Contract("_, _ -> this")
    public S addIngredient(char key, @NotNull ItemProvider itemProvider) {
        structure.addIngredient(key, itemProvider);
        return getThis();
    }
    
    @Contract("_, _ -> this")
    public S addIngredient(char key, @NotNull Item item) {
        structure.addIngredient(key, item);
        return getThis();
    }
    
    @Contract("_, _ -> this")
    public S addIngredient(char key, @NotNull VirtualInventory inventory) {
        structure.addIngredient(key, inventory);
        return getThis();
    }
    
    @Contract("_, _, _ -> this")
    public S addIngredient(char key, @NotNull VirtualInventory inventory, @Nullable ItemProvider background) {
        structure.addIngredient(key, inventory, background);
        return getThis();
    }
    
    @Contract("_, _ -> this")
    public S addIngredient(char key, @NotNull SlotElement element) {
        structure.addIngredient(key, element);
        return getThis();
    }
    
    @Contract("_, _ -> this")
    public S addIngredient(char key, @NotNull Marker marker) {
        structure.addIngredient(key, marker);
        return getThis();
    }
    
    @Contract("_, _ -> this")
    public S addIngredient(char key, @NotNull Supplier<? extends Item> itemSupplier) {
        structure.addIngredient(key, itemSupplier);
        return getThis();
    }
    
    @Contract("_, _ -> this")
    public S addIngredientElementSupplier(char key, @NotNull Supplier<? extends SlotElement> elementSupplier) {
        structure.addIngredientElementSupplier(key, elementSupplier);
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setBackground(@NotNull ItemProvider itemProvider) {
        background = itemProvider;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setBackground(@NotNull ItemStack itemStack) {
        background = new ItemWrapper(itemStack);
        return getThis();
    }
    
    @Contract("_ -> this")
    public S addModifier(@NotNull Consumer<@NotNull Gui> modifier) {
        if (modifiers == null)
            modifiers = new ArrayList<>();
        
        modifiers.add(modifier);
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setModifiers(@NotNull List<@NotNull Consumer<@NotNull Gui>> modifiers) {
        this.modifiers = modifiers;
        return getThis();
    }
    
    protected void applyModifiers(@NotNull G gui) {
        if (background != null) {
            gui.setBackground(background);
        }
        if (modifiers != null) {
            modifiers.forEach(modifier -> modifier.accept(gui));
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public @NotNull AbstractGuiBuilder<G, S> clone() {
        try {
            var clone = (AbstractGuiBuilder<G, S>) super.clone();
            clone.structure = structure.clone();
            if (modifiers != null)
                clone.modifiers = new ArrayList<>(modifiers);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    @Contract(value = "-> this", pure = true)
    protected abstract S getThis();
    
}
