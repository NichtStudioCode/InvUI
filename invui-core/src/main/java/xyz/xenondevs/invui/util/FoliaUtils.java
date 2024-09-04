package xyz.xenondevs.invui.util;

public class FoliaUtils {

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    // Delay ticks may not be <= 0 in Folia
    public static long getFoliaDelay(long delay) {
        return delay <= 0 ? 1 : delay;
    }
}
