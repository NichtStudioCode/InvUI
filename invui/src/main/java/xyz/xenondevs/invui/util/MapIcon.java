package xyz.xenondevs.invui.util;

import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

/**
 * An icon on a map.
 *
 * @param type      The type of icon
 * @param x         The x-coordinate of the icon, from 0 to 256, where 0 is the top left corner
 * @param y         The y-coordinate of the icon, from 0 to 256, where 0 is the top left corner
 * @param rot       The rotation of the icon, from 0 to 15 in 22.5° steps
 * @param component The text displayed under the icon
 */
public record MapIcon(
    Type type,
    int x, int y, int rot,
    @Nullable Component component
)
{
    
    /**
     * The map icon type.
     */
    public enum Type {
        /**
         * <img src="https://i.imgur.com/PzCWlhY.png">
         */
        WHITE_ARROW,
        /**
         * <img src="https://i.imgur.com/oPOvp5O.png">
         */
        GREEN_ARROW,
        /**
         * <img src="https://i.imgur.com/pD8EkLs.png">
         */
        RED_ARROW,
        /**
         * <img src="https://i.imgur.com/1Sd0xkw.png">
         */
        BLUE_ARROW,
        /**
         * <img src="https://i.imgur.com/AYqFuZD.png">
         */
        WHITE_CROSS,
        /**
         * <img src="https://i.imgur.com/we4oJeI.png">
         */
        RED_POINTER,
        /**
         * <img src="https://i.imgur.com/M7Zk2Vw.png">
         */
        WHITE_CIRCLE,
        /**
         * <img src="https://i.imgur.com/r8bNePl.png">
         */
        SMALL_WHITE_CIRCLE,
        /**
         * <img src="https://i.imgur.com/3gSxXAA.png">
         */
        MANSION,
        /**
         * <img src="https://i.imgur.com/1YsDNS1.png">
         */
        TEMPLE,
        /**
         * <img src="https://i.imgur.com/DQBgkm2.png">
         */
        WHITE_BANNER,
        /**
         * <img src="https://i.imgur.com/6toX8W7.png">
         */
        ORANGE_BANNER,
        /**
         * <img src="https://i.imgur.com/AKZtCrr.png">
         */
        MAGENTA_BANNER,
        /**
         * <img src="https://i.imgur.com/kfpiTv2.png">
         */
        LIGHT_BLUE_BANNER,
        /**
         * <img src="https://i.imgur.com/v1QktUa.png">
         */
        YELLOW_BANNER,
        /**
         * <img src="https://i.imgur.com/fPkRIw1.png">
         */
        LIME_BANNER,
        /**
         * <img src="https://i.imgur.com/Job7ICS.png">
         */
        PINK_BANNER,
        /**
         * <img src="https://i.imgur.com/fK0XlZE.png">
         */
        GRAY_BANNER,
        /**
         * <img src="https://i.imgur.com/Vwcaoqo.png">
         */
        LIGHT_GRAY_BANNER,
        /**
         * <img src="https://i.imgur.com/NZ1Qcf1.png">
         */
        CYAN_BANNER,
        /**
         * <img src="https://i.imgur.com/5XpJ5ao.png">
         */
        PURPLE_BANNER,
        /**
         * <img src="https://i.imgur.com/kihvjrG.png">
         */
        BLUE_BANNER,
        /**
         * <img src="https://i.imgur.com/M9PE7hL.png">
         */
        BROWN_BANNER,
        /**
         * <img src="https://i.imgur.com/8URfvnG.png">
         */
        GREEN_BANNER,
        /**
         * <img src="https://i.imgur.com/yMR3wLB.png">
         */
        RED_BANNER,
        /**
         * <img src="https://i.imgur.com/jW07hij.png">
         */
        BLACK_BANNER,
        /**
         * <img src="https://i.imgur.com/GmWo4uJ.png">
         */
        RED_CROSS
    }
    
}
