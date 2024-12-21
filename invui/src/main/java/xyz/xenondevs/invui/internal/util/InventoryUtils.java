package xyz.xenondevs.invui.internal.util;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
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
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static xyz.xenondevs.invui.internal.util.ReflectionRegistry.SERVER_PLAYER_CONTAINER_LISTENER_FIELD;

public class InventoryUtils {
    
    @SuppressWarnings("UnstableApiUsage")
    public static int getMaxStackSize(ItemStack itemStack) {
        var maxStackSize = itemStack.getData(DataComponentTypes.MAX_STACK_SIZE);
        return maxStackSize != null ? maxStackSize : 64;
    }
    
    public static Inventory createMatchingInventory(Gui gui) {
        InventoryType type;
        
        if (gui.getWidth() == 9) type = null;
        else if (gui.getWidth() == 3 && gui.getHeight() == 3) type = InventoryType.DROPPER;
        else if (gui.getWidth() == 5 && gui.getHeight() == 1) type = InventoryType.HOPPER;
        else throw new UnsupportedOperationException("Invalid bounds of Gui");
        
        if (type == null) return Bukkit.createInventory(null, gui.getSize(), Component.empty());
        else return Bukkit.createInventory(null, type, Component.empty());
    }
    
    public static boolean containsSimilar(Inventory inventory, @Nullable ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentStack = ItemUtils.takeUnlessEmpty(inventory.getItem(i));
            
            if ((currentStack == null && itemStack == null)
                || (currentStack != null && currentStack.isSimilar(itemStack))) return true;
        }
        
        return false;
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
        
        var open = new ClientboundOpenScreenPacket(menu.containerId, menu.getType(), PaperAdventure.asVanilla(title));
        var content = new ClientboundContainerSetContentPacket(menu.containerId, menu.incrementStateId(), menu.getItems(), menu.getCarried());
        var bundle = new ClientboundBundlePacket(List.of(open, content));
        serverPlayer.connection.send(bundle);
    }
    
    public static void openCustomInventory(Player player, Inventory inventory) {
        openCustomInventory(player, inventory, Component.empty());
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
