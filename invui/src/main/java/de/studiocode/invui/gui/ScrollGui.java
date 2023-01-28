package de.studiocode.invui.gui;

import de.studiocode.invui.gui.impl.ScrollItemsGuiImpl;
import de.studiocode.invui.gui.impl.ScrollNestedGuiImpl;
import de.studiocode.invui.gui.impl.ScrollVIGuiImpl;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
public interface ScrollGui<C> extends Gui {
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param width            The width of the {@link ScrollGui}.
     * @param height           The height of the {@link ScrollGui}.
     * @param items            The {@link Item Items} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<Item> ofItems(int width, int height, @NotNull List<@NotNull Item> items, int... contentListSlots) {
        return new ScrollItemsGuiImpl(width, height, items, contentListSlots);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param items     The {@link Item Items} to use.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<Item> ofItems(@NotNull Structure structure, @NotNull List<@NotNull Item> items) {
        return new ScrollItemsGuiImpl(items, structure);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param width            The width of the {@link ScrollGui}.
     * @param height           The height of the {@link ScrollGui}.
     * @param guis             The {@link Gui Guis} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<Gui> ofGuis(int width, int height, @NotNull List<@NotNull Gui> guis, int... contentListSlots) {
        return new ScrollNestedGuiImpl(width, height, guis, contentListSlots);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param structure The {@link Structure} to use.
     * @param guis      The {@link Gui Guis} to use.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<Gui> ofGuis(Structure structure, @NotNull List<@NotNull Gui> guis) {
        return new ScrollNestedGuiImpl(guis, structure);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param width            The width of the {@link ScrollGui}.
     * @param height           The height of the {@link ScrollGui}.
     * @param inventories      The {@link VirtualInventory VirtualInventories} to use.
     * @param contentListSlots The slots where content should be displayed.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<VirtualInventory> ofInventories(int width, int height, @NotNull List<@NotNull VirtualInventory> inventories, int... contentListSlots) {
        return new ScrollVIGuiImpl(width, height, inventories, contentListSlots);
    }
    
    /**
     * Creates a new {@link ScrollGui}.
     *
     * @param structure   The {@link Structure} to use.
     * @param inventories The {@link VirtualInventory VirtualInventories} to use.
     * @return The created {@link ScrollGui}.
     */
    static @NotNull ScrollGui<VirtualInventory> ofInventories(@NotNull Structure structure, @NotNull List<@NotNull VirtualInventory> inventories) {
        return new ScrollVIGuiImpl(inventories, structure);
    }
    
    /**
     * Gets the current line of this {@link ScrollGui}.
     *
     * @return The current line of this {@link ScrollGui}.
     */
    int getCurrentLine();
    
    /**
     * Gets the max line index of this {@link ScrollGui}.
     *
     * @return The max line index of this {@link ScrollGui}.
     */
    int getMaxLine();
    
    /**
     * Sets the current line of this {@link ScrollGui}.
     *
     * @param line The line to set.
     */
    void setCurrentLine(int line);
    
    /**
     * Checks if it is possible to scroll the specified amount of lines.
     *
     * @return Whether it is possible to scroll the specified amount of lines.
     */
    boolean canScroll(int lines);
    
    /**
     * Scrolls the specified amount of lines.
     *
     * @param lines The amount of lines to scroll.
     */
    void scroll(int lines);
    
    /**
     * Sets the content of this {@link ScrollGui} for all lines.
     *
     * @param content The content to set.
     */
    void setContent(@NotNull List<@NotNull C> content);
    
}
