package xyz.xenondevs.inventoryaccess.r21;

import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.abstraction.util.InventoryUtils;

import java.util.List;

class InventoryUtilsImpl implements InventoryUtils {
    
    public static int getActiveWindowId(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        return container == null ? -1 : container.containerId;
    }
    
    @Override
    public void openCustomInventory(Player player, Inventory inventory) {
        openCustomInventory(player, inventory, null);
    }
    
    @Override
    public void openCustomInventory(Player player, Inventory inventory, net.kyori.adventure.text.Component title) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        MenuType<?> menuType = CraftContainer.getNotchInventoryType(inventory);
        
        if (serverPlayer.connection != null) {
            AbstractContainerMenu menu = new CraftContainer(inventory, serverPlayer, serverPlayer.nextContainerCounter());
            menu = CraftEventFactory.callInventoryOpenEvent(serverPlayer, menu);
            if (menu != null) {
                Container container = ((CraftInventory) inventory).getInventory();
                Component titleComponent;
                if (title == null) {
                    if (container instanceof MenuProvider)
                        titleComponent = ((MenuProvider) container).getDisplayName();
                    else titleComponent = CraftChatMessage.fromString(menu.getBukkitView().getTitle())[0];
                } else titleComponent = PaperAdventure.asVanilla(title);
                
                menu.checkReachable = false;
                serverPlayer.connection.send(new ClientboundOpenScreenPacket(menu.containerId, menuType, titleComponent));
                serverPlayer.containerMenu = menu;
                serverPlayer.initMenu(menu);
            }
        }
        
    }
    
    @Override
    public void updateOpenInventoryTitle(Player player, net.kyori.adventure.text.Component title) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        AbstractContainerMenu menu = serverPlayer.containerMenu;
        
        var open = new ClientboundOpenScreenPacket(menu.containerId, menu.getType(), PaperAdventure.asVanilla(title));
        var content = new ClientboundContainerSetContentPacket(menu.containerId, menu.incrementStateId(), menu.getItems(), menu.getCarried());
        var bundle = new ClientboundBundlePacket(List.of(open, content));
        serverPlayer.connection.send(bundle);
    }
    
    @Override
    public @Nullable ItemStack getItemStackFromView(InventoryView view, int slot) {
        return view.getItem(slot);
    }
    
}
