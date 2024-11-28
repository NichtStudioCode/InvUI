package xyz.xenondevs.invui.animation.impl;

import java.util.List;

/**
 * An animation that shows the slots sequentially from both directions.
 */
public class SplitSequentialAnimation extends AbstractSoundAnimation {
    
    /**
     * Creates a new {@link SplitSequentialAnimation}.
     * @param tickDelay The delay between each slot being shown.
     * @param sound Whether a sound should be played when the slot is shown.
     */
    public SplitSequentialAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }
    
    @Override
    protected void handleFrame(int frame) {
        List<Integer> slots = getSlots();
        
        int i = slots.get(0);
        int i2 = slots.get(slots.size() - 1);
        
        show(i, i2);
        
        if (slots.size() <= 2) {
            finish();
            return;
        }
        
        slots.remove(0);
        slots.remove(slots.size() - 1);
    }
    
}
