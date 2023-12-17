package xyz.xenondevs.inventoryaccess.r11;

import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.abstraction.util.PlayerUtils;
import xyz.xenondevs.inventoryaccess.map.MapIcon;
import xyz.xenondevs.inventoryaccess.map.MapPatch;
import xyz.xenondevs.inventoryaccess.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class PlayerUtilsImpl implements PlayerUtils {
    
    private static final Method REGISTER_LISTENERS_METHOD = ReflectionUtils.getMethod(
        PlayerAdvancements.class,
        true,
        "ASRM(net/minecraft/server/PlayerAdvancements.registerListeners(Lnet/minecraft/server/ServerAdvancementManager;)V)",
        ServerAdvancementManager.class
    );
    
    @Override
    public void stopAdvancementListening(@NotNull Player player) {
        stopAdvancementListening(((CraftPlayer) player).getHandle());
    }
    
    @Override
    public void stopAdvancementListening(@NotNull Object player) {
        ((ServerPlayer) player).getAdvancements().stopListening();
    }
    
    @Override
    public void startAdvancementListening(@NotNull Player player) {
        startAdvancementListening(((CraftPlayer) player).getHandle());
    }
    
    @Override
    public void startAdvancementListening(@NotNull Object player) {
        PlayerAdvancements advancements = ((ServerPlayer) player).getAdvancements();
        ServerAdvancementManager manager = ((CraftServer) Bukkit.getServer()).getServer().getAdvancements();
        ReflectionUtils.invokeMethod(REGISTER_LISTENERS_METHOD, advancements, manager);
    }
    
    @Override
    public void sendMapUpdate(@NotNull Player player, int mapId, byte scale, boolean locked, @Nullable MapPatch mapPatch, @Nullable List<MapIcon> icons) {
        List<MapDecoration> decorations = icons != null ? icons.stream().map(this::toMapDecoration).collect(Collectors.toCollection(ArrayList::new)) : null;
        MapItemSavedData.MapPatch patch = toMapPatch(mapPatch);
        ClientboundMapItemDataPacket packet = new ClientboundMapItemDataPacket(mapId, scale, locked, decorations, patch);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
    
    private MapDecoration toMapDecoration(MapIcon icon) {
        return new MapDecoration(
            MapDecoration.Type.byIcon(icon.getType().getId()),
            icon.getX(), icon.getY(),
            icon.getRot(),
            icon.getComponent() != null ? InventoryUtilsImpl.createNMSComponent(icon.getComponent()) : null
        );
    }
    
    private MapItemSavedData.MapPatch toMapPatch(MapPatch patch) {
        if (patch == null) return null;
        
        return new MapItemSavedData.MapPatch(
            patch.getStartX(), patch.getStartY(),
            patch.getWidth(), patch.getHeight(),
            patch.getColors()
        );
    }
    
}
