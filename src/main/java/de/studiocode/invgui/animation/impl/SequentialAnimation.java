package de.studiocode.invgui.animation.impl;

import de.studiocode.invgui.item.Item;

import java.util.List;

/**
 * Lets the {@link Item}s pop up index after index.
 */
public class SequentialAnimation extends BaseAnimation {
    
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
