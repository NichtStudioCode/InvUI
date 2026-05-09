package xyz.xenondevs.invui.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.Observable;
import xyz.xenondevs.invui.Observer;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.internal.util.FuncUtils;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Represents an element in a slot in a {@link Gui}.
 */
public sealed interface SlotElement {
    
    /**
     * Gets the {@link ItemStack} that should be displayed in the slot.
     *
     * @param player The player viewing the {@link ItemStack}
     * @return The {@link ItemStack} that should be displayed in the slot
     */
    @Nullable
    ItemStack getItemStack(Player player);
    
    /**
     * Gets the {@link SlotElement} that is actually holding the {@link ItemStack}.
     *
     * @return The {@link SlotElement} that is actually holding the {@link ItemStack}
     */
    @Nullable
    SlotElement getHoldingElement();
    
    /**
     * Adds an {@link Observer} to the content of this {@link SlotElement}.
     *
     * @param who The {@link Observer} to add
     * @param how An integer specifying how the {@link Observer} is observing this {@link Observable}.
     *            Used to {@link Observer#notifyUpdate(int) notify} the {@link Observer} about updates.
     */
    void addObserver(Observer who, int how);
    
    /**
     * Removes an {@link Observer} from the content of this {@link SlotElement}.
     *
     * @param who The {@link Observer} to remove
     * @param how An integer specifying how the {@link Observer} was observing this {@link Observable}.
     */
    void removeObserver(Observer who, int how);
    
    /**
     * Gets the {@link Observable#getUpdatePeriod(int)} of the content of this {@link SlotElement}.
     *
     * @return The update period in ticks, or {@code <= 0} for no auto-updates.
     */
    int getUpdatePeriod();
    
    /**
     * Contains an {@link xyz.xenondevs.invui.item.Item}
     *
     * @param item The {@link xyz.xenondevs.invui.item.Item}
     */
    record Item(xyz.xenondevs.invui.item.Item item) implements SlotElement {
        
        @Override
        public @Nullable ItemStack getItemStack(Player player) {
            try {
                return item.getItemProvider(player).get(Languages.getInstance().getLocale(player));
            } catch (Throwable t) {
                InvUI.getInstance().handleException("Failed to get item stack for item slot element", t);
            }
            
            return null;
        }
        
        @Override
        public SlotElement getHoldingElement() {
            return this;
        }
        
        @Override
        public void addObserver(Observer who, int how) {
            item.addObserver(who, 0, how);
        }
        
        @Override
        public void removeObserver(Observer who, int how) {
            item.removeObserver(who, 0, how);
        }
        
        @Override
        public int getUpdatePeriod() {
            return item.getUpdatePeriod(0);
        }
        
    }
    
