package de.studiocode.inventoryaccess.v1_16_R1.util;

import de.studiocode.inventoryaccess.api.abstraction.util.InventoryUtils;
import de.studiocode.inventoryaccess.api.version.ReflectionUtils;
import net.minecraft.server.v1_16_R1.Containers;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftContainer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Method;

public class InventoryUtilsImpl implements InventoryUtils {
    
    private static final Method OPEN_CUSTOM_INVENTORY_METHOD = ReflectionUtils.getMethod(CraftHumanEntity.class,
        true, "openCustomInventory", Inventory.class, EntityPlayer.class, Containers.class);
    
    @Override
    public void openCustomInventory(Player player, Inventory inventory) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        ReflectionUtils.invokeMethod(OPEN_CUSTOM_INVENTORY_METHOD, player, inventory, entityPlayer, windowType);
    }
    
}
