package xyz.xenondevs.invui.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ItemProvider extends Supplier<@NotNull ItemStack>, Cloneable {
    
    /**
     * An {@link ItemProvider} for an {@link ItemStack}.
     */
    @NotNull ItemProvider EMPTY = new ItemWrapper(new ItemStack(Material.AIR));
    
    /**
     * Gets the {@link ItemStack} translated in the specified language.
     *
     * @param lang The language to translate the item in.
     * @return The {@link ItemStack}
     */
    @NotNull ItemStack get(@Nullable String lang);
    
    /**
     * Gets the {@link ItemStack} without requesting a specific language.
     *
     * @return The {@link ItemStack}
     */
    default @NotNull ItemStack get() {
        return get(null);
    }
    
}
