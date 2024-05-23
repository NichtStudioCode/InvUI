package xyz.xenondevs.invui.util;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ColorPalette {
    
    private static final byte @NotNull[] colorCache;
    
    static {
        ByteArrayOutputStream out = new ByteArrayOutputStream(256 * 256 * 256);
        try(InputStream in = ColorPalette.class.getResourceAsStream("/colors.bin")) {
            assert in != null;
            in.transferTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    
    public static byte getColor(@NotNull Color color) {
        return getColor(color.getRGB());
    }
    
    public static byte[] convertImage(@NotNull BufferedImage image) {
        byte[] colors = new byte[image.getWidth() * image.getHeight()];
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                colors[x + y * image.getWidth()] = getColor(image.getRGB(x, y));
            }
        }
        
        return colors;
    }
    
}
