package xyz.xenondevs.invui.animation.impl;

/**
 * An animation that shows the slots row by row,
 * from top to bottom.
 */
public class RowAnimation extends AbstractSoundAnimation {
    
    private int row;
    
    /**
     * Creates a new {@link RowAnimation}.
     * @param tickDelay The delay between each slot being shown.
     * @param sound Whether a sound should be played when the slot is shown.
     */
    public RowAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }
    
    @Override
    protected void handleFrame(int frame) {
        boolean showedSomething = false;
        
        while (!showedSomething && row < getHeight()) {
            for (int x = 0; x < getWidth(); x++) {
                int index = convToIndex(x, row);
                if (getSlots().contains(index)) {
                    show(index);
                    showedSomething = true;
                }
            }
            
            row++;
        }
        
        if (row >= getHeight()) finish();
    }
    
}
