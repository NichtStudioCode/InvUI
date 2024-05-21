package xyz.xenondevs.invui.animation.impl;

import java.util.List;
import java.util.Random;

/**
 * An animation that lets the slots pop up in a random order.
 */
public class RandomAnimation extends AbstractSoundAnimation {
    
    private final Random random = new Random();
    
    /**
     * Creates a new {@link RandomAnimation}.
     *
     * @param tickDelay The delay between each slot being shown.
     * @param sound     Whether a sound should be played when the slot is shown.
     */
    public RandomAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }
    
    @Override
    protected void handleFrame(int frame) {
        List<Integer> slots = getSlots();
        
        if (!slots.isEmpty()) {
            int slot = slots.get(random.nextInt(slots.size()));
            slots.remove(Integer.valueOf(slot));
            show(slot);
        } else finish();
    }
    
}
