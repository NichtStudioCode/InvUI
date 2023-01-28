package xyz.xenondevs.invui.animation.impl;

public class ColumnAnimation extends SoundAnimation {
    
    private int column;
    
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
