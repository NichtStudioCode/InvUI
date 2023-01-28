package xyz.xenondevs.invui.item.builder;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class ItemBuilder extends BaseItemBuilder<ItemBuilder> {
    
    /**
     * Constructs a new {@link BaseItemBuilder} based on the given {@link Material}.
     *
     * @param material The {@link Material}
     */
    public ItemBuilder(@NotNull Material material) {
        super(material);
    }
    
    /**
     * Constructs a new {@link BaseItemBuilder} based on the given {@link Material} and amount.
     *
     * @param material The {@link Material}
     * @param amount   The amount
     */
    public ItemBuilder(@NotNull Material material, int amount) {
        super(material, amount);
    }
    
    /**
     * Constructs a new {@link BaseItemBuilder} based on the give {@link ItemStack}.
     * This will keep the {@link ItemStack} and uses it's {@link ItemMeta}
     *
     * @param base The {@link ItemStack to use as a base}
     */
    public ItemBuilder(@NotNull ItemStack base) {
        super(base);
    }
    
    @Override
    protected ItemBuilder getThis() {
        return this;
    }
    
}
