package de.studiocode.invgui.animation.impl;

public class HorizontalSnakeAnimation extends SoundAnimation {
    
    private int x;
    private int y;
    private boolean left;
    
    public HorizontalSnakeAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }
    
    @Override
    protected void handleFrame(int frame) {
        boolean slotShown = false;
        while (!slotShown) {
            int slotIndex = convToIndex(x, y);
            if (slotShown = getSlots().contains(slotIndex)) show(slotIndex);
            
            if (left) {
                if (x <= 0) {
                    y++;
                    left = false;
                } else x--;
            } else {
                if (x >= getWidth() - 1) {
                    y++;
                    left = true;
                } else x++;
            }
            
            if (y >= getHeight()) {
                finish();
                return;
            }
        }
    }
    
}
