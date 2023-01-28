package xyz.xenondevs.invui.animation.impl;

public class RowAnimation extends SoundAnimation {
    
    private int row;
    
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
