package xyz.xenondevs.invui.gui;

final class NormalGuiImpl extends AbstractGui {
    
    public NormalGuiImpl(int width, int height) {
        super(width, height);
    }
    
    public NormalGuiImpl(Structure structure) {
        super(structure.getWidth(), structure.getHeight());
        applyStructure(structure);
    }
    
    static final class Builder extends AbstractBuilder<NormalGuiImpl, Builder> {
        
        @Override
        public NormalGuiImpl build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new NormalGuiImpl(structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
