package xyz.xenondevs.invui.internal.util;

import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.level.saveddata.maps.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.util.MapIcon;
import xyz.xenondevs.invui.util.MapPatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static xyz.xenondevs.invui.internal.util.ReflectionRegistry.PLAYER_ADVANCEMENTS_REGISTER_LISTENERS_METHOD;

public class PlayerUtils {
    
    public static void stopAdvancementListening(Player player) {
        ((CraftPlayer) player).getHandle().getAdvancements().stopListening();
    }
    
    public static void startAdvancementListening(Player player) {
        PlayerAdvancements advancements = ((CraftPlayer) player).getHandle().getAdvancements();
        ServerAdvancementManager manager = ((CraftServer) Bukkit.getServer()).getServer().getAdvancements();
        ReflectionUtils.invokeMethod(PLAYER_ADVANCEMENTS_REGISTER_LISTENERS_METHOD, advancements, manager);
    }
    
    public static void sendMapUpdate(Player player, int mapId, byte scale, boolean locked, @Nullable MapPatch mapPatch, @Nullable List<MapIcon> icons) {
        List<MapDecoration> decorations = icons != null ? icons.stream().map(PlayerUtils::toMapDecoration).collect(Collectors.toCollection(ArrayList::new)) : null;
        MapItemSavedData.MapPatch patch = toMapPatch(mapPatch);
        ClientboundMapItemDataPacket packet = new ClientboundMapItemDataPacket(new MapId(mapId), scale, locked, decorations, patch);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
    
    private static MapDecoration toMapDecoration(MapIcon icon) {
        return new MapDecoration(
            getDecorationTypeByIconType(icon.type()),
            icon.x(), icon.y(),
            icon.rot(),
            Optional.ofNullable(icon.component()).map(PaperAdventure::asVanilla)
        );
    }
    
    private static MapItemSavedData.@Nullable MapPatch toMapPatch(@Nullable MapPatch patch) {
        if (patch == null)
            return null;
        
        return new MapItemSavedData.MapPatch(
            patch.startX(), patch.startY(),
            patch.width(), patch.height(),
            patch.colors()
        );
    }
    
    private static Holder<MapDecorationType> getDecorationTypeByIconType(MapIcon.Type icon) {
        return switch (icon) {
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
