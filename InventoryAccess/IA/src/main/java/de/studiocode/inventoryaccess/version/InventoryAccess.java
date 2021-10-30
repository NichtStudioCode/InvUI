package de.studiocode.inventoryaccess.version;

import de.studiocode.inventoryaccess.abstraction.inventory.AnvilInventory;
import de.studiocode.inventoryaccess.abstraction.inventory.CartographyInventory;
import de.studiocode.inventoryaccess.abstraction.util.InventoryUtils;
import de.studiocode.inventoryaccess.abstraction.util.ItemUtils;
import de.studiocode.inventoryaccess.abstraction.util.PlayerUtils;
import de.studiocode.inventoryaccess.util.ReflectionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

public class InventoryAccess {
    
    private static final Class<InventoryUtils> INVENTORY_UTILS_CLASS = ReflectionUtils.getImplClass("util.InventoryUtilsImpl");
    private static final Class<ItemUtils> ITEM_UTILS_CLASS = ReflectionUtils.getImplClass("util.ItemUtilsImpl");
    private static final Class<PlayerUtils> PLAYER_UTILS_CLASS = ReflectionUtils.getImplClass("util.PlayerUtilsImpl");
    private static final Class<AnvilInventory> ANVIL_INVENTORY_CLASS = ReflectionUtils.getImplClass("inventory.AnvilInventoryImpl");
    private static final Class<CartographyInventory> CARTOGRAPHY_INVENTORY_CLASS = ReflectionUtils.getImplClass("inventory.CartographyInventoryImpl");
    
    private static final Constructor<AnvilInventory> ANVIL_INVENTORY_CONSTRUCTOR =
        ReflectionUtils.getConstructor(ANVIL_INVENTORY_CLASS, false, Player.class, BaseComponent[].class, Consumer.class);
    private static final Constructor<CartographyInventory> CARTOGRAPHY_INVENTORY_CONSTRUCTOR =
        ReflectionUtils.getConstructor(CARTOGRAPHY_INVENTORY_CLASS, false, Player.class, BaseComponent[].class);
    
    private static final InventoryUtils INVENTORY_UTILS = ReflectionUtils.constructEmpty(INVENTORY_UTILS_CLASS);
    private static final ItemUtils ITEM_UTILS = ReflectionUtils.constructEmpty(ITEM_UTILS_CLASS);
    private static final PlayerUtils PLAYER_UTILS = ReflectionUtils.constructEmpty(PLAYER_UTILS_CLASS);
    
    /**
     * Gets the {@link InventoryUtils}
     *
     * @return The {@link InventoryUtils}
     */
    public static InventoryUtils getInventoryUtils() {
        return INVENTORY_UTILS;
    }
    
    /**
     * Gets the {@link ItemUtils}
     *
     * @return The {@link ItemUtils}
     */
    public static ItemUtils getItemUtils() {
        return ITEM_UTILS;
    }
    
    /**
     * Gets the {@link PlayerUtils}
     *
     * @return The {@link PlayerUtils}
     */
    public static PlayerUtils getPlayerUtils() {
        return PLAYER_UTILS;
    }
    
    /**
     * Creates a new {@link AnvilInventory}.
     *
     * @param player        The {@link Player} that should see this {@link AnvilInventory}
     * @param title         The inventory title as a {@link BaseComponent BaseComponent[]}
     * @param renameHandler A {@link Consumer} that is called whenever the {@link Player}
     *                      types something in the renaming section of the anvil
     * @return The {@link AnvilInventory}
     */
    public static AnvilInventory createAnvilInventory(@NotNull Player player, @NotNull BaseComponent[] title, Consumer<String> renameHandler) {
        return ReflectionUtils.construct(ANVIL_INVENTORY_CONSTRUCTOR, player, title, renameHandler);
    }
    
    /**
     * Creates a new {@link CartographyInventory}.
     *
     * @param player The {@link Player} that should see this {@link CartographyInventory}
     * @param title  The inventory title as a {@link BaseComponent BaseComponent[]}
     * @return The {@link CartographyInventory}
     */
    public static CartographyInventory createCartographyInventory(@NotNull Player player, @NotNull BaseComponent[] title) {
        return ReflectionUtils.construct(CARTOGRAPHY_INVENTORY_CONSTRUCTOR, player, title);
    }
    
}
