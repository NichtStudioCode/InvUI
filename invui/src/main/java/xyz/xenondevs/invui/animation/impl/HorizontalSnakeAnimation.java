package xyz.xenondevs.invui.animation.impl;

/**
 * An animation that moves like a snake in horizontal direction,
 * starting from (0,0) then moving all the way right, one down,
 * all the way left and repeat.
 */
public class HorizontalSnakeAnimation extends AbstractSoundAnimation {
    
    private int x;
    private int y;
    private boolean left;
    
    /**
     * Creates a new {@link HorizontalSnakeAnimation}.
     * @param tickDelay The delay between each slot being shown.
     * @param sound Whether a sound should be played when the slot is shown.
     */
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
