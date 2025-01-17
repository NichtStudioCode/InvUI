package xyz.xenondevs.invui.internal.util;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static xyz.xenondevs.invui.internal.util.ReflectionRegistry.SERVER_PLAYER_CONTAINER_LISTENER_FIELD;

public class InventoryUtils {
    
    public static Inventory createMatchingInventory(Gui gui) {
        InventoryType type;
        
        if (gui.getWidth() == 9) type = null;
        else if (gui.getWidth() == 3 && gui.getHeight() == 3) type = InventoryType.DROPPER;
        else if (gui.getWidth() == 5 && gui.getHeight() == 1) type = InventoryType.HOPPER;
        else throw new UnsupportedOperationException("Invalid bounds of Gui");
        
        if (type == null) return Bukkit.createInventory(null, gui.getSize(), Component.empty());
        else return Bukkit.createInventory(null, type, Component.empty());
    }
    
    public static void dropItemLikePlayer(Player player, ItemStack itemStack) {
        Location location = player.getLocation();
        location.add(0, 1.5, 0); // not the eye location
        Item item = location.getWorld().dropItem(location, itemStack);
        item.setPickupDelay(40);
        item.setVelocity(location.getDirection().multiply(0.35));
    }
    
    public static void updateOpenInventoryTitle(Player player, Component title) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        AbstractContainerMenu menu = serverPlayer.containerMenu;
        
        var synchronizer = new CapturingContainerSynchronizer(serverPlayer);
        menu.setSynchronizer(synchronizer);
        
        var packets = new ArrayList<Packet<? super ClientGamePacketListener>>();
        packets.add(new ClientboundOpenScreenPacket(menu.containerId, menu.getType(), PaperAdventure.asVanilla(title)));
        packets.addAll(synchronizer.stopCapture());
        serverPlayer.connection.send(new ClientboundBundlePacket(packets));
    }
    
    public static void openCustomInventory(Player player, Inventory inventory, Component title) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        MenuType<?> menuType = CraftContainer.getNotchInventoryType(inventory);
        
        AbstractContainerMenu menu = new CraftContainer(inventory, serverPlayer, serverPlayer.nextContainerCounter());
        menu = CraftEventFactory.callInventoryOpenEvent(serverPlayer, menu);
        if (menu != null) {
            menu.checkReachable = false;
            serverPlayer.containerMenu = menu;
            
            var synchronizer = new CapturingContainerSynchronizer(serverPlayer);
            menu.setSynchronizer(synchronizer);
            menu.addSlotListener(Objects.requireNonNull(ReflectionUtils.getFieldValue(SERVER_PLAYER_CONTAINER_LISTENER_FIELD, serverPlayer)));
            
            var packets = new ArrayList<Packet<? super ClientGamePacketListener>>();
            packets.add(new ClientboundOpenScreenPacket(menu.containerId, menuType, PaperAdventure.asVanilla(title)));
            packets.addAll(synchronizer.stopCapture());
            serverPlayer.connection.send(new ClientboundBundlePacket(packets));
        }
    }
    
}
