package xyz.xenondevs.invui.gui;

import java.util.function.Supplier;

class GuiSlotElementSupplier implements Supplier<SlotElement.GuiLink> {
    
    private final Gui gui;
    private int slot;
    
    public GuiSlotElementSupplier(Gui gui) {
        if (gui.getSize() <= 0)
            throw new IllegalArgumentException("Illegal gui size: " + gui.getSize());
        
        this.gui = gui;
    }
    
    @Override
    public SlotElement.GuiLink get() {
        var element = new SlotElement.GuiLink(gui, slot);
        slot = (slot + 1) % gui.getSize();
        return element;
    }
    
}
