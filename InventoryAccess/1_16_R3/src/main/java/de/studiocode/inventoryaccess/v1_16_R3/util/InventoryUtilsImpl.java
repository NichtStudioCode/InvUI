package de.studiocode.inventoryaccess.v1_16_R3.util;

import de.studiocode.inventoryaccess.api.abstraction.util.InventoryUtils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
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
                else title = CraftChatMessage.fromString(container.getBukkitView().getTitle())[0];
            
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, title));
                entityPlayer.activeContainer = container;
                entityPlayer.activeContainer.addSlotListener(entityPlayer);
            }
        }
    }
    
}
