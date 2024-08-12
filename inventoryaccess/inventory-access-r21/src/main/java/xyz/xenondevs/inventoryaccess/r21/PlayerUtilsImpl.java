package xyz.xenondevs.inventoryaccess.r21;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.maps.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
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
import java.util.Optional;
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
        ClientboundMapItemDataPacket packet = new ClientboundMapItemDataPacket(new MapId(mapId), scale, locked, decorations, patch);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
    
    private MapDecoration toMapDecoration(MapIcon icon) {
        return new MapDecoration(
            getDecorationTypeByIconType(icon.getType()),
            icon.getX(), icon.getY(),
            icon.getRot(),
            Optional.ofNullable(icon.getComponent()).map(InventoryUtilsImpl::createNMSComponent)
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
    
    private Holder<MapDecorationType> getDecorationTypeByIconType(MapIcon.MapIconType icon) {
        return switch(icon) {
            case WHITE_ARROW -> MapDecorationTypes.PLAYER;
            case GREEN_ARROW -> MapDecorationTypes.FRAME;
            case RED_ARROW -> MapDecorationTypes.RED_MARKER;
            case BLUE_ARROW -> MapDecorationTypes.BLUE_MARKER;
            case WHITE_CROSS -> MapDecorationTypes.TARGET_X;
            case RED_POINTER -> MapDecorationTypes.TARGET_POINT;
            case WHITE_CIRCLE -> MapDecorationTypes.PLAYER_OFF_MAP;
            case SMALL_WHITE_CIRCLE -> MapDecorationTypes.PLAYER_OFF_LIMITS;
            case MANSION -> MapDecorationTypes.WOODLAND_MANSION;
            case TEMPLE -> MapDecorationTypes.JUNGLE_TEMPLE;
            case WHITE_BANNER -> MapDecorationTypes.WHITE_BANNER;
            case ORANGE_BANNER -> MapDecorationTypes.ORANGE_BANNER;
            case MAGENTA_BANNER -> MapDecorationTypes.MAGENTA_BANNER;
            case LIGHT_BLUE_BANNER -> MapDecorationTypes.LIGHT_BLUE_BANNER;
            case YELLOW_BANNER -> MapDecorationTypes.YELLOW_BANNER;
            case LIME_BANNER -> MapDecorationTypes.LIME_BANNER;
            case PINK_BANNER -> MapDecorationTypes.PINK_BANNER;
            case GRAY_BANNER -> MapDecorationTypes.GRAY_BANNER;
            case LIGHT_GRAY_BANNER -> MapDecorationTypes.LIGHT_GRAY_BANNER;
            case CYAN_BANNER -> MapDecorationTypes.CYAN_BANNER;
            case PURPLE_BANNER -> MapDecorationTypes.PURPLE_BANNER;
            case BLUE_BANNER -> MapDecorationTypes.BLUE_BANNER;
            case BROWN_BANNER -> MapDecorationTypes.BROWN_BANNER;
            case GREEN_BANNER -> MapDecorationTypes.GREEN_BANNER;
            case RED_BANNER -> MapDecorationTypes.RED_BANNER;
            case BLACK_BANNER -> MapDecorationTypes.BLACK_BANNER;
            case RED_CROSS -> MapDecorationTypes.RED_X;
        };
    }
    
}
