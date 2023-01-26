package de.studiocode.invui.map;

import de.studiocode.invui.util.IOUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ColorPalette {
    
    private static final byte[] colorCache;
    
    static {
        ByteArrayOutputStream out = new ByteArrayOutputStream(256 * 256 * 256);
        try {
            InputStream in = ColorPalette.class.getResourceAsStream("/colors.bin");
            IOUtils.copy(in, out, 8192);
        } catch (IOException e) {
            e.printStackTrace();
        }
        colorCache = out.toByteArray();
    }
    
    public static byte getColor(int red, int green, int blue) {
        return colorCache[(red << 16) | (green << 8) | blue];
    }
    
    public static byte getColor(int rgba) {
        if ((rgba >> 24 & 0xFF) < 255) return 0;
        return colorCache[rgba & 0xFFFFFF];
    }
    
    public static byte getColor(Color color) {
        return getColor(color.getRGB());
    }
    
    public static byte[] convertImage(BufferedImage image) {
        byte[] colors = new byte[image.getWidth() * image.getHeight()];
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                colors[x + y * image.getWidth()] = getColor(image.getRGB(x, y));
            }
        }
        
        return colors;
    }
    
}
