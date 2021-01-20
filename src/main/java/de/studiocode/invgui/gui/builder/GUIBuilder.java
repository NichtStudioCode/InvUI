package de.studiocode.invgui.gui.builder;

import de.studiocode.invgui.gui.GUI;
import de.studiocode.invgui.item.Item;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class to easily construct {@link GUI}s.<br>
 * It provides similar functionality to Bukkit's {@link ShapedRecipe}, as it
 * allows for a structure String which defines the layout of the {@link GUI}.
 */
public interface GUIBuilder {
    
    /**
     * Sets the structure of the {@link GUI}.
     * The structure is a {@link String} of characters, like used in {@link ShapedRecipe} to
     * define where which {@link Item} should be.
     *
     * @param structure The structure {@link String}
     */
    void setStructure(@NotNull String structure);
    
    /**
     * Sets an ingredient for the structure String, which will later be
     * used to set up the inventory correctly.
     *
     * @param c    The ingredient key
     * @param item The {@link Item}
     */
    void setIngredient(char c, @NotNull Item item);
    
    /**
     * Builds the {@link GUI}.
     *
     * @return The built {@link GUI}
     */
    GUI build();
    
}
