package xyz.xenondevs.invui.internal.menu;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.saveddata.maps.*;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.network.PacketListener;
import xyz.xenondevs.invui.internal.util.MathUtils;
import xyz.xenondevs.invui.util.MapIcon;
import xyz.xenondevs.invui.util.MapPatch;

import java.util.List;
import java.util.Optional;

import static io.papermc.paper.datacomponent.item.MapId.mapId;

/**
 * A packet-based cartography table menu.
 */
public class CustomCartographyMenu extends CustomContainerMenu {
    
    private static final int MAP_SIZE = 128;
    private int mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
    
    /**
     * Creates a new {@link CustomCartographyMenu} for the specified viewer.
     *
     * @param player The player that will view the menu
     */
    public CustomCartographyMenu(Player player) {
        super(MenuType.CARTOGRAPHY_TABLE, player);
    }
    
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void setItem(int slot, @Nullable ItemStack item) {
        if (slot == 0 && item != null) {
            var clone = item.clone();
            clone.setData(DataComponentTypes.MAP_ID, mapId(mapId));
            super.setItem(slot, clone);
        } else {
            super.setItem(slot, item);
        }
    }
    
    public void resetMap() {
        mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
        setItem(0, CraftItemStack.asCraftMirror(items.getFirst()));
    }
    
    public void sendMapUpdate(@Nullable MapPatch patch, @Nullable List<MapIcon> icons) {
        if (patch != null && (patch.startX() + patch.width() > MAP_SIZE || patch.startY() + patch.height() > MAP_SIZE))
            throw new IllegalArgumentException("Map patch is out of bounds");
        
        var packet = new ClientboundMapItemDataPacket(
            new MapId(mapId),
            (byte) 0,
            false,
            toNmsDecorations(icons),
            toNmsMapPatch(patch)
        );
        PacketListener.getInstance().injectOutgoing(player, packet);
    }
    
    private static @Nullable List<MapDecoration> toNmsDecorations(@Nullable List<MapIcon> icons) {
        if (icons == null)
            return null;
        
        return icons.stream()
            .map(CustomCartographyMenu::toNmsDecoration)
            .toList();
    }
    
    private static MapDecoration toNmsDecoration(MapIcon icon) {
        return new MapDecoration(
            getDecorationTypeByIconType(icon.type()),
            icon.x(), icon.y(),
            icon.rot(),
            Optional.ofNullable(icon.component()).map(PaperAdventure::asVanilla)
        );
    }
    
    private static MapItemSavedData.@Nullable MapPatch toNmsMapPatch(@Nullable MapPatch patch) {
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
