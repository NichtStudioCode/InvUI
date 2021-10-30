package de.studiocode.inventoryaccess.r4.util;

import de.studiocode.inventoryaccess.abstraction.util.InventoryUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class InventoryUtilsImpl implements InventoryUtils {
    
    public static IChatBaseComponent createNMSComponent(BaseComponent[] components) {
        String json = ComponentSerializer.toString(components);
        return IChatBaseComponent.ChatSerializer.a(json);
    }
    
    public static int getActiveWindowId(EntityPlayer player) {
        Container container = player.activeContainer;
        return container == null ? -1 : container.windowId;
    }
    
    @Override
    public void openCustomInventory(@NotNull Player player, @NotNull Inventory inventory) {
        openCustomInventory(player, inventory, null);
    }
    
    @Override
    public void openCustomInventory(@NotNull Player player, @NotNull Inventory inventory, @NotNull BaseComponent[] title) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Containers<?> windowType = CraftContainer.getNotchInventoryType(inventory);
        
        if (entityPlayer.playerConnection != null) {
            Container container = new CraftContainer(inventory, entityPlayer, entityPlayer.nextContainerCounter());
            container = CraftEventFactory.callInventoryOpenEvent(entityPlayer, container);
            if (container != null) {
                IInventory iinventory = ((CraftInventory) inventory).getInventory();
                IChatBaseComponent titleComponent;
                if (title == null) {
                    if (iinventory instanceof ITileInventory)
                        titleComponent = ((ITileInventory) iinventory).getScoreboardDisplayName();
                    else titleComponent = CraftChatMessage.fromString(container.getBukkitView().getTitle())[0];
                } else titleComponent = createNMSComponent(title);
                
                entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, titleComponent));
                entityPlayer.activeContainer = container;
                entityPlayer.activeContainer.addSlotListener(entityPlayer);
            }
        }
    }
    
    @Override
    public void updateOpenInventoryTitle(@NotNull Player player, @NotNull BaseComponent[] title) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        Container container = entityPlayer.activeContainer;
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, container.getType(), createNMSComponent(title)));
        entityPlayer.updateInventory(container);
    }
    
}
