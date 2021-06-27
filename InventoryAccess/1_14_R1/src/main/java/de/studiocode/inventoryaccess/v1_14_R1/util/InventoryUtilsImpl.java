package de.studiocode.inventoryaccess.v1_14_R1.util;

import de.studiocode.inventoryaccess.api.abstraction.util.InventoryUtils;
import de.studiocode.inventoryaccess.api.version.ReflectionUtils;
import net.minecraft.server.v1_14_R1.Containers;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.lang.reflect.Method;

public class InventoryUtilsImpl implements InventoryUtils {
    
    private static final Method OPEN_CUSTOM_INVENTORY_METHOD = ReflectionUtils.getMethod(CraftHumanEntity.class,
        true, "openCustomInventory", Inventory.class, EntityPlayer.class, Containers.class);
    
    @Override
    public void openCustomInventory(Player player, Inventory inventory) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Containers<?> windowType = getNotchInventoryType(inventory);
        ReflectionUtils.invokeMethod(OPEN_CUSTOM_INVENTORY_METHOD, player, inventory, entityPlayer, windowType);
    }
    
    private static Containers<?> getNotchInventoryType(Inventory inventory) {
        InventoryType type = inventory.getType();
        if (type == InventoryType.CHEST) {
            switch (inventory.getSize()) {
                case 9:
                    return Containers.GENERIC_9X1;
                case 18:
                    return Containers.GENERIC_9X2;
                case 27:
                    return Containers.GENERIC_9X3;
                case 36:
                    return Containers.GENERIC_9X4;
                case 45:
                    return Containers.GENERIC_9X5;
                case 54:
                    return Containers.GENERIC_9X6;
                default:
                    throw new IllegalArgumentException("Unsupported custom inventory size " + inventory.getSize());
            }
        } else return CraftContainer.getNotchInventoryType(type);
    }
    
}
