package xyz.xenondevs.invui.animation.impl;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Abstract base class for animations that may use the entity pickup sound when displaying a slot.
 */
public abstract class AbstractSoundAnimation extends AbstractAnimation {
    
    /**
     * Creates a new {@link AbstractSoundAnimation}.
     *
     * @param tickDelay The delay between each frame
     * @param sound     Whether the entity pickup sound should be played when displaying a slot
     */
    public AbstractSoundAnimation(int tickDelay, boolean sound) {
        super(tickDelay);
        
        if (sound) {
            addShowHandler((frame, index) -> {
                    for (Player viewer : getCurrentViewers()) {
                        viewer.playSound(viewer.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    }
                }
            );
        }
    }
    
}
