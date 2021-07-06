package de.studiocode.inventoryaccess.api.version;

import de.studiocode.inventoryaccess.api.abstraction.inventory.AnvilInventory;
import de.studiocode.inventoryaccess.api.abstraction.util.InventoryUtils;
import de.studiocode.inventoryaccess.api.abstraction.util.ItemUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

public class InventoryAccess {
    
    private static final Class<InventoryUtils> INVENTORY_UTILS_CLASS = ReflectionUtils.getImplClass("util.InventoryUtilsImpl");
    private static final Class<ItemUtils> ITEM_UTILS_CLASS = ReflectionUtils.getImplClass("util.ItemUtilsImpl");
    private static final Class<AnvilInventory> ANVIL_INVENTORY_CLASS = ReflectionUtils.getImplClass("inventory.AnvilInventoryImpl");
    
    private static final Constructor<AnvilInventory> ANVIL_INVENTORY_CONSTRUCTOR
        = ReflectionUtils.getConstructor(ANVIL_INVENTORY_CLASS, Player.class, BaseComponent[].class, Consumer.class);
    
    private static final InventoryUtils INVENTORY_UTILS = ReflectionUtils.constructEmpty(INVENTORY_UTILS_CLASS);
    private static final ItemUtils ITEM_UTILS = ReflectionUtils.constructEmpty(ITEM_UTILS_CLASS);
    
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
     * Creates a new {@link AnvilInventory}.
     *
     * @param player        The {@link Player} that should see this {@link AnvilInventory}
     * @param title         The title {@link String} of the {@link AnvilInventory}
     * @param renameHandler A {@link Consumer} that is called whenever the {@link Player}
     *                      types something in the renaming section of the anvil
     * @return The {@link AnvilInventory}
     */
    public static AnvilInventory createAnvilInventory(Player player, BaseComponent[] title, Consumer<String> renameHandler) {
        return ReflectionUtils.construct(ANVIL_INVENTORY_CONSTRUCTOR, player, title, renameHandler);
    }
    
}
