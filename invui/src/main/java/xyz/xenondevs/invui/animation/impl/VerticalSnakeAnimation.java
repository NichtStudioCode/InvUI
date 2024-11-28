package xyz.xenondevs.invui.animation.impl;

/**
 * An animation that moves like a snake in vertical direction,
 * starting from (0,0) then moving all the way down, one to the side,
 * all the way up and repeat.
 */
public class VerticalSnakeAnimation extends AbstractSoundAnimation {
    
    private int x;
    private int y;
    private boolean up;
    
    /**
     * Creates a new {@link VerticalSnakeAnimation}.
     * @param tickDelay The delay between each slot being shown.
     * @param sound Whether a sound should be played when the slot is shown.
     */
    public VerticalSnakeAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }
    
    @Override
    protected void handleFrame(int frame) {
        boolean slotShown = false;
        while (!slotShown) {
            int slotIndex = convToIndex(x, y);
            slotShown = getSlots().contains(slotIndex);
            if (slotShown)
                show(slotIndex);
            
            if (up) {
                if (y <= 0) {
                    x++;
                    up = false;
                } else y--;
            } else {
                if (y >= getHeight() - 1) {
                    x++;
                    up = true;
                } else y++;
            }
            
            if (x >= getWidth()) {
                finish();
                return;
            }
        }
    }
    
}
