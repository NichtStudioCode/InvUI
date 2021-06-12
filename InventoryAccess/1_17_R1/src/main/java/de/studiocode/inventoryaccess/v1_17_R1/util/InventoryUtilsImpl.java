package de.studiocode.inventoryaccess.v1_17_R1.util;

import de.studiocode.inventoryaccess.api.abstraction.util.InventoryUtils;
import de.studiocode.inventoryaccess.api.version.ReflectionUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftContainer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Method;

public class InventoryUtilsImpl implements InventoryUtils {
    
    private static final Method OPEN_CUSTOM_INVENTORY_METHOD = ReflectionUtils.getMethod(CraftHumanEntity.class,
        true, "openCustomInventory", Inventory.class, ServerPlayer.class, MenuType.class);
    
    @Override
    public void openCustomInventory(Player player, Inventory inventory) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        MenuType<?> menuType = CraftContainer.getNotchInventoryType(inventory);
        ReflectionUtils.invokeMethod(OPEN_CUSTOM_INVENTORY_METHOD, null, inventory, serverPlayer, menuType);
        serverPlayer.containerMenu.checkReachable = false;
    }
    
}
