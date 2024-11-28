package xyz.xenondevs.invui.animation.impl;

/**
 * An animation that shows the slots column by column,
 * from left to right.
 */
public class ColumnAnimation extends AbstractSoundAnimation {
    
    private int column;
    
    /**
     * Creates a new {@link ColumnAnimation}.
     * @param tickDelay The delay between each slot being shown.
     * @param sound Whether a sound should be played when the slot is shown.
     */
    public ColumnAnimation(int tickDelay, boolean sound) {
        super(tickDelay, sound);
    }
    
    @Override
    protected void handleFrame(int frame) {
        boolean showedSomething = false;
        
        while (!showedSomething && column < getWidth()) {
            for (int y = 0; y < getHeight(); y++) {
                int index = convToIndex(column, y);
                if (getSlots().contains(index)) {
                    show(index);
                    showedSomething = true;
                }
            }
            
            column++;
        }
        
        if (column >= getWidth()) finish();
    }
    
}
