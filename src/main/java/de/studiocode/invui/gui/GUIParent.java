package de.studiocode.invui.gui;

public interface GUIParent {
    
    /**
     * Called by the child {@link GUI} to report an update of a {@link SlotElement}.
     * 
     * @param child The child {@link GUI} whose {@link SlotElement} has changed
     * @param slotIndex The slot index of the changed {@link SlotElement} in the child {@link GUI}
     */
    void handleSlotElementUpdate(GUI child, int slotIndex);
    
}
