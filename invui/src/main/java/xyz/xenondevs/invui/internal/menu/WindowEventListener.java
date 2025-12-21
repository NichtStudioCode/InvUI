package xyz.xenondevs.invui.internal.menu;

import it.unimi.dsi.fastutil.ints.IntSet;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.xenondevs.invui.Click;

public interface WindowEventListener {
    
    void handleClick(int slot, Click click);
    
    void handleDrag(IntSet slots, ClickType mode);
    
    void handleBundleSelect(int slot, int bundleSlot);
    
    void handleClose(InventoryCloseEvent.Reason cause);
    
    void handlePong(int id);
    
    void updateSlots();
    
}
