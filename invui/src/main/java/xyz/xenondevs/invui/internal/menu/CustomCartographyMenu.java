package xyz.xenondevs.invui.internal.menu;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.saveddata.maps.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.network.PacketListener;
import xyz.xenondevs.invui.internal.util.ItemUtils2;
import xyz.xenondevs.invui.internal.util.MathUtils;
import xyz.xenondevs.invui.window.CartographyWindow;
import xyz.xenondevs.invui.window.CartographyWindow.MapIcon;
import xyz.xenondevs.invui.window.CartographyWindow.MapPatch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static io.papermc.paper.datacomponent.item.MapId.mapId;

/**
 * A packet-based cartography table menu.
 */
public class CustomCartographyMenu extends CustomContainerMenu {
    
    private static final int MAP_SIZE = 128;
    private int mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
    private byte[] canvas = new byte[MAP_SIZE * MAP_SIZE];
    private final Set<MapDecoration> decorations = new HashSet<>();
    private CartographyWindow.View view = CartographyWindow.View.NORMAL;
    
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
        } else if (slot == 1 && item != null) {
            var targetType = switch (view) {
                case NORMAL -> Material.STONE;
                case SMALL -> Material.PAPER;
                case DUPLICATE -> Material.MAP;
                case LOCK -> Material.GLASS_PANE;
            };
            super.setItem(slot, ItemUtils2.asType(item, targetType));
        } else {
            super.setItem(slot, item);
        }
    }
    
    @Override
    public void open(Component title) {
        super.open(title);
        sendMapUpdate(new MapItemSavedData.MapPatch(0, 0, MAP_SIZE, MAP_SIZE, canvas), decorations);
    }
    
    public void setView(CartographyWindow.View view) {
        this.view = view;
        setItem(1, CraftItemStack.asCraftMirror(items.get(1)));
    }
    
    public void setIcons(Collection<? extends MapIcon> icons, boolean sendUpdate) {
        decorations.clear();
        decorations.addAll(icons.stream()
            .map(CustomCartographyMenu::toNmsDecoration)
            .toList());
        
        if (sendUpdate)
            sendMapUpdate(null, decorations);
    }
    
    public void applyPatch(MapPatch patch, boolean sendUpdate) {
        if (patch.startX() + patch.width() > MAP_SIZE || patch.startY() + patch.height() > MAP_SIZE)
            throw new IllegalArgumentException("Map patch is out of bounds");
        
        for (int y = 0; y < patch.height(); y++) {
            for (int x = 0; x < patch.width(); x++) {
                int i = (patch.startY() + y) * MAP_SIZE + patch.startX() + x;
                canvas[i] = patch.colors()[y * patch.width() + x];
            }
        }
        
        if (sendUpdate)
            sendMapUpdate(toNmsMapPatch(patch), null);
    }
    
    public void resetMap() {
        mapId = -MathUtils.RANDOM.nextInt(Integer.MAX_VALUE);
        canvas = new byte[MAP_SIZE * MAP_SIZE];
        decorations.clear();
        
        setItem(0, CraftItemStack.asCraftMirror(items.getFirst()));
    }
    
    private void sendMapUpdate(MapItemSavedData.@Nullable MapPatch patch, @Nullable Collection<MapDecoration> icons) {
        var packet = new ClientboundMapItemDataPacket(new MapId(mapId), (byte) 0, false, icons, patch);
        PacketListener.getInstance().injectOutgoing(player, packet);
    }
    
    private static @Nullable Collection<MapDecoration> toNmsDecorations(@Nullable Collection<MapIcon> icons) {
        if (icons == null)
            return null;
        
        return icons.stream()
            .map(CustomCartographyMenu::toNmsDecoration)
            .toList();
    }
    
    private static MapDecoration toNmsDecoration(MapIcon icon) {
        return new MapDecoration(
            getDecorationTypeByIconType(icon.type()),
            (byte) (icon.x() - 128), (byte) (icon.y() - 128),
            (byte) icon.rot(),
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
