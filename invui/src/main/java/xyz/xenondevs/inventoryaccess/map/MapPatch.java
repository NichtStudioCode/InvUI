package xyz.xenondevs.inventoryaccess.map;

import java.io.Serializable;

public class MapPatch implements Serializable {
    
    private final int startX;
    private final int startY;
    private final int width;
    private final int height;
    private final byte[] colors;
    
    public MapPatch(int startX, int startY, int width, int height, byte[] colors) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.colors = colors;
    }
    
    public int getStartX() {
        return startX;
    }
    
    public int getStartY() {
        return startY;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public byte[] getColors() {
        return colors;
    }
    
}
