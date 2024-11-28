package xyz.xenondevs.invui.animation.impl;

import java.util.List;

/**
 * An animation that shows the slots sequentially, ordered by their index.
 */
public class SequentialAnimation extends AbstractSoundAnimation {
    
    /**
     * Creates a new {@link SequentialAnimation}.
     * @param tickDelay The delay between each slot being shown.
     * @param sound Whether a sound should be played when the slot is shown.
     */
    public SequentialAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }
    
    @Override
    protected void handleFrame(int frame) {
        List<Integer> slots = getSlots();
        if (!slots.isEmpty()) {
            show(slots.get(0));
            slots.remove(0);
        } else finish();
    }
    
}
