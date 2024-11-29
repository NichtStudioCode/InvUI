package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@ApiStatus.Internal
public interface GuiParent {
    
    void handleSlotElementUpdate(Gui child, int slotIndex);
    
}
