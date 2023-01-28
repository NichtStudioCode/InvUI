package de.studiocode.inventoryaccess.util;

import org.bukkit.Bukkit;

public class VersionUtils {
    
    private static final int major;
    private static final int minor;
    private static final int patch;
    
    static {
        String version = Bukkit.getVersion();
        version = version.substring(version.indexOf(':') + 2, version.lastIndexOf(')'));
        
        int[] parts = toMajorMinorPatch(version);
        major = parts[0];
        minor = parts[1];
        patch = parts[2];
    }
    
    /**
     * Converts a version string like "1.17.1" or "1.17" to an array of three ints.
     *
     * @param version The version to check against
     * @return The version as an array of three ints.
     * @throws NumberFormatException If an invalid version string is provided.
     */
    public static int[] toMajorMinorPatch(String version) {
        String[] parts = version.split("\\.");
        
        int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        
        return new int[] {major, minor, patch};
    }
    
    /**
     * Returns if the given version is higher or equal to the one currently running on the server.
     *
     * @param version The version to check against
     * @return If the given version is running on this server or a newer one.
     */
    public static boolean isHigherOrEqualThanServer(String version) {
        return isHigherOrEqualThanServer(toMajorMinorPatch(version));
    }
    
    /**
     * Returns if the given version is higher or equal to the one currently running on the server.
     *
     * @param version The version to check against
     * @return If the given version is running on this server or a newer one.
     * @throws IllegalArgumentException If the version array does not have a size of 3
     */
    public static boolean isHigherOrEqualThanServer(int... version) {
        if (version.length != 3)
            throw new IllegalArgumentException("Version array must have a size of 3");
        
        return version[0] > major
            || (version[0] == major && version[1] > minor)
            || (version[0] == major && version[1] == minor && version[2] >= patch);
    }
    
    /**
     * Returns if the server is on this or a newer version of Minecraft.
     *
     * @param version The version to check against
     * @return If the server is running this exact or a newer version of Minecraft.
     */
    public static boolean isServerHigherOrEqual(String version) {
        return isServerHigherOrEqual(toMajorMinorPatch(version));
    }
    
    /**
     * Returns if the server is on this or a newer version of Minecraft.
     *
     * @param version The version to check against
     * @return If the server is running this exact or a newer version of Minecraft.
     * @throws IllegalArgumentException If the version array does not have a size of 3
     */
    public static boolean isServerHigherOrEqual(int... version) {
        if (version.length != 3)
            throw new IllegalArgumentException("Version array must have a size of 3");
        
        return major > version[0]
            || (major == version[0] && minor > version[1])
            || (major == version[0] && minor == version[1] && patch >= version[2]);
    }
    
}
