package de.studiocode.invui.gui;

import de.studiocode.invui.animation.Animation;
import de.studiocode.invui.gui.builder.GUIBuilder;
import de.studiocode.invui.gui.impl.*;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.virtualinventory.VirtualInventory;
import de.studiocode.invui.window.Window;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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
 * @see SimplePagedGUIsGUI
 * @see SimpleTabGUI
 * @see GUIBuilder
 */
public interface GUI extends GUIParent {
    
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
    void setSlotElement(int x, int y, @Nullable SlotElement slotElement);
    
    /**
     * Sets the {@link SlotElement} on these coordinates.
     * If you need to set an {@link Item}, please use {@link #setItem(int, Item)} instead.
     *
     * @param index       The slot index
     * @param slotElement The {@link SlotElement} to be placed there.
     */
    void setSlotElement(int index, @Nullable SlotElement slotElement);
    
    /**
     * Gets the {@link SlotElement} on these coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link SlotElement} placed there
     */
    SlotElement getSlotElement(int x, int y);
    
    /**
     * Gets the {@link SlotElement} placed on that slot.
     *
     * @param index The slot index
     * @return The {@link SlotElement} placed on that slot
     */
    SlotElement getSlotElement(int index);
    
    /**
     * Gets if there is a {@link SlotElement} on these coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return If there is a {@link SlotElement} placed there
     */
    boolean hasSlotElement(int x, int y);
    
    /**
     * Gets if there is a {@link SlotElement} placed on that slot.
     *
     * @param index The slot index
     * @return If there is a {@link SlotElement} placed there
     */
    boolean hasSlotElement(int index);
    
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
     * Applies the given {@link Structure} to the {@link GUI}.
     *
     * @param structure The structure
     */
    void applyStructure(Structure structure);
    
    /**
     * A method called if a slot in the {@link Inventory} has been clicked.
     *
     * @param slot      The slot that has been clicked
     * @param player    The {@link Player} that clicked
     * @param clickType The {@link ClickType}
     * @param event     The {@link InventoryClickEvent}
     */
    void handleClick(int slot, Player player, ClickType clickType, InventoryClickEvent event);
    
    /**
     * A method called when an {@link ItemStack} has been shift-clicked into this
     * {@link GUI}.
     *
     * @param event The {@link InventoryClickEvent} associated with this action
     */
    void handleItemShift(InventoryClickEvent event);
    
    /**
     * A method called when an {@link ItemStack} has been dragged over the {@link GUI}.
     *
     * @param player   The player that is responsible for this action
     * @param slot     The slot index
     * @param oldStack The {@link ItemStack} that was previously on that slot
     * @param newStack The new {@link ItemStack} that would be there if the action isn't cancelled
     * @return If the action has been cancelled
     */
    boolean handleItemDrag(Player player, int slot, ItemStack oldStack, ItemStack newStack);
    
    /**
     * Adds a {@link GUIParent} to the set of {@link GUIParent}s.
     *
     * @param parent The {@link GUIParent} to add
     */
    void addParent(@NotNull GUIParent parent);
    
    /**
     * Removes a {@link GUIParent} from the set of {@link GUIParent}s
     *
     * @param parent The {@link GUIParent} to remove
     */
    void removeParent(@NotNull GUIParent parent);
    
    /**
     * Gets all {@link GUIParent}s.
     *
     * @return The {@link GUIParent}s of this {@link GUI}
     */
    Set<GUIParent> getParents();
    
    /**
     * Finds all {@link Window}s that show this {@link GUI}.
     *
     * @return The list of {@link Window} that show this {@link GUI}
     */
    List<Window> findAllWindows();
    
    /**
     * Finds all {@link Player}s that are currently seeing this {@link Window}.
     *
     * @return The list of {@link Player}s that are currently seeing this {@link Window}
     */
    Set<Player> findAllCurrentViewers();
    
    /**
     * Plays an {@link Animation}.
     *
     * @param animation The {@link Animation} to play.
     * @param filter    The filter that selects which {@link SlotElement}s should be animated.
     */
    void playAnimation(@NotNull Animation animation, @Nullable Predicate<SlotElement> filter);
    
    /**
     * Cancels the running {@link Animation} if there is one.
     */
    void cancelAnimation();
    
    // ---- fill methods ----
    
    /**
     * Fills the {@link GUI} with {@link Item}s.
     *
     * @param start           The start index of the fill (inclusive)
     * @param end             The end index of the fill (exclusive)
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fill(int start, int end, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills the entire {@link GUI} with {@link Item}s.
     *
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fill(@Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills one row with an specific {@link Item}
     *
     * @param row             The row
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fillRow(int row, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills one column with an specific {@link Item}
     *
     * @param column          The column
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fillColumn(int column, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills the borders of this {@link GUI} with an specific {@link Item}
     *
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item}s should be replaced.
     */
    void fillBorders(@Nullable Item item, boolean replaceExisting);
    
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
    void fillRectangle(int x, int y, int width, int height, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills a rectangle of another {@link GUI} in this {@link GUI}.
     *
     * @param x               The x coordinate where the rectangle should start
     * @param y               The y coordinate where the rectangle should start
     * @param gui             The {@link GUI} to be put into this {@link GUI}
     * @param replaceExisting If existing {@link SlotElement}s should be replaced.
     */
    void fillRectangle(int x, int y, @NotNull GUI gui, boolean replaceExisting);
    
    /**
     * Fills a rectangle of a {@link VirtualInventory} in this {@link GUI}.
     *
     * @param x                The x coordinate where the rectangle should start
     * @param y                The y coordinate where the rectangle should start
     * @param width            The line length of the rectangle. (VirtualInventory does not define a width)
     * @param virtualInventory The {@link VirtualInventory} to be put into this {@link GUI}.
     * @param replaceExisting  If existing {@link SlotElement}s should be replaced.
     */
    void fillRectangle(int x, int y, int width, @NotNull VirtualInventory virtualInventory, boolean replaceExisting);
    
}
