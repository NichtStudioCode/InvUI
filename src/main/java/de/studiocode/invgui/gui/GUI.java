package de.studiocode.invgui.gui;

import de.studiocode.invgui.gui.SlotElement.ItemStackHolder;
import de.studiocode.invgui.gui.impl.*;
import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.virtualinventory.VirtualInventory;
import de.studiocode.invgui.window.Window;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * A GUI is a container for width * height {@link SlotElement}s.<br>
 * Each {@link SlotElement} can either be an {@link Item},
 * a reference to a {@link VirtualInventory}'s or another {@link GUI}'s
 * slot index.<br>
 * A {@link GUI} is not an {@link Inventory}, nor does
 * it access one. It just contains {@link SlotElement}s and their positions.<br>
 * In order to create an {@link Inventory} which is visible
 * to players, you will need to use a {@link Window}.
 *
 * @see BaseGUI
 * @see PagedGUI
 * @see SimpleGUI
 * @see SimplePagedItemsGUI
 * @see SimplePagedGUIs
 */
public interface GUI {
    
    /**
     * Gets the size of the {@link GUI}.
     *
     * @return The size of the gui.
     */
    int getSize();
    
    /**
     * Gets the width of the {@link GUI}
     *
     * @return The width of the {@link GUI}
     */
    int getWidth();
    
    /**
     * Gets the height of the {@link GUI}
     *
     * @return The height of the {@link GUI}
     */
    int getHeight();
    
    /**
     * Sets the {@link SlotElement} on these coordinates.
     * If you need to set an {@link Item}, please use {@link #setItem(int, int, Item)} instead.
     *
     * @param x           The x coordinate
     * @param y           The y coordinate
     * @param slotElement The {@link SlotElement} to be placed there.
     */
    void setSlotElement(int x, int y, @NotNull SlotElement slotElement);
    
    /**
     * Sets the {@link SlotElement} on these coordinates.
     * If you need to set an {@link Item}, please use {@link #setItem(int, Item)} instead.
     *
     * @param index       The slot index
     * @param slotElement The {@link SlotElement} to be placed there.
     */
    void setSlotElement(int index, @NotNull SlotElement slotElement);
    
    /**
     * Gets the {@link SlotElement} on these coordinates
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link SlotElement} placed there
     */
    SlotElement getSlotElement(int x, int y);
    
    /**
     * Gets the {@link SlotElement} placed on that slot
     *
     * @param index The slot index
     * @return The {@link SlotElement} placed on that slot
     */
    SlotElement getSlotElement(int index);
    
    /**
     * Gets a all {@link SlotElement}s of this {@link GUI} in an Array.
     *
     * @return All {@link SlotElement}s of this {@link GUI}
     */
    SlotElement[] getSlotElements();
    
    /**
     * Sets the {@link Item} on these coordinates.
     *
     * @param x    The x coordinate
     * @param y    The y coordinate
     * @param item The {@link Item} that should be placed on these coordinates
     *             or null to remove the {@link Item} that is currently there.
     */
    void setItem(int x, int y, Item item);
    
    /**
     * Sets the {@link Item} on that slot
     *
     * @param index The slot index
     * @param item  The {@link Item} that should be placed on that slot or null
     *              to remove the {@link Item} that is currently there.
     */
    void setItem(int index, Item item);
    
    /**
     * Gets the {@link Item} on these coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link Item} which is placed on that slot or null if there isn't one
     */
    Item getItem(int x, int y);
    
    /**
     * Gets the {@link Item} placed on that slot.
     *
     * @param index The slot index
     * @return The {@link Item} which is placed on that slot or null if there isn't one
     */
    Item getItem(int index);
    
    /**
     * Adds {@link Item}s to the gui.
     *
     * @param items The {@link Item}s that should be added to the gui
     */
    void addItems(@NotNull Item... items);
    
    /**
     * Gets the {@link ItemStackHolder} on these coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link ItemStackHolder} which is placed on that slot or null if there isn't one
     */
    ItemStackHolder getItemStackHolder(int x, int y);
    
    /**
     * Gets the {@link ItemStackHolder} placed on that slot.
     *
     * @param index The slot index
     * @return The {@link ItemStackHolder} which is placed on that slot or null if there isn't one
     */
    ItemStackHolder getItemStackHolder(int index);
    
    /**
     * Removes an {@link Item} by its coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    void remove(int x, int y);
    
    /**
     * Remove the {@link Item} which are placed on these slots.
     *
     * @param index The slot index of the {@link Item}s that should be removed
     */
    void remove(int index);
    
    /**
     * Fills the slots of this {@link GUI} with the content of another one,
     * allowing for nested {@link GUI}s
     *
     * @param offset Defines the index where the nested {@link GUI} should start (inclusive)
     * @param gui    The {@link GUI} which should be put inside this {@link GUI}
     */
    void nest(int offset, @NotNull GUI gui);
    
    /**
     * A method called if a slot in the {@link Inventory} has been clicked.
     *
     * @param slot      The slot that has been clicked
     * @param player    The {@link Player} that clicked
     * @param clickType The {@link ClickType}
     * @param event     The {@link InventoryClickEvent}
     */
    void handleClick(int slot, Player player, ClickType clickType, InventoryClickEvent event);
    
    // ---- fill methods ----
    
    /**
     * Fills the {@link GUI} with {@link Item}s.
     *
     * @param start           The start index of the fill (inclusive)
     * @param end             The end index of the fill (exclusive)
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fill(int start, int end, Item item, boolean replaceExisting);
    
    /**
     * Fills the entire {@link GUI} with {@link Item}s.
     *
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fill(Item item, boolean replaceExisting);
    
    /**
     * Fills one row with an specific {@link Item}
     *
     * @param row             The row
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fillRow(int row, Item item, boolean replaceExisting);
    
    /**
     * Fills one column with an specific {@link Item}
     *
     * @param column          The column
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fillColumn(int column, Item item, boolean replaceExisting);
    
    /**
     * Fills the borders of this {@link GUI} with an specific {@link Item}
     *
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fillBorders(Item item, boolean replaceExisting);
    
    /**
     * Fills a rectangle in this {@link GUI} with an specific {@link Item}
     *
     * @param x               The x coordinate where the rectangle should start.
     * @param y               The y coordinate where the rectangle should start.
     * @param width           The width of the rectangle.
     * @param height          The height of the rectangle
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fillRectangle(int x, int y, int width, int height, Item item, boolean replaceExisting);
    
}
