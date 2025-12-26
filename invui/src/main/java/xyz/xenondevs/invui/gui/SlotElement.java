package xyz.xenondevs.invui.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.Observable;
import xyz.xenondevs.invui.Observer;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * @return The update period in ticks, or <= 0 for no auto-updates.
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
     * @param inventory  The {@link Inventory} to link to
     * @param slot       The slot in the {@link Inventory} to link to
     * @param background The {@link ItemProvider} to use as background if the slot is empty
     */
    record InventoryLink(Inventory inventory, int slot, @Nullable ItemProvider background) implements SlotElement {
        
        /**
         * Creates a new {@link InventoryLink} using the given {@link Inventory} and slot.
         *
         * @param inventory The {@link Inventory} to link to
         * @param slot      The slot in the {@link Inventory} to link to
         */
        public InventoryLink(Inventory inventory, int slot) {
            this(inventory, slot, null);
        }
        
        @Override
        public @Nullable ItemStack getItemStack(Player player) {
            ItemStack itemStack = inventory.getUnsafeItem(slot);
            if (itemStack == null && background != null)
                itemStack = background.get(Languages.getInstance().getLocale(player));
            return itemStack;
        }
        
        @Override
        public SlotElement getHoldingElement() {
            return this;
        }
        
        @Override
        public void addObserver(Observer who, int how) {
            inventory.addObserver(who, slot, how);
        }
        
        @Override
        public void removeObserver(Observer who, int how) {
            inventory.removeObserver(who, slot, how);
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
