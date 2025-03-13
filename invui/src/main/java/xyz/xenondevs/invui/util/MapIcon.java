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
         * <img src="https://i.imgur.com/PzCWlhY.png" alt="image of white arrow"/>
         */
        WHITE_ARROW,
        /**
         * <img src="https://i.imgur.com/oPOvp5O.png" alt="image of green arrow"/>
         */
        GREEN_ARROW,
        /**
         * <img src="https://i.imgur.com/pD8EkLs.png" alt="image of red arrow"/>
         */
        RED_ARROW,
        /**
         * <img src="https://i.imgur.com/1Sd0xkw.png" alt="image of blue arrow"/>
         */
        BLUE_ARROW,
        /**
         * <img src="https://i.imgur.com/AYqFuZD.png" alt="image of white cross"/>
         */
        WHITE_CROSS,
        /**
         * <img src="https://i.imgur.com/we4oJeI.png" alt="image of red pointer"/>
         */
        RED_POINTER,
        /**
         * <img src="https://i.imgur.com/M7Zk2Vw.png" alt="image of white circle"/>
         */
        WHITE_CIRCLE,
        /**
         * <img src="https://i.imgur.com/r8bNePl.png" alt="image of small white circle"/>
         */
        SMALL_WHITE_CIRCLE,
        /**
         * <img src="https://i.imgur.com/3gSxXAA.png" alt="image of mansion"/>
         */
        MANSION,
        /**
         * <img src="https://i.imgur.com/1YsDNS1.png" alt="image of temple"/>
         */
        TEMPLE,
        /**
         * <img src="https://i.imgur.com/DQBgkm2.png" alt="image of white banner"/>
         */
        WHITE_BANNER,
        /**
         * <img src="https://i.imgur.com/6toX8W7.png" alt="image of orange banner"/>
         */
        ORANGE_BANNER,
        /**
         * <img src="https://i.imgur.com/AKZtCrr.png" alt="image of magenta banner"/>
         */
        MAGENTA_BANNER,
        /**
         * <img src="https://i.imgur.com/kfpiTv2.png" alt="image of light blue banner"/>
         */
        LIGHT_BLUE_BANNER,
        /**
         * <img src="https://i.imgur.com/v1QktUa.png" alt="image of yellow banner"/>
         */
        YELLOW_BANNER,
        /**
         * <img src="https://i.imgur.com/fPkRIw1.png" alt="image of lime banner"/>
         */
        LIME_BANNER,
        /**
         * <img src="https://i.imgur.com/Job7ICS.png" alt="image of pink banner"/>
         */
        PINK_BANNER,
        /**
         * <img src="https://i.imgur.com/fK0XlZE.png" alt="image of gray banner"/>
         */
        GRAY_BANNER,
        /**
         * <img src="https://i.imgur.com/Vwcaoqo.png" alt="image of light gray banner"/>
         */
        LIGHT_GRAY_BANNER,
        /**
         * <img src="https://i.imgur.com/NZ1Qcf1.png" alt="image of cyan banner"/>
         */
        CYAN_BANNER,
        /**
         * <img src="https://i.imgur.com/5XpJ5ao.png" alt="image of purple banner"/>
         */
        PURPLE_BANNER,
        /**
         * <img src="https://i.imgur.com/kihvjrG.png" alt="image of blue banner"/>
         */
        BLUE_BANNER,
        /**
         * <img src="https://i.imgur.com/M9PE7hL.png" alt="image of brown banner"/>
         */
        BROWN_BANNER,
        /**
         * <img src="https://i.imgur.com/8URfvnG.png" alt="image of green banner"/>
         */
        GREEN_BANNER,
        /**
         * <img src="https://i.imgur.com/yMR3wLB.png" alt="image of red banner"/>
         */
        RED_BANNER,
        /**
         * <img src="https://i.imgur.com/jW07hij.png" alt="image of black banner"/>
         */
        BLACK_BANNER,
        /**
         * <img src="https://i.imgur.com/GmWo4uJ.png" alt="image of red X"/>
         */
        RED_CROSS
    }
    
}
