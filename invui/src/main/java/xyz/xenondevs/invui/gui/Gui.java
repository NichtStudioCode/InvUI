package xyz.xenondevs.invui.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.animation.Animation;
import xyz.xenondevs.invui.gui.builder.GuiType;
import xyz.xenondevs.invui.gui.impl.NormalGuiImpl;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.virtualinventory.VirtualInventory;
import xyz.xenondevs.invui.window.Window;
import xyz.xenondevs.invui.window.WindowManager;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A Gui is a container for width * height {@link SlotElement SlotElements}.<br>
 * Each {@link SlotElement} can either be an {@link Item},
 * a reference to a {@link VirtualInventory}'s or another {@link Gui}'s
 * slot index.<br>
 * A {@link Gui} is not an {@link Inventory}, nor does
 * it access one. It just contains {@link SlotElement SlotElements} and their positions.<br>
 * In order to create an {@link Inventory} which is visible
 * to players, you will need to use a {@link Window}.
 *
 * @see GuiType
 * @see AbstractGui
 * @see AbstractPagedGui
 * @see AbstractScrollGui
 * @see AbstractTabGui
 */
@SuppressWarnings("deprecation")
public interface Gui {
    
    /**
     * Creates a new empty {@link Gui}.
     *
     * @param width  The width of the {@link Gui}.
     * @param height The height of the {@link Gui}.
     * @return The created {@link Gui}.
     */
    static @NotNull Gui empty(int width, int height) {
        return new NormalGuiImpl(width, height);
    }
    
    /**
     * Creates a new empty {@link Gui}.
     *
     * @param structure The {@link Structure} of the {@link Gui}.
     * @return The created {@link Gui}.
     */
    static @NotNull Gui of(@NotNull Structure structure) {
        return new NormalGuiImpl(structure);
    }
    
    /**
     * Gets the size of the {@link Gui}.
     *
     * @return The size of the gui.
     */
    int getSize();
    
    /**
     * Gets the width of the {@link Gui}
     *
     * @return The width of the {@link Gui}
     */
    int getWidth();
    
    /**
     * Gets the height of the {@link Gui}
     *
     * @return The height of the {@link Gui}
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
     * Adds {@link SlotElement SlotElements} to the {@link Gui}.
     *
     * @param slotElements The {@link SlotElement SlotElements} to add.
     */
    void addSlotElements(@NotNull SlotElement... slotElements);
    
    /**
     * Gets the {@link SlotElement} on these coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link SlotElement} placed there
     */
    @Nullable SlotElement getSlotElement(int x, int y);
    
    /**
     * Gets the {@link SlotElement} placed on that slot.
     *
     * @param index The slot index
     * @return The {@link SlotElement} placed on that slot
     */
    @Nullable SlotElement getSlotElement(int index);
    
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
     * Gets all {@link SlotElement SlotElements} of this {@link Gui} in an Array.
     *
     * @return All {@link SlotElement SlotElements} of this {@link Gui}
     */
    @Nullable SlotElement @NotNull [] getSlotElements();
    
    /**
     * Sets the {@link Item} on these coordinates.
     *
     * @param x    The x coordinate
     * @param y    The y coordinate
     * @param item The {@link Item} that should be placed on these coordinates
     *             or null to remove the {@link Item} that is currently there.
     */
    void setItem(int x, int y, @Nullable Item item);
    
    /**
     * Sets the {@link Item} on that slot
     *
     * @param index The slot index
     * @param item  The {@link Item} that should be placed on that slot or null
     *              to remove the {@link Item} that is currently there.
     */
    void setItem(int index, @Nullable Item item);
    
    /**
     * Adds {@link Item Items} to the gui.
     *
     * @param items The {@link Item Items} that should be added to the gui
     */
    void addItems(@NotNull Item... items);
    
    /**
     * Gets the {@link Item} on these coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link Item} which is placed on that slot or null if there isn't one
     */
    @Nullable Item getItem(int x, int y);
    
    /**
     * Gets the {@link Item} placed on that slot.
     *
     * @param index The slot index
     * @return The {@link Item} which is placed on that slot or null if there isn't one
     */
    @Nullable Item getItem(int index);
    
    /**
     * Gets the {@link ItemProvider} that will be used if nothing else
     * is placed on a slot.
     *
     * @return The {@link ItemProvider}
     */
    @Nullable ItemProvider getBackground();
    
    /**
     * Sets the {@link ItemProvider} that will be used if nothing else
     * is placed on a slot.
     *
     * @param itemProvider The {@link ItemProvider}
     */
    void setBackground(@Nullable ItemProvider itemProvider);
    
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
     * @param index The slot index of the {@link Item Items} that should be removed
     */
    void remove(int index);
    
