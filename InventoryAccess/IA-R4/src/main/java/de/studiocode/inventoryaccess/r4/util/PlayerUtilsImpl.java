package de.studiocode.inventoryaccess.r4.util;

import de.studiocode.inventoryaccess.abstraction.util.PlayerUtils;
import de.studiocode.inventoryaccess.map.MapPatch;
import de.studiocode.inventoryaccess.util.ReflectionUtils;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerUtilsImpl implements PlayerUtils {
    
    private static final Method REGISTER_LISTENERS_METHOD =
        ReflectionUtils.getMethod(AdvancementDataPlayer.class, true, "b", AdvancementDataWorld.class);
    
    @Override
    public void stopAdvancementListening(@NotNull Player player) {
        stopAdvancementListening(((CraftPlayer) player).getHandle());
    }
    
    @Override
    public void stopAdvancementListening(@NotNull Object player) {
        ((EntityPlayer) player).getAdvancementData().a(); // stops listening
    }
    
    @Override
    public void startAdvancementListening(@NotNull Player player) {
        startAdvancementListening(((CraftPlayer) player).getHandle());
    }
    
    @Override
    public void startAdvancementListening(@NotNull Object player) {
        AdvancementDataPlayer advancements = ((EntityPlayer) player).getAdvancementData();
        AdvancementDataWorld manager = ((CraftServer) Bukkit.getServer()).getServer().getAdvancementData();
        ReflectionUtils.invokeMethod(REGISTER_LISTENERS_METHOD, advancements, manager);
    }
    
    @Override
    public void sendMapUpdate(@NotNull Player player, int mapId, byte scale, boolean locked, @Nullable MapPatch mapPatch, @Nullable List<de.studiocode.inventoryaccess.map.MapIcon> icons) {
        List<MapIcon> decorations = icons != null
            ? icons.stream().map(this::toMapDecoration).collect(Collectors.toCollection(ArrayList::new))
            : new ArrayList<>();
        
        int width = 0;
        int height = 0;
        int startX = 0;
        int startY = 0;
        byte[] colors = new byte[0];
        if (mapPatch != null) {
            width = mapPatch.getWidth();
            height = mapPatch.getHeight();
            startX = mapPatch.getStartX();
            startY = mapPatch.getStartY();
            colors = mapPatch.getColors();
        }
        
        PacketPlayOutMap packet = new PacketPlayOutMap(mapId, scale, !decorations.isEmpty(), locked, decorations, colors, startX, startY, width, height);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
    
    private MapIcon toMapDecoration(de.studiocode.inventoryaccess.map.MapIcon icon) {
        return new MapIcon(
            MapIcon.Type.a(icon.getType().getId()),
            icon.getX(), icon.getY(),
            icon.getRot(),
            icon.getComponents() != null ? InventoryUtilsImpl.createNMSComponent(icon.getComponents()) : null
        );
    }
    
}
