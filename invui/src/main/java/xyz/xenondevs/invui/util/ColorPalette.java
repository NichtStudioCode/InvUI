package xyz.xenondevs.invui.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility for converting images into the color space used by maps in Minecraft.
 */
public final class ColorPalette {
    
    private static final byte[] colorCache;
    
    static {
        ByteArrayOutputStream out = new ByteArrayOutputStream(256 * 256 * 256);
        try (InputStream in = ColorPalette.class.getResourceAsStream("/colors.bin")) {
            assert in != null;
            in.transferTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        colorCache = out.toByteArray();
    }
    
    private ColorPalette() {}
    
    /**
     * Gets the closest map color to the given RGB values.
     *
     * @param red   The red value (0-255)
     * @param green The green value (0-255)
     * @param blue  The blue value (0-255)
     * @return The map color
     */
    public static byte getColor(int red, int green, int blue) {
        return colorCache[(red << 16) | (green << 8) | blue];
    }
    
    /**
     * Gets the closest map color to the given ARGB value.
     * Since map colors do not have alpha, any color with an alpha value less than 255 will return 0.
     *
     * @param argb The ARGB value
     * @return The map color
     */
    public static byte getColor(int argb) {
        if ((argb >> 24 & 0xFF) < 255) return 0;
        return colorCache[argb & 0xFFFFFF];
    }
    
    /**
     * Gets the closest map color to the given {@link Color}.
     *
     * @param color The color
     * @return The map color
     */
    public static byte getColor(Color color) {
        return getColor(color.getRGB());
    }
    
    /**
     * Gets the closest map color to the given {@link org.bukkit.Color}.
     *
     * @param color The color
     * @return The map color
     */
    public static byte getColor(org.bukkit.Color color) {
        if (color.getAlpha() < 255)
            return 0;
        return getColor(color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * Converts the given {@link BufferedImage} into a byte array of map colors.
     *
     * @param image The image to convert
     * @return The map colors
     */
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
