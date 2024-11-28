package xyz.xenondevs.inventoryaccess.map;

import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.BungeeComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;

public class MapIcon {
    
    private final MapIconType type;
    private final byte x;
    private final byte y;
    private final byte rot;
    private final ComponentWrapper component;
    
    public MapIcon(@NotNull MapIconType type, int x, int y, int rot, @Nullable ComponentWrapper component) {
        this.type = type;
        this.x = (byte) (x - 128);
        this.y = (byte) (y - 128);
        this.rot = (byte) rot;
        this.component = component;
    }
    
    public MapIcon(@NotNull MapIconType type, int x, int y, int rot, @Nullable BaseComponent[] component) {
        this(type, x, y, rot, new BungeeComponentWrapper(component));
    }
    
    public MapIcon(MapIconType type, byte x, byte y, byte rot) {
        this(type, x, y, rot, (ComponentWrapper) null);
    }
    
    @NotNull
    public MapIconType getType() {
        return type;
    }
    
    public byte getX() {
        return x;
    }
    
    public byte getY() {
        return y;
    }
    
    public byte getRot() {
        return rot;
    }
    
    @Nullable
    public ComponentWrapper getComponent() {
        return component;
    }
    
    public enum MapIconType {
        WHITE_ARROW(false, true),
        GREEN_ARROW(true, true),
        RED_ARROW(false, true),
        BLUE_ARROW(false, true),
        WHITE_CROSS(true, false),
        RED_POINTER(true, false),
        WHITE_CIRCLE(false, true),
        SMALL_WHITE_CIRCLE(false, true),
        MANSION(true, false, 5393476),
        TEMPLE(true, false, 3830373),
        WHITE_BANNER(true, true),
        ORANGE_BANNER(true, true),
        MAGENTA_BANNER(true, true),
        LIGHT_BLUE_BANNER(true, true),
        YELLOW_BANNER(true, true),
        LIME_BANNER(true, true),
        PINK_BANNER(true, true),
        GRAY_BANNER(true, true),
        LIGHT_GRAY_BANNER(true, true),
        CYAN_BANNER(true, true),
        PURPLE_BANNER(true, true),
        BLUE_BANNER(true, true),
        BROWN_BANNER(true, true),
        GREEN_BANNER(true, true),
        RED_BANNER(true, true),
        BLACK_BANNER(true, true),
        RED_CROSS(true, false);
        
        private final byte id;
        private final boolean renderedOnFrame;
        private final int mapColor;
        private final boolean trackCount;
        
        MapIconType(boolean renderedOnFrame, boolean trackCount) {
            this(renderedOnFrame, trackCount, -1);
        }
        
        MapIconType(boolean renderedOnFrame, boolean trackCount, int mapColor) {
            this.trackCount = trackCount;
            this.id = (byte) this.ordinal();
            this.renderedOnFrame = renderedOnFrame;
            this.mapColor = mapColor;
        }
        
        public byte getId() {
            return this.id;
        }
        
        public boolean isRenderedOnFrame() {
            return this.renderedOnFrame;
        }
        
        public boolean hasMapColor() {
            return this.mapColor >= 0;
        }
        
        public int getMapColor() {
            return this.mapColor;
        }
        
        public boolean shouldTrackCount() {
            return this.trackCount;
        }
        
    }
    
}
