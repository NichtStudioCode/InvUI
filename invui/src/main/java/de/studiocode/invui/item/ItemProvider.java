package de.studiocode.invui.item;

import de.studiocode.invui.window.impl.BaseWindow;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

public interface ItemProvider extends Supplier<ItemStack>, Cloneable {
    
    ItemProvider EMPTY = new ItemWrapper(new ItemStack(Material.AIR));
    
    /**
     * Builds the {@link ItemStack}
     *
     * @return The {@link ItemStack}
     */
    ItemStack get();
    
    /**
     * Gets the {@link ItemStack} for a specific player.
     * This is the method called by {@link BaseWindow} which gives you
     * the option to (for example) create a subclass of {@link ItemProvider} that automatically
     * translates the item's name into the player's language.
     *
     * @param playerUUID The {@link UUID} of the {@link Player}
     *                   for whom this {@link ItemStack} should be made.
     * @return The {@link ItemStack}
     */
    ItemStack getFor(@NotNull UUID playerUUID);
    
}
