package de.studiocode.invgui.animation.impl;

import de.studiocode.invgui.item.Item;
import org.bukkit.Sound;

import java.util.List;

/**
 * Lets the {@link Item}s pop up index after index.
 */
public class IndexAnimation extends BaseAnimation {
    
    public IndexAnimation(int tickDelay, boolean sound) {
        super(tickDelay);
        
        if (sound) addShowHandler((frame, index) -> getPlayer().playSound(getPlayer().getLocation(),
            Sound.ENTITY_ITEM_PICKUP, 1, 1));
    }
    
    @Override
    protected void handleFrame(int frame) {
        List<Integer> slots = getSlots();
        if (!slots.isEmpty()) {
            show(slots.get(0));
            slots.remove(0);
        } else finished();
    }
    
}
