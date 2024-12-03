package xyz.xenondevs.invui.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.i18n.Languages;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;

public sealed interface SlotElement {
    
    @Nullable
    ItemStack getItemStack(Player player);
    
    @Nullable
    SlotElement getHoldingElement();
    
    /**
     * Contains an {@link xyz.xenondevs.invui.item.Item}
     */
    record Item(xyz.xenondevs.invui.item.Item item) implements SlotElement {
        
        @Override
        public ItemStack getItemStack(Player player) {
            return item.getItemProvider(player).get(Languages.getInstance().getLocale(player));
        }
        
        @Override
        public SlotElement getHoldingElement() {
            return this;
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
            GuiLink element = this;
            while (true) {
                SlotElement below = element.gui().getSlotElement(element.slot());
                if (below instanceof GuiLink) element = (GuiLink) below;
                else return below;
            }
        }
        
        @Override
        public @Nullable ItemStack getItemStack(Player player) {
            SlotElement holdingElement = getHoldingElement();
            return holdingElement != null ? holdingElement.getItemStack(player) : null;
        }
        
    }
    
}
