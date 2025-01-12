package xyz.xenondevs.invui.gui;

import java.util.function.Supplier;

class GuiSlotElementSupplier implements Supplier<SlotElement> {
    
    private final Gui gui;
    private int slot;
    
    public GuiSlotElementSupplier(Gui gui) {
        this.gui = gui;
    }
    
    @Override
    public SlotElement get() {
        if (slot >= gui.getSize())
            throw new IllegalStateException("No more slots available");
        
        return new SlotElement.GuiLink(gui, slot++);
    }
    
}
