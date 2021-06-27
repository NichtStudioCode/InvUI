package de.studiocode.inventoryaccess.v1_17_R1.util;

import de.studiocode.inventoryaccess.api.abstraction.util.InventoryUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftContainer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryUtilsImpl implements InventoryUtils {
    
    @Override
    public void openCustomInventory(Player player, Inventory inventory) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        MenuType<?> menuType = CraftContainer.getNotchInventoryType(inventory);
        
        if (serverPlayer.connection != null) {
            AbstractContainerMenu menu = new CraftContainer(inventory, serverPlayer, serverPlayer.nextContainerCounter());
            menu = CraftEventFactory.callInventoryOpenEvent(serverPlayer, menu);
            if (menu != null) {
                Container container = ((CraftInventory) inventory).getInventory();
                Component title;
                if (container instanceof MenuProvider)
                    title = ((MenuProvider) container).getDisplayName();
                else title = CraftChatMessage.fromString(menu.getBukkitView().getTitle())[0];
                
                menu.checkReachable = false;
                serverPlayer.connection.send(new ClientboundOpenScreenPacket(menu.containerId, menuType, title));
                serverPlayer.containerMenu = menu;
                serverPlayer.initMenu(menu);
            }
        }
        
    }
    
}
