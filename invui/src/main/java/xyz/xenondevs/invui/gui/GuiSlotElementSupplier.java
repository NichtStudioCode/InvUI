package xyz.xenondevs.invui.gui;

import java.util.ArrayList;
import java.util.List;

class GuiSlotElementSupplier implements SlotElementSupplier {
    
    private final Gui gui;
    private final int offsetX;
    private final int offsetY;
    
    public GuiSlotElementSupplier(Gui gui, int offsetX, int offsetY) {
        if (gui.getSize() <= 0)
            throw new IllegalArgumentException("Illegal gui size: " + gui.getSize());
        
        this.gui = gui;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    @Override
    public List<? extends SlotElement> generateSlotElements(List<? extends Slot> slots) {
        if (slots.isEmpty())
            return List.of();
        
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (Slot slot : slots) {
            if (slot.x() < minX) minX = slot.x();
            if (slot.y() < minY) minY = slot.y();
        }
        
        var elements = new ArrayList<SlotElement.GuiLink>();
        for (Slot slot : slots) {
            var guiX = slot.x() - minX + offsetX;
            var guiY = slot.y() - minY + offsetY;
            
            if (guiX < 0 || guiY < 0 || guiX >= gui.getWidth() || guiY >= gui.getHeight())
                throw new IndexOutOfBoundsException(
                    "Structure slot at (" + slot.x() + ", " + slot.y() + 
                    ") with offset (" + offsetX + ", " + offsetY +
                    ") is looking for slot (" + guiX + ", " + guiY +
                    "), which is out of bounds for a gui of size " + gui.getWidth() + "x" + gui.getHeight()
                );
            
            elements.add(new SlotElement.GuiLink(gui, guiY * gui.getWidth() + guiX));
        }
        
        return elements;
    }
    
}
