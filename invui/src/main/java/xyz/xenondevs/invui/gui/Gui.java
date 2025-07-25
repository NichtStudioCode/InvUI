package xyz.xenondevs.invui.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.window.Window;

import java.util.Collection;
import java.util.List;
import java.util.SequencedCollection;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
    static Builder<?, ?> builder() {
        return new NormalGuiImpl.Builder();
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
     * Creates a new {@link Gui} filled entirely with the given {@link Item}.
     *
     * @param width  The width of the {@link Gui}.
     * @param height The height of the {@link Gui}.
     * @param item   The {@link Item} to fill the {@link Gui} with.
     * @return The created {@link Gui} filled with the given {@link Item}.
     */
    static Gui of(int width, int height, Item item) {
        Gui gui = empty(width, height);
        gui.fill(item);
        return gui;
    }
    
    /**
     * Creates a new {@link Gui} with the given {@link Inventory} as its content.
     *
     * @param width     The width of the {@link Gui}.
     * @param height    The height of the {@link Gui}.
     * @param inventory The {@link Inventory} to be placed in the {@link Gui}.
     * @return The created {@link Gui} with the given {@link Inventory} as its content.
     */
    static Gui of(int width, int height, Inventory inventory) {
        Gui gui = empty(width, height);
        gui.fillRectangle(0, 0, width, inventory);
        return gui;
    }
    
    /**
     * Creates a new 1x1 {@link Gui} with the given item on the only slot.
     *
     * @param item The {@link Item} to be placed on the only slot.
     * @return The created {@link Gui}.
     */
    static Gui single(Item item) {
        Gui gui = empty(1, 1);
        gui.setItem(0, item);
        return gui;
    }
    
    /**
     * Creates a new 1x1 {@link Gui} with a link to the given inventory's slot on the only slot.
     *
     * @param inventory The {@link Inventory} to be linked.
     * @param slot      The slot of the inventory to be linked.
     * @return The created {@link Gui}.
     */
    static Gui single(Inventory inventory, int slot) {
        Gui gui = empty(1, 1);
        gui.setSlotElement(0, new SlotElement.InventoryLink(inventory, slot));
        return gui;
    }
    
    /**
     * Creates a new 1x1 {@link Gui} with a link to the given inventory's slot on the only slot.
     *
     * @param inventory  The {@link Inventory} to be linked.
     * @param slot       The slot of the inventory to be linked.
     * @param background The {@link ItemProvider} to display when the slot is empty.
     * @return The created {@link Gui}.
     */
    static Gui single(Inventory inventory, int slot, ItemProvider background) {
        Gui gui = empty(1, 1);
        gui.setSlotElement(0, new SlotElement.InventoryLink(inventory, slot, background));
        return gui;
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
     * Sets the {@link SlotElement} on all slots that are associated with the given key through a {@link Structure}.
     * If you need to set an {@link Item}, prefer {@link #setItem(char, Item)} instead.
     *
     * @param key         The key
     * @param slotElement The {@link SlotElement} to be placed on these slots,
     *                    or null to remove the elements that are currently there.
     * @see #applyStructure(Structure)
     */
    void setSlotElement(char key, @Nullable SlotElement slotElement);
    
    /**
     * Sets the {@link SlotElement SlotElements} on all slots associated with the given key through a {@link Structure},
     * invoking the given {@link Supplier} to get the {@link SlotElement} for each slot.
     * If you need to set an {@link Item}, prefer {@link #setItem(char, Supplier)} instead.
     *
     * @param key             The key
     * @param elementSupplier The {@link Supplier} for the {@link SlotElement SlotElements}
     * @see #applyStructure(Structure)
     */
    void setSlotElement(char key, Supplier<? extends @Nullable SlotElement> elementSupplier);
    
    /**
     * Sets the {@link SlotElement} on the given {@link Slot}. If you need to set an {@link Item},
     * prefer {@link #setItem(int, Item)} instead.
     *
     * @param slot        The slot
     * @param slotElement The {@link SlotElement} to be placed there, or null to remove the {@link SlotElement}
     *                    that is currently there.
     */
    void setSlotElement(Slot slot, @Nullable SlotElement slotElement);
    
    /**
     * Sets the {@link SlotElement} on these coordinates.
     * If you need to set an {@link Item}, prefer {@link #setItem(int, int, Item)} instead.
     *
     * @param x           The x coordinate
     * @param y           The y coordinate
     * @param slotElement The {@link SlotElement} to be placed there, or null to remove the {@link SlotElement}
     *                    that is currently there.
     */
    void setSlotElement(int x, int y, @Nullable SlotElement slotElement);
    
    /**
     * Sets the {@link SlotElement} on these coordinates.
     * If you need to set an {@link Item}, prefer {@link #setItem(int, Item)} instead.
     *
     * @param index       The slot index
     * @param slotElement The {@link SlotElement} to be placed there, or null to remove the {@link SlotElement}
     *                    that is currently there.
     */
    void setSlotElement(int index, @Nullable SlotElement slotElement);
    
    /**
     * Adds {@link SlotElement SlotElements} to the {@link Gui}.
     *
     * @param slotElements The {@link SlotElement SlotElements} to add.
     */
    void addSlotElements(SlotElement... slotElements);
    
    /**
     * Gets the {@link SlotElement} on that {@link Slot}.
     *
     * @param slot The slot
     * @return The {@link SlotElement} placed there
     */
    @Nullable
    SlotElement getSlotElement(Slot slot);
    
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
     * Gets if there is a {@link SlotElement} on that {@link Slot}.
     *
     * @param slot The slot
     * @return If there is a {@link SlotElement} placed there
     */
    boolean hasSlotElement(Slot slot);
    
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
     * Sets the {@link Item} on all slots that are associated with the given key through a {@link Structure}.
     *
     * @param key  The key
     * @param item The {@link Item} that should be placed on these slots,
     *             or null to remove the items that are currently there.
     * @see #applyStructure(Structure)
     */
    void setItem(char key, @Nullable Item item);
    
    /**
     * Sets the {@link Item} on all slots associated with the given key through a {@link Structure},
     * building a new item using the given {@link Item.Builder} for each slot.
     *
     * @param key         The key
     * @param itemBuilder The {@link Item.Builder} for the {@link Item}
     * @see #applyStructure(Structure)
     */
    void setItem(char key, Item.Builder<?> itemBuilder);
    
    /**
     * Sets the {@link Item} on all slots associated with the given key through a {@link Structure},
     * invoking the given {@link Supplier} to get the {@link Item} for each slot.
     *
     * @param key          The key
     * @param itemSupplier The {@link Supplier} for the {@link Item}
     * @see #applyStructure(Structure)
     */
    void setItem(char key, Supplier<? extends @Nullable Item> itemSupplier);
    
    /**
     * Sets the {@link Item} on that {@link Slot}.
     *
     * @param slot The slot
     * @param item The {@link Item} that should be placed on that slot or null
     *             to remove the {@link Item} that is currently there.
     */
    void setItem(Slot slot, @Nullable Item item);
    
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
     * Gets the {@link Item} on that {@link Slot}.
     *
     * @param slot The slot
     * @return The {@link Item} which is placed on that slot or null if there isn't one
     */
    @Nullable
    Item getItem(Slot slot);
    
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
     * Fills the slots associated with the given key through a {@link Structure} with the given {@link Inventory}.
     *
     * @param key       The key
     * @param inventory The {@link Inventory} that should be placed on these slots
     * @see #applyStructure(Structure)
     */
    default void setInventory(char key, Inventory inventory) {
        setSlotElement(key, new InventorySlotElementSupplier(inventory, null, 0));
    }
    
    /**
     * Fills the slots associated with the given key through a {@link Structure} with the given {@link Inventory},
     * starting at the given offset slot.
     *
     * @param key       The key
     * @param inventory The {@link Inventory} that should be placed on these slots
     * @param offset    The slot offset inside the {@link Inventory} to start from
     */
    default void setInventory(char key, Inventory inventory, int offset) {
        setSlotElement(key, new InventorySlotElementSupplier(inventory, null, offset));
    }
    
    /**
     * Fills the slots associated with the given key through a {@link Structure} with the given {@link Inventory},
     * using the given {@link ItemProvider} as background for empty slots.
     *
     * @param key        The key
     * @param inventory  The {@link Inventory} that should be placed on these slots
     * @param background The {@link ItemProvider} for empty slots of the {@link Inventory}
     * @see #applyStructure(Structure)
     */
    default void setInventory(char key, Inventory inventory, @Nullable ItemProvider background) {
        setSlotElement(key, new InventorySlotElementSupplier(inventory, background, 0));
    }
    
    /**
     * Fills the slots associated with the given key through a {@link Structure} with the given {@link Inventory},
     * using the given {@link ItemProvider} as background for empty slots, starting at the given offset slot.
     *
     * @param key        The key
     * @param inventory  The {@link Inventory} that should be placed on these slots
     * @param background The {@link ItemProvider} for empty slots of the {@link Inventory}
     * @param offset     The slot offset inside the {@link Inventory} to start from
     */
    default void setInventory(char key, Inventory inventory, @Nullable ItemProvider background, int offset) {
        setSlotElement(key, new InventorySlotElementSupplier(inventory, background, offset));
    }
    
    /**
     * Fills the slots associated with the given key through a {@link Structure} with the given {@link Gui},
     * using the given {@link ItemProvider} as background for empty slots.
     *
     * @param key The key
     * @param gui The {@link Gui} that should be placed on these slots
     */
    default void setGui(char key, Gui gui) {
        setSlotElement(key, new GuiSlotElementSupplier(gui));
    }
    
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
     * Applies the given {@link Structure} to the {@link Gui}.
     *
     * @param structure The structure
     */
    void applyStructure(Structure structure);
    
    /**
     * Gets the slots associated with the given key through a {@link Structure}.
     *
     * @param key The key
     * @return An unmodifiable collection of the slots associated with the given key.
     * @see #applyStructure(Structure)
     */
    @Unmodifiable
    SequencedCollection<Slot> getSlots(char key);
    
    /**
     * Gets the {@link Structure} key that is associated with the slot at the given index.
     *
     * @param i The slot index
     * @return The {@link Structure} key, or null if the slot is not associated with a {@link Structure}.
     * @see #applyStructure(Structure)
     */
    @Nullable
    Character getKey(int i);
    
    /**
     * Gets the {@link Structure} key that is associated with the slot at the given coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The {@link Structure} key, or null if the slot is not associated with a {@link Structure}.
     * @see #applyStructure(Structure)
     */
    @Nullable
    Character getKey(int x, int y);
    
    /**
     * Gets the {@link Structure} key that is associated with the given {@link Slot}.
     *
     * @param slot The slot
     * @return The {@link Structure} key, or null if the slot is not associated with a {@link Structure}.
     * @see #applyStructure(Structure)
     */
    @Nullable
    Character getKey(Slot slot);
    
    /**
     * Checks whether the slot at the given index is tagged with the given {@link Structure} key.
     *
     * @param i   The slot index
     * @param key The key
     * @return Whether the slot is tagged with the key
     * @see #applyStructure(Structure)
     */
    boolean isTagged(int i, char key);
    
    /**
     * Checks whether the slot at the given coordinates is tagged with the given {@link Structure} key.
     *
     * @param x   The x coordinate
     * @param y   The y coordinate
     * @param key The key
     * @return Whether the slot is tagged with the key
     * @see #applyStructure(Structure)
     */
    boolean isTagged(int x, int y, char key);
    
    /**
     * Checks whether the slot is tagged with the given {@link Structure} key.
     *
     * @param slot The slot
     * @param key  The key
     * @return Whether the slot is tagged with the key
     * @see #applyStructure(Structure)
     */
    boolean isTagged(Slot slot, char key);
    
    /**
     * Gets all {@link Window Windows} that show this {@link Gui}.
     *
     * @return The collection of {@link Window} that show this {@link Gui}
     */
    @Unmodifiable
    Collection<Window> getWindows();
    
    /**
     * Gets all {@link Player Players} that are currently seeing this {@link Window}.
     *
     * @return The collection of {@link Player Players} that are currently seeing this {@link Window}
     */
    @Unmodifiable
    Collection<Player> getCurrentViewers();
    
    /**
     * Closes the all open {@link Window Windows} that display this {@link Gui}.
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
     * Notifies all {@link Window Windows} that display the given {@link Slot} to update their
     * representative {@link ItemStack ItemStacks}.
     * <p>
     * Can be called asynchronously.
     *
     * @param slot The slot
     */
    void notifyWindows(Slot slot);
    
    /**
     * Notifies all {@link Window Windows} that display the slot at the given coordinates
     * to update their representative {@link ItemStack ItemStacks}.
     * <p>
     * Can be called asynchronously.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    void notifyWindows(int x, int y);
    
    /**
     * Notifies all {@link Window Windows} of the slot element at the given index
     * to update their representative {@link ItemStack ItemStacks}.
     * <p>
     * Can be called asynchronously.
     *
     * @param index The slot index
     */
    void notifyWindows(int index);
    
    /**
     * Notifies all {@link Window Windows} that display the slots associated with
     * the given key(s) through a {@link Structure} to update their
     * representative {@link ItemStack ItemStacks}.
     * <p>
     * Note that this does not notify the ingredient that was initially associated with the
     * given key through the structure (if any), but instead just notifies the {@link Window Windows}
     * displaying the affected slots of this {@link Gui}.
     * <p>
     * Can be called asynchronously.
     *
     * @param key  The key of the slot elements to notify.
     * @param keys Additional keys of the slot elements to notify.
     * @see #applyStructure(Structure)
     */
    void notifyWindows(char key, char... keys);
    
    /**
     * Plays an {@link Animation}.
     *
     * @param animation The {@link Animation} to play.
     * @throws IllegalStateException If the {@link Animation} is already playing or was already played.
     */
    void playAnimation(Animation animation);
    
    /**
     * Checks whether an {@link Animation} is currently running.
     *
     * @return Whether an {@link Animation} is currently running.
     */
    boolean isAnimationRunning();
    
    /**
     * Cancels the running {@link Animation}, if there is one.
     */
    void cancelAnimation();
    
    /**
     * Freezes or unfreezes the {@link Gui}.
     * A frozen {@link Gui} will not allow any interactions.
     *
     * @param frozen If the {@link Gui} should be frozen or not.
     */
    void setFrozen(boolean frozen);
    
    /**
     * Gets if the {@link Gui} is frozen, i.e. does not allow any interactions.
     * A {@link Gui} can become frozen either due to a call to {@link #setFrozen(boolean)} or
     * because a freezing {@link Animation} is currently running.
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
    
    /**
     * Gets an unmodifiable sequenced collection of all inventories visible in this gui,
     * including those that are part of nested guis, ignoring the specified inventories.
     * Slots that are embedded multiple times are only returned once.
     * If {@link #isIgnoreObscuredInventorySlots()} is true, the obscured slots will not be visible
     * in the returned inventories.
     * If the gui or any nested gui containing an inventory is frozen, those slots will also not be visible.
     *
     * @param ignored the inventories to ignore
     * @return a sequenced collection of all inventories visible in this gui
     */
    @Unmodifiable
    Collection<Inventory> getInventories(Inventory... ignored);
    
    //<editor-fold desc="fill methods">
    
    /**
     * Fills the gui with the given item, regardless of what was previously there.
     *
     * @param start The start index of the fill (inclusive)
     * @param end   The end index of the fill (exclusive)
     * @param item  The item that should be used or null to remove existing items.
     */
    default void fill(int start, int end, @Nullable Item item) {
        fill(start, end, item, true);
    }
    
    /**
     * Fills the gui with the given item.
     *
     * @param start           The start index of the fill (inclusive)
     * @param end             The end index of the fill (exclusive)
     * @param item            The item that should be used or null to remove existing items.
     * @param replaceExisting If existing slots should be replaced.
     */
    void fill(int start, int end, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills the entire gui with the given item, regardless of what was previously there.
     *
     * @param item The item to put into every slot, or null to remove existing items.
     */
    default void fill(@Nullable Item item) {
        fill(item, true);
    }
    
    /**
     * Fills the entire gui with the given item.
     *
     * @param item            The item that should be used, or null to remove existing items.
     * @param replaceExisting If existing slots should be replaced.
     */
    void fill(@Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills one row with the given item, regardless of what was previously there.
     *
     * @param row  The row
     * @param item The item that should be used, or null to remove existing items.
     */
    default void fillRow(int row, @Nullable Item item) {
        fillRow(row, item, true);
    }
    
    /**
     * Fills one row with the given item.
     *
     * @param row             The row
     * @param item            The item that should be used, or null to remove existing items.
     * @param replaceExisting If existing slots should be replaced.
     */
    void fillRow(int row, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills one column with the given item, regardless of what was previously there.
     *
     * @param column The column
     * @param item   The item that should be used, or null to remove existing items.
     */
    default void fillColumn(int column, @Nullable Item item) {
        fillColumn(column, item, true);
    }
    
    /**
     * Fills one column with the given item.
     *
     * @param column          The column
     * @param item            The item that should be used, or null to remove existing items.
     * @param replaceExisting If existing slots should be replaced.
     */
    void fillColumn(int column, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills the borders of this gui with the given item, regardless of what was previously there.
     *
     * @param item The item that should be used, or null to remove existing items.
     */
    default void fillBorders(@Nullable Item item) {
        fillBorders(item, true);
    }
    
    /**
     * Fills the borders of this gui with the given item.
     *
     * @param item            The item that should be used, or null to remove existing items.
     * @param replaceExisting If existing slots should be replaced.
     */
    void fillBorders(@Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills a rectangle in this gui with the given item, regardless of what was previously there.
     *
     * @param x      The x coordinate where the rectangle should start.
     * @param y      The y coordinate where the rectangle should start.
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle
     * @param item   The item that should be used, or null to remove existing items.
     */
    default void fillRectangle(int x, int y, int width, int height, @Nullable Item item) {
        fillRectangle(x, y, width, height, item, true);
    }
    
    /**
     * Fills a rectangle in this gui with the given item.
     *
     * @param x               The x coordinate where the rectangle should start.
     * @param y               The y coordinate where the rectangle should start.
     * @param width           The width of the rectangle.
     * @param height          The height of the rectangle
     * @param item            The item that should be used, or null to remove existing items.
     * @param replaceExisting If existing slots should be replaced.
     */
    void fillRectangle(int x, int y, int width, int height, @Nullable Item item, boolean replaceExisting);
    
    /**
     * Fills a rectangular area in this gui with the given gui, regardless of what was previously there.
     *
     * @param x   The x coordinate where the rectangle should start
     * @param y   The y coordinate where the rectangle should start
     * @param gui The gui to put into this gui
     */
    default void fillRectangle(int x, int y, Gui gui) {
        fillRectangle(x, y, gui, true);
    }
    
    /**
     * Fills a rectangular area in this gui with the given gui.
     *
     * @param x               The x coordinate where the rectangle should start
     * @param y               The y coordinate where the rectangle should start
     * @param gui             The gui to put into this gui
     * @param replaceExisting If existing slots should be replaced.
     */
    void fillRectangle(int x, int y, Gui gui, boolean replaceExisting);
    
    /**
     * Fills a rectangular area in this gui with the given inventory, regardless of what was previously there.
     *
     * @param x         The x coordinate where the rectangle should start
     * @param y         The y coordinate where the rectangle should start
     * @param width     The line length of the rectangle.
     * @param inventory The inventory to put into this gui.
     */
    default void fillRectangle(int x, int y, int width, Inventory inventory) {
        fillRectangle(x, y, width, inventory, true);
    }
    
    /**
     * Fills a rectangular area in this gui with the given inventory.
     *
     * @param x               The x coordinate where the rectangle should start
     * @param y               The y coordinate where the rectangle should start
     * @param width           The line length of the rectangle.
     * @param inventory       The inventory to put into this gui.
     * @param replaceExisting If existing slots should be replaced.
     */
    void fillRectangle(int x, int y, int width, Inventory inventory, boolean replaceExisting);
    
    /**
     * Fills a rectangle with a {@link Inventory} in this {@link Gui}, using the given background for empty slots.
     *
     * @param x          The x coordinate where the rectangle should start
     * @param y          The y coordinate where the rectangle should start
     * @param width      The line length of the rectangle.
     * @param inventory  The inventory to put into this gui.
     * @param background The item provider for empty slots of the inventory
     */
    default void fillRectangle(int x, int y, int width, Inventory inventory, @Nullable ItemProvider background) {
        fillRectangle(x, y, width, inventory, background, true);
    }
    
    /**
     * Fills a rectangle with a {@link Inventory} in this {@link Gui}.
     *
     * @param x               The x coordinate where the rectangle should start
     * @param y               The y coordinate where the rectangle should start
     * @param width           The line length of the rectangle.
     * @param inventory       The inventory to put into this gui.
     * @param background      The item provider for empty slots of the inventory
     * @param replaceExisting If existing slots should be replaced.
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
        S addModifier(Consumer<? super G> modifier);
        
        /**
         * Sets the {@link Consumer}s that are run when the {@link Gui} is built.
         *
         * @param modifiers The {@link Consumer}s that modify the {@link Gui}
         * @return This {@link Builder}
         */
        S setModifiers(List<? extends Consumer<? super G>> modifiers);
        
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
