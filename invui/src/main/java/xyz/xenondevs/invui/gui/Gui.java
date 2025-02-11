package xyz.xenondevs.invui.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A Gui is a container for width * height {@link SlotElement SlotElements}.<br>
 * Each {@link SlotElement} can either be an {@link Item},
 * a reference to a {@link Inventory}'s or another {@link Gui}'s
 * slot index.<br>
 * A {@link Gui} is not an {@link org.bukkit.inventory.Inventory}, nor does
 * it access one. It just contains {@link SlotElement SlotElements} and their positions.<br>
 * In order to create an {@link org.bukkit.inventory.Inventory} which is visible
 * to players, you will need to use a {@link Window}.
 *
 * @see PagedGui
 * @see ScrollGui
 * @see TabGui
 */
public sealed interface Gui permits AbstractGui, PagedGui, ScrollGui, TabGui {
    
    /**
     * Creates a new {@link Builder Gui Builder} for a normal {@link Gui}.
     *
     * @return The new {@link Builder Gui Builder}.
     */
    static Builder<?, ?> normal() {
        return new NormalGuiImpl.Builder();
    }
    
    /**
     * Creates a new normal {@link Gui} after configuring a {@link Builder Gui Builder} with the given {@link Consumer}.
     *
     * @param consumer The {@link Consumer} to configure the {@link Builder Gui Builder}.
     * @return The created {@link Gui}.
     */
    static Gui normal(Consumer<Builder<?, ?>> consumer) {
        Builder<?, ?> builder = normal();
        consumer.accept(builder);
        return builder.build();
    }
    
    /**
     * Creates a new empty {@link Gui}.
     *
     * @param width  The width of the {@link Gui}.
     * @param height The height of the {@link Gui}.
     * @return The created {@link Gui}.
     */
    static Gui empty(int width, int height) {
        return new NormalGuiImpl(width, height);
    }
    
