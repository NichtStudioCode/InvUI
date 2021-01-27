package de.studiocode.invgui.animation.impl;

import org.bukkit.Sound;

public abstract class SoundAnimation extends BaseAnimation {
    
    public SoundAnimation(int tickDelay, boolean sound) {
        super(tickDelay);
    
        if (sound) addShowHandler((frame, index) -> getPlayer().playSound(getPlayer().getLocation(),
            Sound.ENTITY_ITEM_PICKUP, 1, 1));
    }
    
}
