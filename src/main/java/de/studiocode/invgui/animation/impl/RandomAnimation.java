package de.studiocode.invgui.animation.impl;

import de.studiocode.invgui.item.Item;

import java.util.List;
import java.util.Random;

/**
 * Lets the {@link Item}s pop up randomly.
 */
public class RandomAnimation extends SoundAnimation {
    
    private final Random random = new Random();
    
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
