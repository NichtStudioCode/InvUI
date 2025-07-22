package xyz.xenondevs.invui.gui;

class GuiSlotElementSupplier implements ResettableSlotElementSupplier<SlotElement.GuiLink> {
    
    private final Gui gui;
    private int slot;
    
    public GuiSlotElementSupplier(Gui gui) {
        if (gui.getSize() <= 0)
            throw new IllegalArgumentException("Illegal gui size: " + gui.getSize());
        
        this.gui = gui;
    }
    
    @Override
    public SlotElement.GuiLink get() {
        if (slot >= gui.getSize())
            throw new IllegalStateException("No more slots available");
        
        return new SlotElement.GuiLink(gui, slot++);
    }
    
    @Override
    public void reset() {
        slot = 0;
    }
    
}