    /**
     * Links to a slot in a {@link Inventory}
     *
     * @param inventory          The {@link Inventory} to link to
     * @param slot               The slot in the {@link Inventory} to link to
     * @param backgroundProperty The property containing the {@link ItemProvider} to use as background if the linked slot is empty.
     * @param visualizer         A function that takes the actual {@link ItemStack} in the linked slot and returns an {@link ItemProvider} to visualize it.
     *                           May return {@code null} to display the item stack normally and to e.g. fall back to the background provider.
     */
    record InventoryLink(Inventory inventory, int slot,
                         Property<@Nullable ItemProvider> backgroundProperty,
                         Function<@Nullable ItemStack, @Nullable ItemProvider> visualizer
    ) implements SlotElement
    {
        
        /**
         * Creates a new {@link InventoryLink} using the given {@link Inventory} and slot.
         *
         * @param inventory The {@link Inventory} to link to
         * @param slot      The slot in the {@link Inventory} to link to
         */
        public InventoryLink(Inventory inventory, int slot) {
            this(inventory, slot, (ItemProvider) null);
        }
        
        /**
         * Creates a new {@link InventoryLink} using the given {@link Inventory}, slot and background {@link ItemProvider}.
         *
         * @param inventory  The {@link Inventory} to link to
         * @param slot       The slot in the {@link Inventory} to link to
         * @param background The {@link ItemProvider} to use as background if the linked slot is empty.
         */
        public InventoryLink(Inventory inventory, int slot, @Nullable ItemProvider background) {
            this(inventory, slot, Property.of(background));
        }
        
        /**
         * Creates a new {@link InventoryLink} using the given {@link Inventory}, slot and background property.
         *
         * @param inventory          The {@link Inventory} to link to
         * @param slot               The slot in the {@link Inventory} to link to
         * @param backgroundProperty The property containing the {@link ItemProvider} to use as background if the linked slot is empty.
         */
        public InventoryLink(Inventory inventory, int slot, Property<@Nullable ItemProvider> backgroundProperty) {
            this(inventory, slot, backgroundProperty, _ -> null);
        }
        
        /**
         * Gets the background {@link ItemProvider}.
         *
         * @return The background {@link ItemProvider}
         */
        public @Nullable ItemProvider background() {
            return backgroundProperty().get();
        }
        
        @Override
        public @Nullable ItemStack getItemStack(Player player) {
            var locale = Languages.getInstance().getLocale(player);
            
            // 1. try visualizer on SlotElement level
            var slotVisualization = FuncUtils.applySafely(visualizer, inventory.getItem(slot), null);
            if (slotVisualization != null)
                return slotVisualization.get(locale);
            
            // 2. try visualizer on Inventory level
            var invVisualization = inventory.getVisualization(slot);
            if (invVisualization != null)
                return invVisualization.get(locale);
            
            // 3. use actual item stack on slot, if present
            var itemStack = inventory.getItem(slot);
            if (itemStack != null)
                return itemStack;
            
            // 4. use background if there is no item stack on the inventory slot but there is a background
            var background = backgroundProperty().get();
            if (background != null)
                return background.get(locale);
            
            // 5. no visualization, no item stack, no background: empty slot
            return null;
        }
        
        @Override
        public SlotElement getHoldingElement() {
            return this;
        }
        
        @Override
        public void addObserver(Observer who, int how) {
            inventory.addObserver(who, slot, how);
            backgroundProperty.observeWeak(this, thisRef -> thisRef.inventory.notifyWindows(slot));
        }
        
        @Override
        public void removeObserver(Observer who, int how) {
            inventory.removeObserver(who, slot, how);
            backgroundProperty.unobserveWeak(this);
        }
        
        @Override
        public int getUpdatePeriod() {
            return inventory.getUpdatePeriod(slot);
        }
        
    }
    
    /**
     * Links to a slot in another {@link Gui}
     *
     * @param gui  The {@link Gui} to link to
     * @param slot The slot in the {@link Gui} to link to
     */
    record GuiLink(Gui gui, int slot) implements SlotElement {
        
        @Override
        public @Nullable SlotElement getHoldingElement() {
            SlotElement current = this;
            while (current instanceof GuiLink(Gui g, int s)) {
                current = g.getSlotElement(s);
            }
            return current;
        }
        
        @Override
        @Nullable
        public ItemStack getItemStack(Player player) {
            SlotElement holdingElement = getHoldingElement();
            if (holdingElement != null)
                return holdingElement.getItemStack(player);
            return null;
        }
        
        /**
         * Follows {@link #getHoldingElement()} until a non-{@link GuiLink} is reached
         * and builds a list of all {@link SlotElement SlotElements} reached during the traversal.
         * If the last {@link GuiLink} element {@link #getHoldingElement() returns} {@code null}, the list will
         * end with that {@link GuiLink} element and not contain {@code null}.
         *
         * @return A {@link List} of all {@link SlotElement SlotElements} reached by traversing.
         */
        public @Unmodifiable List<SlotElement> traverse() {
            var elements = new ArrayList<SlotElement>();
            
            SlotElement current = this;
            while (current instanceof GuiLink(Gui g, int s)) {
                elements.add(current);
                current = g.getSlotElement(s);
            }
            if (current != null)
                elements.add(current);
            
            return Collections.unmodifiableList(elements);
        }
        
        /**
         * Follows {@link #getHoldingElement()} until a non-{@link GuiLink} is reached and compares the path
         * to the given list as returned by {@link #traverse()}.
         *
         * @return {@code true} if the path matches the given list, {@code false} otherwise.
         */
        public boolean checkTraverse(List<SlotElement> other) {
            int i = 0;
            SlotElement current = this;
            while (current instanceof GuiLink(Gui g, int s)) {
                if (i >= other.size() || !current.equals(other.get(i)))
                    return false;
                current = g.getSlotElement(s);
                i++;
            }
            return (i == other.size() && current == null)
                   || (i == other.size() - 1 && other.getLast().equals(current));
        }
        
        @Override
        public void addObserver(Observer who, int how) {
            gui.addObserver(who, slot, how);
        }
        
        @Override
        public void removeObserver(Observer who, int how) {
            gui.removeObserver(who, slot, how);
        }
        
        @Override
        public int getUpdatePeriod() {
            return gui.getUpdatePeriod(slot);
        }
    }
    
}