    /**
     * Creates a new empty {@link Gui}.
     *
     * @param structure The {@link Structure} of the {@link Gui}.
     * @return The created {@link Gui}.
     */
    static Gui of(Structure structure) {
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
    void addSlotElements(SlotElement... slotElements);
    
    /**
     * Gets the {@link SlotElement} on these coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link SlotElement} placed there
     */
    @Nullable
    SlotElement getSlotElement(int x, int y);
    
    /**
     * Gets the {@link SlotElement} placed on that slot.
     *
     * @param index The slot index
     * @return The {@link SlotElement} placed on that slot
     */
    @Nullable
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
     * Gets all {@link SlotElement SlotElements} of this {@link Gui} in an Array.
     *
     * @return All {@link SlotElement SlotElements} of this {@link Gui}
     */
    @Nullable
    SlotElement[] getSlotElements();
    
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
    void addItems(Item... items);
    
    /**
     * Gets the {@link Item} on these coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link Item} which is placed on that slot or null if there isn't one
     */
    @Nullable
    Item getItem(int x, int y);
    
    /**
     * Gets the {@link Item} placed on that slot.
     *
     * @param index The slot index
     * @return The {@link Item} which is placed on that slot or null if there isn't one
     */
    @Nullable
    Item getItem(int index);
    
    /**
     * Gets the {@link ItemProvider} that will be used if nothing else
     * is placed on a slot.
     *
     * @return The {@link ItemProvider}
     */
    @Nullable
    ItemProvider getBackground();
    
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
     * Remove the {@link SlotElement} that is placed on the slot.
     *
     * @param index The slot index of the {@link SlotElement} that should be removed
     */
    void remove(int index);
    
    /**
     * Applies the given {@link Structure} to the {@link Gui}.
     *
     * @param structure The structure
     */
    void applyStructure(Structure structure);
    
    /**
     * Finds all {@link Window Windows} that show this {@link Gui}.
     *
     * @return The list of {@link Window} that show this {@link Gui}
     */
    List<Window> findAllWindows();
    
    /**
     * Finds all {@link Player Players} that are currently seeing this {@link Window}.
     *
     * @return The list of {@link Player Players} that are currently seeing this {@link Window}
     */
    Set<Player> findAllCurrentViewers();
    
    /**
     * Closes the open {@link org.bukkit.inventory.Inventory} for all viewers of {@link Window Windows}
     * where this {@link Gui} is displayed.
     */
    void closeForAllViewers();
    
    /**
     * Notifies all {@link Window Windows} that show this {@link Gui} to update their
     * representative {@link ItemStack ItemStacks}.
     * <p>
     * Can be called asynchronously.
     */
    void notifyWindows();
    
    /**
     * Plays an {@link Animation}.
     *
     * @param animation The {@link Animation} to play.
     */
    void playAnimation(Animation animation);
    
    /**
     * Cancels the running {@link Animation}, if there is one.
     */
    void cancelAnimation();
    
    /**
     * Freezes or unfreezes the {@link Gui}.
     * A frozen {@link Gui} will not allow any clicks.
     *
     * @param frozen If the {@link Gui} should be frozen or not.
     */
    void setFrozen(boolean frozen);
    
    /**
     * Gets if the {@link Gui} is frozen.
     *
     * @return If the {@link Gui} is frozen.
     */
    boolean isFrozen();
    
    /**
     * Configures whether it is possible to shift-click items into and cursor collect items from all {@link Inventory}
     * slots of partially obscured embedded {@link Inventory Inventories}.
     * <p>
     * Defaults to true.
     *
     * @param ignoreObscuredInventorySlots Whether obscured {@link Inventory} slots should be ignored when shift-clicking
     *                                     and collecting to the cursor.
     */
    void setIgnoreObscuredInventorySlots(boolean ignoreObscuredInventorySlots);
    
    /**
     * Gets whether it is possible to shift-click items into and cursor collect items from all {@link Inventory}
     * slots of partially obscured embedded {@link Inventory Inventories}.
     *
     * @return Whether obscured {@link Inventory} slots are ignored when shift-clicking and collecting to the cursor.
     */
    boolean isIgnoreObscuredInventorySlots();
    
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
    void fillRectangle(int x, int y, Gui gui, boolean replaceExisting);
    
    /**
     * Fills a rectangle with a {@link Inventory} in this {@link Gui}.
     *
     * @param x               The x coordinate where the rectangle should start
     * @param y               The y coordinate where the rectangle should start
     * @param width           The line length of the rectangle.
     * @param inventory       The {@link Inventory} to be put into this {@link Gui}.
     * @param replaceExisting If existing {@link SlotElement SlotElements} should be replaced.
     */
    void fillRectangle(int x, int y, int width, Inventory inventory, boolean replaceExisting);
    
    /**
     * Fills a rectangle with a {@link Inventory} in this {@link Gui}.
     *
     * @param x               The x coordinate where the rectangle should start
     * @param y               The y coordinate where the rectangle should start
     * @param width           The line length of the rectangle.
     * @param inventory       The {@link Inventory} to be put into this {@link Gui}.
     * @param background      The {@link ItemProvider} for empty slots of the {@link Inventory}
     * @param replaceExisting If existing {@link SlotElement SlotElements} should be replaced.
     */
    void fillRectangle(int x, int y, int width, Inventory inventory, @Nullable ItemProvider background, boolean replaceExisting);
    
    //</editor-fold>
    
    /**
     * A {@link Gui} builder.
     *
     * @param <G> The type of the {@link Gui}
     * @param <S> The type of the builder
     */
    sealed interface Builder<G extends Gui, S extends Builder<G, S>>
        extends IngredientMapper<S>
        permits AbstractGui.AbstractBuilder, PagedGui.Builder, ScrollGui.Builder, TabGui.Builder
    {
        
        /**
         * Sets the {@link Structure} of the {@link Gui}.
         *
         * @param structure The {@link Structure} of the {@link Gui}
         * @return This {@link Builder}
         */
        S setStructure(Structure structure);
        
        /**
         * Sets the {@link Structure} of the {@link Gui} using the given structure data Strings.
         * Each String is interpreted as a row of the {@link Gui}. All Strings must have the same length.
         *
         * @param structureData The structure data
         * @return This {@link Builder Gui Builder}
         */
        S setStructure(String... structureData);
        
        /**
         * Sets the {@link Structure} of the {@link Gui} using the given structure data, width and height.
         *
         * @param width         The width of the {@link Gui}
         * @param height        The height of the {@link Gui}
         * @param structureData The structure data
         * @return This {@link Builder Gui Builder}
         */
        S setStructure(int width, int height, String structureData);
        
        /**
         * Sets the background of the {@link Gui}.
         *
         * @param itemProvider The {@link ItemProvider} for the background
         * @return This {@link Builder Gui Builder}
         */
        S setBackground(ItemProvider itemProvider);
        
        /**
         * Sets the background of the {@link Gui}.
         *
         * @param itemStack The {@link ItemStack} for the background
         * @return This {@link Builder Gui Builder}
         */
        S setBackground(ItemStack itemStack);
        
        /**
         * Sets whether the {@link Gui} should be frozen.
         *
         * @param frozen Whether the {@link Gui} should be frozen
         * @return This {@link Builder Gui Builder}
         */
        S setFrozen(boolean frozen);
        
        /**
         * Sets whether it is possible to shift-click items into and cursor collect items from all {@link Inventory}
         * slots of partially obscured embedded {@link Inventory Inventories}.
         *
         * @param ignoreObscuredInventorySlots Whether obscured {@link Inventory} slots should be ignored when shift-clicking
         *                                     and collecting to the cursor.
         * @return This {@link Builder Gui Builder}
         */
        S setIgnoreObscuredInventorySlots(boolean ignoreObscuredInventorySlots);
        
        /**
         * Adds a {@link Consumer} that is run when the {@link Gui} is built.
         *
         * @param modifier The {@link Consumer} that modifies the {@link Gui}
         * @return This {@link Builder}
         */
        S addModifier(Consumer<G> modifier);
        
        /**
         * Sets the {@link Consumer}s that are run when the {@link Gui} is built.
         *
         * @param modifiers The {@link Consumer}s that modify the {@link Gui}
         * @return This {@link Builder}
         */
        S setModifiers(List<Consumer<G>> modifiers);
        
        /**
         * Builds the {@link Gui}.
         *
         * @return The {@link Gui}
         */
        G build();
        
        /**
         * Clones the Gui Builder.
         *
         * @return The cloned Gui Builder
         */
        S clone();
        
    }
    
}
