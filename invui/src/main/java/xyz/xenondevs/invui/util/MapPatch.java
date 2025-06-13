package xyz.xenondevs.invui.util;

/**
 * A color section of a map.
 *
 * @param startX The x-coordinate of the top-left corner of the patch.
 * @param startY The y-coordinate of the top-left corner of the patch.
 * @param width  The width of the patch.
 * @param height The height of the patch.
 * @param colors The colors of the patch.
 * @see ColorPalette
 */
public record MapPatch(int startX, int startY, int width, int height, byte[] colors) {
    
    /**
     * Creates a new {@link MapPatch} with the given parameters.
     *
     * @param startX The x-coordinate of the top-left corner of the patch.
     * @param startY The y-coordinate of the top-left corner of the patch.
     * @param width  The width of the patch.
     * @param height The height of the patch.
     * @param colors The colors of the patch.
     */
    public MapPatch {
        if (colors.length != width * height)
            throw new IllegalArgumentException("Invalid colors array length, expected " + width * height + " but got " + colors.length);
    }
    
}
