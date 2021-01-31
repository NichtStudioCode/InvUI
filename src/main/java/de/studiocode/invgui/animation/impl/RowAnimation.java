package de.studiocode.invgui.animation.impl;

public class RowAnimation extends SoundAnimation {
    
    private int row;
    
    public RowAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }
    
    @Override
    protected void handleFrame(int frame) {
        boolean showedSomething = false;
        
        while (!showedSomething || row == getHeight() - 1) {
            for (int x = 0; x < getWidth(); x++) {
                int index = convToIndex(x, row);
                if (getSlots().contains(index)) {
                    show(index);
                    showedSomething = true;
                }
            }
            
            row++;
        }
        
        if (frame == getHeight() - 1) finish();
    }
    
}
