package de.studiocode.invgui.animation.impl;

import java.util.List;

public class SplitSequentialAnimation extends BaseAnimation {
    
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
