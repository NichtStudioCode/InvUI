package xyz.xenondevs.inventoryaccess;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.AnvilInventory;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.CartographyInventory;
import xyz.xenondevs.inventoryaccess.abstraction.util.InventoryUtils;
import xyz.xenondevs.inventoryaccess.abstraction.util.ItemUtils;
import xyz.xenondevs.inventoryaccess.abstraction.util.PlayerUtils;
import xyz.xenondevs.inventoryaccess.component.BungeeComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.inventoryaccess.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Consumer;

public class InventoryAccess {
    
    private static final Class<InventoryUtils> INVENTORY_UTILS_CLASS = ReflectionUtils.getImplClass("InventoryUtilsImpl");
    private static final Class<ItemUtils> ITEM_UTILS_CLASS = ReflectionUtils.getImplClass("ItemUtilsImpl");
    private static final Class<PlayerUtils> PLAYER_UTILS_CLASS = ReflectionUtils.getImplClass("PlayerUtilsImpl");
    private static final Class<AnvilInventory> ANVIL_INVENTORY_CLASS = ReflectionUtils.getImplClass("AnvilInventoryImpl");
    private static final Class<CartographyInventory> CARTOGRAPHY_INVENTORY_CLASS = ReflectionUtils.getImplClass("CartographyInventoryImpl");
    
    private static final Constructor<AnvilInventory> ANVIL_INVENTORY_CONSTRUCTOR =
        ReflectionUtils.getConstructor(ANVIL_INVENTORY_CLASS, true, Player.class, ComponentWrapper.class, List.class);
    private static final Constructor<CartographyInventory> CARTOGRAPHY_INVENTORY_CONSTRUCTOR =
        ReflectionUtils.getConstructor(CARTOGRAPHY_INVENTORY_CLASS, true, Player.class, ComponentWrapper.class);
    
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
     * @param player         The {@link Player} that should see this {@link AnvilInventory}
     * @param title          The inventory title
     * @param renameHandlers A list of {@link Consumer}s that are called whenever the {@link Player}
     *                       types something in the renaming section of the anvil.
     * @return The {@link AnvilInventory}
     */
    public static AnvilInventory createAnvilInventory(@NotNull Player player, @Nullable ComponentWrapper title, @Nullable List<@NotNull Consumer<String>> renameHandlers) {
        return ReflectionUtils.construct(ANVIL_INVENTORY_CONSTRUCTOR, player, title == null ? BungeeComponentWrapper.EMPTY : title, renameHandlers);
    }
    
    /**
     * Creates a new {@link CartographyInventory}.
     *
     * @param player The {@link Player} that should see this {@link CartographyInventory}
     * @param title  The inventory title
     * @return The {@link CartographyInventory}
     */
    public static CartographyInventory createCartographyInventory(@NotNull Player player, @Nullable ComponentWrapper title) {
        return ReflectionUtils.construct(CARTOGRAPHY_INVENTORY_CONSTRUCTOR, player, title == null ? BungeeComponentWrapper.EMPTY : title);
    }
    
}