    /**
     * Applies the given {@link Structure} to the {@link Gui}.
     *
     * @param structure The structure
     */
    void applyStructure(@NotNull Structure structure);
    
    /**
     * Finds all {@link Window Windows} that show this {@link Gui}.
     *
     * @return The list of {@link Window} that show this {@link Gui}
     */
    @NotNull List<@NotNull Window> findAllWindows();
    
    /**
     * Finds all {@link Player Players} that are currently seeing this {@link Window}.
     *
     * @return The list of {@link Player Players} that are currently seeing this {@link Window}
     */
    @NotNull Set<@NotNull Player> findAllCurrentViewers();
    
    /**
     * Closes the open {@link Inventory} for all viewers of {@link Window Windows}
     * where this {@link Gui} is displayed.
     * <p>
     * If the {@link Window Windows} are not marked as "retain",
     * they will be removed from the {@link WindowManager} automatically.
     */
    void closeForAllViewers();
    
    /**
     * Plays an {@link Animation}.
     *
     * @param animation The {@link Animation} to play.
     * @param filter    The filter that selects which {@link SlotElement SlotElements} should be animated.
     */
    void playAnimation(@NotNull Animation animation, @Nullable Predicate<@NotNull SlotElement> filter);
    
    /**
     * Cancels the running {@link Animation} if there is one.
     */
    void cancelAnimation();
    
    //<editor-fold desc="fill methods">
    
    /**
     * Fills the {@link Gui} with {@link Item Items}.
     *
     * @param start           The start index of the fill (inclusive)
     * @param end             The end index of the fill (exclusive)
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item Items} should be replaced.
     */
    void fill(int start, int end, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills the entire {@link Gui} with {@link Item Items}.
     *
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item Items} should be replaced.
     */
    void fill(@Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills one row with a specific {@link Item}
     *
     * @param row             The row
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item Items} should be replaced.
     */
    void fillRow(int row, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills one column with a specific {@link Item}
     *
     * @param column          The column
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item Items} should be replaced.
     */
    void fillColumn(int column, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills the borders of this {@link Gui} with a specific {@link Item}
     *
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item Items} should be replaced.
     */
    void fillBorders(@Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills a rectangle in this {@link Gui} with a specific {@link Item}
     *
     * @param x               The x coordinate where the rectangle should start.
     * @param y               The y coordinate where the rectangle should start.
     * @param width           The width of the rectangle.
     * @param height          The height of the rectangle
     * @param item            The {@link Item} that should be used or null to remove an existing item.
     * @param replaceExisting If existing {@link Item Items} should be replaced.
     */
    void fillRectangle(int x, int y, int width, int height, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills a rectangle with another {@link Gui} in this {@link Gui}.
     *
     * @param x               The x coordinate where the rectangle should start
     * @param y               The y coordinate where the rectangle should start
     * @param gui             The {@link Gui} to be put into this {@link Gui}
     * @param replaceExisting If existing {@link SlotElement SlotElements} should be replaced.
     */
    void fillRectangle(int x, int y, @NotNull Gui gui, boolean replaceExisting);
    
    /**
     * Fills a rectangle with a {@link VirtualInventory} in this {@link Gui}.
     *
     * @param x                The x coordinate where the rectangle should start
     * @param y                The y coordinate where the rectangle should start
     * @param width            The line length of the rectangle. (VirtualInventory does not define a width)
     * @param virtualInventory The {@link VirtualInventory} to be put into this {@link Gui}.
     * @param replaceExisting  If existing {@link SlotElement SlotElements} should be replaced.
     */
    void fillRectangle(int x, int y, int width, @NotNull VirtualInventory virtualInventory, boolean replaceExisting);
    
    /**
     * Fills a rectangle with a {@link VirtualInventory} in this {@link Gui}.
     *
     * @param x                The x coordinate where the rectangle should start
     * @param y                The y coordinate where the rectangle should start
     * @param width            The line length of the rectangle. (VirtualInventory does not define a width)
     * @param virtualInventory The {@link VirtualInventory} to be put into this {@link Gui}.
     * @param background       The {@link ItemProvider} for empty slots of the {@link VirtualInventory}
     * @param replaceExisting  If existing {@link SlotElement SlotElements} should be replaced.
     */
    void fillRectangle(int x, int y, int width, @NotNull VirtualInventory virtualInventory, @Nullable ItemProvider background, boolean replaceExisting);
    
    //</editor-fold>
    
}
