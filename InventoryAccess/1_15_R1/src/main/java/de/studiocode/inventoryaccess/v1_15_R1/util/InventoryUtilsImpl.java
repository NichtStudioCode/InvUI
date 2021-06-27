package de.studiocode.inventoryaccess.v1_15_R1.util;

import de.studiocode.inventoryaccess.api.abstraction.util.InventoryUtils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryUtilsImpl implements InventoryUtils {
    
    @Override
    public void openCustomInventory(Player player, Inventory inventory) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        
        if (entityPlayer.playerConnection != null) {
            Container container = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
            container = CraftEventFactory.callInventoryOpenEvent(entityPlayer, container);
            if (container != null) {
                IInventory iinventory = ((CraftInventory) inventory).getInventory();
                IChatBaseComponent title;
                if (iinventory instanceof ITileInventory)
                    title = ((ITileInventory) iinventory).getScoreboardDisplayName();
                else title = new ChatComponentText(container.getBukkitView().getTitle());
                
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, title));
                entityPlayer.activeContainer = container;
                entityPlayer.activeContainer.addSlotListener(entityPlayer);
            }
        }
    }
    
}
