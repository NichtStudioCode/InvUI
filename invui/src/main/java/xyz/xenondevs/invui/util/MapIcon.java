package xyz.xenondevs.invui.util;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

public record MapIcon(
    Type type,
    byte x, byte y, byte rot,
    @Nullable Component component
)
{
    
    public MapIcon(Type type, int x, int y, int rot, @Nullable Component component) {
        this(type, (byte) (x - 128), (byte) (y - 128), (byte) rot, null);
    }
    
    public enum Type {
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
        
        Type(boolean renderedOnFrame, boolean trackCount) {
            this(renderedOnFrame, trackCount, -1);
        }
        
        Type(boolean renderedOnFrame, boolean trackCount, int mapColor) {
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
